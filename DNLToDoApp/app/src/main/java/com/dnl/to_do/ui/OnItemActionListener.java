package com.dnl.to_do.ui;

import android.support.v7.widget.RecyclerView;

public interface OnItemActionListener<T> {
    void onClick(T item);

    void onOptionMenuClick(T item);

    void onDragStart(RecyclerView.ViewHolder viewHolder);
}
