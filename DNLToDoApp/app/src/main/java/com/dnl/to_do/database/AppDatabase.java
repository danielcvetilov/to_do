package com.dnl.to_do.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.dnl.to_do.data.Task;
import com.dnl.to_do.data.TaskDao;
import com.dnl.to_do.data.TaskGroup;
import com.dnl.to_do.data.TaskGroupDao;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {Task.class, TaskGroup.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    public abstract TaskGroupDao taskGroupDao();

    public void upsertTasks(List<Task> records) {
        List<Task> toInsert = new ArrayList<>();
        List<Task> toUpdate = new ArrayList<>();

        for (Task record : records) {
            if (record.id == 0)
                toInsert.add(record);
            else
                toUpdate.add(record);
        }

        taskDao().insert(toInsert);
        taskDao().update(toUpdate);
    }

    public void upsertTaskGroups(List<TaskGroup> records) {
        List<TaskGroup> toInsert = new ArrayList<>();
        List<TaskGroup> toUpdate = new ArrayList<>();

        for (TaskGroup record : records) {
            if (record.id == 0)
                toInsert.add(record);
            else
                toUpdate.add(record);
        }

        taskGroupDao().insert(toInsert);
        taskGroupDao().update(toUpdate);
    }
}





