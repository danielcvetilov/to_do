package com.dnl.to_do.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.dnl.to_do.MainApp;
import com.dnl.to_do.R;
import com.dnl.to_do.data.TaskState;
import com.dnl.to_do.ui.task_groups.TaskGroupsActivity;


public class DNLWidgetProvider extends AppWidgetProvider {
    public static final String ITEM_TASK_ACTION = "item_task_action";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(
                    context.getPackageName(),
                    R.layout.widget
            );

            Intent openAppIntent = new Intent(context, TaskGroupsActivity.class);
            openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_open_app, openAppPendingIntent);

            Intent intent = new Intent(context, DNLRemoteViewsService.class);
            views.setRemoteAdapter(R.id.widget_listview, intent);

            final Intent onItemClick = new Intent(context, DNLWidgetProvider.class);
            onItemClick.setAction(ITEM_TASK_ACTION);
            onItemClick.setData(Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0, onItemClick,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_listview, onClickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, DNLWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null)
            return;

        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, DNLWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_listview);
        }

        if (action.equals(ITEM_TASK_ACTION)) {
            int taskId = intent.getIntExtra("task_id", -1);
            if (taskId <= 0)
                return;

            Toast.makeText(context, R.string.tasks_task_completed_successfully, Toast.LENGTH_LONG).show();
            new UpdateTaskStateTask(taskId).execute();

            sendRefreshBroadcast(context);
        }


        super.onReceive(context, intent);
    }

    private static class UpdateTaskStateTask extends AsyncTask<Void, Void, Boolean> {

        private final int recordId;

        UpdateTaskStateTask(int recordId) {
            this.recordId = recordId;
        }

        @Override
        protected Boolean doInBackground(Void... objs) {
            MainApp.instance.db.taskDao().updateTaskState(recordId, TaskState.DONE);
            return true;
        }
    }
}
