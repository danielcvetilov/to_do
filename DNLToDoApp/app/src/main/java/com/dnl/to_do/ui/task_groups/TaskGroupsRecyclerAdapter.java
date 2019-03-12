package com.dnl.to_do.ui.task_groups;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dnl.to_do.R;
import com.dnl.to_do.data.TaskGroup;
import com.dnl.to_do.data.TaskGroupWithProgress;
import com.dnl.to_do.ui.ItemTouchHelperAdapter;
import com.dnl.to_do.ui.ItemTouchHelperCallback;
import com.dnl.to_do.ui.OnItemActionListener;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskGroupsRecyclerAdapter extends RecyclerView.Adapter<TaskGroupsRecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private final OnItemActionListener<TaskGroup> onItemActionListener;
    private final List<TaskGroupWithProgress> records;

    TaskGroupsRecyclerAdapter(List<TaskGroupWithProgress> records, OnItemActionListener<TaskGroup> onItemActionListener) {
        this.records = records;
        this.onItemActionListener = onItemActionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(records.get(position), onItemActionListener);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(records, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(records, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperCallback.ViewHolder {

        @BindView(R.id.name_tv)
        TextView nameTv;

        @BindView(R.id.progress_tv)
        TextView progressTv;

        @BindView(R.id.handle_view)
        View handleView;

        @BindView(R.id.options_menu_btn)
        View optionsMenuBtn;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(TaskGroupWithProgress group, OnItemActionListener<TaskGroup> onItemActionListener) {
            nameTv.setText(group.record.name);

            String progress = String.format(Locale.getDefault(), itemView.getContext().getString(R.string.task_groups_progress_pattern), group.completedCount, group.totalCount);
            progressTv.setText(progress);

            optionsMenuBtn.setOnClickListener(v -> onItemActionListener.onOptionMenuClick(group.record));

            handleView.setOnClickListener(view -> onItemActionListener.onClick(group.record));
            handleView.setOnLongClickListener(v -> {
                onItemActionListener.onDragStart(this);
                return true;
            });
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryLight));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
        }
    }

}
