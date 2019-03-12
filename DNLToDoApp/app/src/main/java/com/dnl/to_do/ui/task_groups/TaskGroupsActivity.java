package com.dnl.to_do.ui.task_groups;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.dnl.to_do.MainApp;
import com.dnl.to_do.R;
import com.dnl.to_do.data.TaskGroup;
import com.dnl.to_do.data.TaskGroupWithProgress;
import com.dnl.to_do.ui.ItemTouchHelperCallback;
import com.dnl.to_do.ui.OnItemActionListener;
import com.dnl.to_do.ui.common.TextInputDialog;
import com.dnl.to_do.ui.common.UserConfirmationDialog;
import com.dnl.to_do.ui.on_boarding.OnBoardingActivity;
import com.dnl.to_do.ui.tasks.TasksActivity;
import com.dnl.to_do.utils.TaskUtils;
import com.dnl.to_do.widget.DNLWidgetProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskGroupsActivity extends AppCompatActivity implements TaskGroupBottomSheet.OnBottomSheetActionListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.create_record_fab)
    View createRecordFab;

    private ItemTouchHelper itemTouchHelper;
    private TaskGroupsRecyclerAdapter adapter;

    private final ArrayList<TaskGroupWithProgress> data = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_groups);

        ButterKnife.bind(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new TaskGroupsRecyclerAdapter(data, onItemActionListener);

        recyclerView.setAdapter(adapter);
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter) {
            @Override
            public void onMovedCompleted() {
                updateItemsPositions();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        createRecordFab.setOnClickListener(v -> showAddGroupDialog());

        MainApp.instance.db.taskGroupDao().getAll().observe(this, newRecords -> {
            data.clear();

            if (newRecords != null)
                data.addAll(newRecords);

            TaskUtils.postOnMainAfter(200, () -> adapter.notifyDataSetChanged());
        });

        showOnBoardingActivityIfNeeded();
    }

    private void showOnBoardingActivityIfNeeded() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(OnBoardingActivity.COMPLETED_ON_BOARDING_TAG, false)) {
            startActivity(new Intent(this, OnBoardingActivity.class));
        }
    }

    private final OnItemActionListener<TaskGroup> onItemActionListener = new OnItemActionListener<TaskGroup>() {
        @Override
        public void onClick(TaskGroup item) {
            Intent intent = new Intent(TaskGroupsActivity.this, TasksActivity.class);
            intent.putExtra(TasksActivity.TASK_GROUP_ID_TAG, item.id);
            startActivity(intent);
        }

        @Override
        public void onOptionMenuClick(TaskGroup item) {
            TaskGroupBottomSheet bottomSheet = TaskGroupBottomSheet.newInstance(item);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        }

        @Override
        public void onDragStart(RecyclerView.ViewHolder viewHolder) {
            itemTouchHelper.startDrag(viewHolder);
        }
    };

    private void showAddGroupDialog() {
        TextInputDialog.show(this, null, value -> {
            TaskGroup group = new TaskGroup();
            group.name = value;
            new UpsertAsyncTask(group).execute();

        });
    }

    private void showEditGroupDialog(TaskGroup group) {
        TextInputDialog.show(this, group.name, value -> {
            group.name = value;
            new UpsertAsyncTask(group).execute();
        });
    }

    private void updateItemsPositions() {
        List<TaskGroup> taskGroupsToUpdate = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            TaskGroupWithProgress t = data.get(i);
            t.record.position = i;

            taskGroupsToUpdate.add(t.record);
        }

        new UpsertAsyncTask(taskGroupsToUpdate).execute();
    }

    @Override
    public void onRecordRename(TaskGroup record) {
        showEditGroupDialog(record);
    }

    @Override
    public void onRecordDelete(TaskGroup record) {
        UserConfirmationDialog.show(this, R.string.task_groups_message_delete, () -> new DeleteAsyncTask(TaskGroupsActivity.this, record).execute());
    }


    private static class UpsertAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private final List<TaskGroup> records;

        UpsertAsyncTask(TaskGroup record) {
            this.records = new ArrayList<>();
            records.add(record);
        }

        UpsertAsyncTask(List<TaskGroup> records) {
            this.records = records;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            MainApp.instance.db.upsertTaskGroups(records);
            return true;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private final WeakReference<Context> context;
        private final TaskGroup record;

        DeleteAsyncTask(Context context, TaskGroup record) {
            this.context = new WeakReference<>(context);
            this.record = record;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            MainApp.instance.db.taskDao().deleteByGroupId(record.id);
            MainApp.instance.db.taskGroupDao().delete(record);

            if (context.get() != null)
                DNLWidgetProvider.sendRefreshBroadcast(context.get());

            return true;
        }
    }
}
