package com.dnl.to_do.ui.tasks;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dnl.to_do.R;
import com.dnl.to_do.data.Task;
import com.dnl.to_do.data.TaskState;
import com.dnl.to_do.ui.ItemTouchHelperAdapter;
import com.dnl.to_do.ui.ItemTouchHelperCallback;
import com.dnl.to_do.ui.OnTaskItemActionListener;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TasksRecyclerAdapter extends RecyclerView.Adapter<TasksRecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private final OnTaskItemActionListener onItemActionListener;
    private final List<Task> records;

    TasksRecyclerAdapter(List<Task> records, OnTaskItemActionListener onItemActionListener) {
        this.records = records;
        this.onItemActionListener = onItemActionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperCallback.ViewHolder {

        @BindView(R.id.name_tv)
        TextView taskNameTv;

        @BindView(R.id.task_state_btn)
        ImageButton taskStateBtn;

        @BindView(R.id.handle_view)
        View handleView;

        @BindView(R.id.options_menu_btn)
        View optionsMenuBtn;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(Task task, OnTaskItemActionListener onItemActionListener) {
            boolean isTaskDone = task.state == TaskState.DONE;

            taskNameTv.setText(task.name);
            strikeThruText(taskNameTv, isTaskDone);

            if (isTaskDone)
                taskStateBtn.setImageResource(R.drawable.ic_check_box);
            else
                taskStateBtn.setImageResource(R.drawable.ic_check_box_outline);

            optionsMenuBtn.setOnClickListener(v -> onItemActionListener.onOptionMenuClick(task));

            handleView.setOnClickListener(view -> onItemActionListener.onClick(task));
            handleView.setOnLongClickListener(v -> {
                onItemActionListener.onDragStart(this);
                return true;
            });

            taskStateBtn.setOnClickListener(v -> onItemActionListener.onItemStateChange(task));
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryLight));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
        }

        private void strikeThruText(TextView textView, boolean isTaskDone) {
            if (isTaskDone) {
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.colorSecondaryText));
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.colorPrimaryText));
                textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    }
}
