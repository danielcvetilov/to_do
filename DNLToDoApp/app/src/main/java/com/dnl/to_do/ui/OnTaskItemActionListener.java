package com.dnl.to_do.ui;

import com.dnl.to_do.data.Task;

public interface OnTaskItemActionListener extends OnItemActionListener<Task> {
    void onItemStateChange(Task task);
}
