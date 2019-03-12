package com.dnl.to_do.data;

import android.arch.persistence.room.Embedded;

public class TaskGroupWithProgress {
    @Embedded
    public TaskGroup record;

    public int completedCount;
    public int totalCount;
}
