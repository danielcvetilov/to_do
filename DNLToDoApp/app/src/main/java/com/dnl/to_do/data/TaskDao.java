package com.dnl.to_do.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TaskDao extends IDao<Task> {
    @Query("SELECT * FROM task WHERE group_id = :taskGroupId ORDER BY position ")
    LiveData<List<Task>> getByGroupId(int taskGroupId);

    @Query("SELECT * FROM task WHERE state = :state")
    List<Task> getByState(int state);

    @Query("DELETE FROM task WHERE group_id = :groupId")
    void deleteByGroupId(int groupId);

    @Query("UPDATE task SET state = :state WHERE id = :id")
    void updateTaskState(int id, int state);
}
