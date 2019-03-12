package com.dnl.to_do.widget;


import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.dnl.to_do.MainApp;
import com.dnl.to_do.R;
import com.dnl.to_do.data.Task;
import com.dnl.to_do.data.TaskState;

import java.util.ArrayList;
import java.util.List;

import static com.dnl.to_do.widget.DNLWidgetProvider.ITEM_TASK_ACTION;

class DNLRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final List<Task> tasks = new ArrayList<>();

    DNLRemoteViewsFactory(Context applicationContext) {
        context = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();

        List<Task> dbRecords = MainApp.instance.db.taskDao().getByState(TaskState.TODO);
        tasks.clear();

        if (dbRecords != null)
            tasks.addAll(dbRecords);

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        Task task;
        try {
            task = tasks.get(position);
            if (task == null)
                return rv;

            rv.setTextViewText(R.id.name_tv, task.name);

            final Intent completeTaskIntent = new Intent();
            completeTaskIntent.setAction(ITEM_TASK_ACTION);
            final Bundle completeTaskBundle = new Bundle();
            completeTaskBundle.putInt("task_id", task.id);
            completeTaskIntent.putExtras(completeTaskBundle);
            rv.setOnClickFillInIntent(R.id.task_state_btn, completeTaskIntent);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
