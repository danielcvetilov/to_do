package com.dnl.to_do.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TaskGroupDao extends IDao<TaskGroup> {
    @Query("SELECT  " +
            "    tg.*, " +
            "    COALESCE(completed.count, 0) AS completedCount, " +
            "    COALESCE(total.count, 0) AS totalCount " +
            "FROM task_group AS tg " +
            "LEFT JOIN " +
            "( " +
            "    SELECT  " +
            "        COUNT(*) AS count, " +
            "        t.group_id " +
            "    FROM task AS t  " +
            "    WHERE t.state = 1 " +
            "    GROUP BY t.group_id " +
            ") AS completed ON completed.group_id = tg.id " +
            "LEFT JOIN " +
            "( " +
            "    SELECT  " +
            "        COUNT(*) AS count, " +
            "        t.group_id " +
            "    FROM task AS t  " +
            "    GROUP BY t.group_id " +
            ") AS total ON total.group_id = tg.id " +
            "ORDER BY tg.position ")
    LiveData<List<TaskGroupWithProgress>> getAll();

    @Query("SELECT  " +
            "    tg.*, " +
            "    COALESCE(completed.count, 0) AS completedCount, " +
            "    COALESCE(total.count, 0) AS totalCount " +
            "FROM task_group AS tg " +
            "LEFT JOIN " +
            "( " +
            "    SELECT  " +
            "        COUNT(*) AS count, " +
            "        t.group_id " +
            "    FROM task AS t  " +
            "    WHERE t.state = 1 " +
            "    GROUP BY t.group_id " +
            ") AS completed ON completed.group_id = tg.id " +
            "LEFT JOIN " +
            "( " +
            "    SELECT  " +
            "        COUNT(*) AS count, " +
            "        t.group_id " +
            "    FROM task AS t  " +
            "    GROUP BY t.group_id " +
            ") AS total ON total.group_id = tg.id " +
            "WHERE tg.id = :id " +
            "LIMIT 1 ")
    LiveData<TaskGroupWithProgress> getById(int id);
}
