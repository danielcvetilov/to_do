package com.dnl.to_do.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dnl.to_do.MainApp;
import com.dnl.to_do.R;
import com.dnl.to_do.data.Task;
import com.dnl.to_do.data.TaskGroupWithProgress;
import com.dnl.to_do.data.TaskState;
import com.dnl.to_do.database.AppDatabase;
import com.dnl.to_do.ui.ItemTouchHelperCallback;
import com.dnl.to_do.ui.OnTaskItemActionListener;
import com.dnl.to_do.ui.common.TextInputDialog;
import com.dnl.to_do.ui.common.UserConfirmationDialog;
import com.dnl.to_do.utils.TaskUtils;
import com.dnl.to_do.widget.DNLWidgetProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TasksActivity extends AppCompatActivity implements TaskBottomSheet.OnBottomSheetActionListener {
    public static final String TASK_GROUP_ID_TAG = "task_group_id_tag";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.create_record_fab)
    View createRecordFab;

    @BindView(R.id.task_group_name_tv)
    TextView taskGroupNameTv;

    @BindView(R.id.task_group_progress_tv)
    TextView taskGroupProgressTv;

    private ItemTouchHelper itemTouchHelper;
    private TasksRecyclerAdapter adapter;

    private int taskGroupId;
    private TaskGroupWithProgress taskGroupWithProgress;
    private final ArrayList<Task> data = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        ButterKnife.bind(this);

        boolean hasParams;
        if (savedInstanceState != null)
            hasParams = InitBySavedInstance(savedInstanceState);
        else
            hasParams = InitByNewInstance();

        if (!hasParams) {
            Toast.makeText(this, R.string.tasks_no_selected_task_group, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new TasksRecyclerAdapter(data, onItemActionListener);

        recyclerView.setAdapter(adapter);
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter) {
            @Override
            public void onMovedCompleted() {
                updateItemsPositions();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        createRecordFab.setOnClickListener(v -> showAddGroupDialog());

        AppDatabase db = MainApp.instance.db;
        db.taskGroupDao().getById(taskGroupId).observe(this, newRecord -> {
            taskGroupWithProgress = newRecord;
            updateHeader();
        });

        db.taskDao().getByGroupId(taskGroupId).observe(this, newRecords -> {
            data.clear();

            if (newRecords != null)
                data.addAll(newRecords);

            TaskUtils.postOnMainAfter(200, () -> adapter.notifyDataSetChanged());
        });
    }

    private void updateHeader() {
        if (taskGroupWithProgress == null)
            return;

        taskGroupNameTv.setText(taskGroupWithProgress.record.name);

        String progressText = String.format(Locale.getDefault(), getString(R.string.task_groups_progress_pattern), taskGroupWithProgress.completedCount, taskGroupWithProgress.totalCount);
        taskGroupProgressTv.setText(progressText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(TASK_GROUP_ID_TAG, taskGroupId);
    }

    private boolean InitBySavedInstance(Bundle savedInstanceState) {
        if (!savedInstanceState.containsKey(TASK_GROUP_ID_TAG))
            return false;

        taskGroupId = savedInstanceState.getInt(TASK_GROUP_ID_TAG, 0);
        return taskGroupId > 0;
    }

    private boolean InitByNewInstance() {
        Intent intent = getIntent();
        if (intent == null)
            return false;

        if (!intent.hasExtra(TASK_GROUP_ID_TAG))
            return false;

        taskGroupId = intent.getIntExtra(TASK_GROUP_ID_TAG, 0);
        return taskGroupId > 0;
    }

    private final OnTaskItemActionListener onItemActionListener = new OnTaskItemActionListener() {
        @Override
        public void onItemStateChange(Task task) {
            if (task.state == TaskState.TODO)
                task.state = TaskState.DONE;
            else
                task.state = TaskState.TODO;

            if (task.state == TaskState.DONE)
                Toast.makeText(TasksActivity.this, R.string.tasks_task_completed_successfully, Toast.LENGTH_LONG).show();

            new UpsertAsyncTask(TasksActivity.this, task).execute();
        }

        @Override
        public void onClick(Task item) {

        }

        @Override
        public void onOptionMenuClick(Task item) {
            TaskBottomSheet bottomSheet = TaskBottomSheet.newInstance(item);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        }

        @Override
        public void onDragStart(RecyclerView.ViewHolder viewHolder) {
            itemTouchHelper.startDrag(viewHolder);
        }

    };

    private void showAddGroupDialog() {
        TextInputDialog.show(this, null, value -> {
            Task record = new Task();
            record.groupId = taskGroupId;
            record.name = value;
            new UpsertAsyncTask(TasksActivity.this, record).execute();
        });
    }

    private void updateItemsPositions() {
        for (int i = 0; i < data.size(); i++) {
            Task t = data.get(i);
            t.position = i;
        }

        new UpsertAsyncTask(TasksActivity.this, data).execute();
    }

    @Override
    public void onRecordRename(Task record) {
        TextInputDialog.show(this, record.name, value -> {
            record.name = value;
            new UpsertAsyncTask(TasksActivity.this, record).execute();
        });
    }

    @Override
    public void onRecordDelete(Task record) {
        UserConfirmationDialog.show(this, R.string.tasks_message_delete, () -> new DeleteAsyncTask(TasksActivity.this, record).execute());
    }


    private static class UpsertAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private final WeakReference<Context> context;
        private final List<Task> records;

        UpsertAsyncTask(Context context, Task record) {
            this.context = new WeakReference<>(context);
            this.records = new ArrayList<>();
            records.add(record);
        }

        UpsertAsyncTask(Context context, List<Task> records) {
            this.context = new WeakReference<>(context);
            this.records = records;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            MainApp.instance.db.upsertTasks(records);

            if (context.get() != null)
                DNLWidgetProvider.sendRefreshBroadcast(context.get());

            return true;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private final WeakReference<Context> context;
        private final Task record;

        DeleteAsyncTask(Context context, Task record) {
            this.context = new WeakReference<>(context);
            this.record = record;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            MainApp.instance.db.taskDao().delete(record);

            if (context.get() != null)
                DNLWidgetProvider.sendRefreshBroadcast(context.get());

            return true;
        }
    }

}
