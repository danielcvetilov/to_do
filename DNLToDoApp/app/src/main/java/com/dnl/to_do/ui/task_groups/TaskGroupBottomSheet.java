package com.dnl.to_do.ui.task_groups;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dnl.to_do.R;
import com.dnl.to_do.data.TaskGroup;
import com.dnl.to_do.ui.common.UIBottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskGroupBottomSheet extends UIBottomSheetDialogFragment {
    private static final String RECORD_TAG = "record_tag";

    @BindView(R.id.caption_tv)
    TextView captionTv;

    @BindView(R.id.rename_btn)
    View renameBtn;

    @BindView(R.id.delete_btn)
    View deleteBtn;

    private TaskGroup record;
    private OnBottomSheetActionListener onBottomSheetActionListener;

    public static TaskGroupBottomSheet newInstance(TaskGroup record) {
        Bundle args = new Bundle();
        args.putParcelable(RECORD_TAG, record);
        TaskGroupBottomSheet fragment = new TaskGroupBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.botom_sheet_task_group, container, false);

        ButterKnife.bind(this, contentView);

        Bundle bundle = getArguments();
        if (bundle == null || bundle.isEmpty())
            return contentView;

        record = bundle.getParcelable(RECORD_TAG);

        if (getActivity() instanceof OnBottomSheetActionListener) {
            onBottomSheetActionListener = (OnBottomSheetActionListener) getActivity();
        }

        captionTv.setText(record.name);

        renameBtn.setOnClickListener(view -> {
            dismiss();
            onBottomSheetActionListener.onRecordRename(record);
        });

        deleteBtn.setOnClickListener(view -> {
            dismiss();
            onBottomSheetActionListener.onRecordDelete(record);
        });

        return contentView;
    }

    public interface OnBottomSheetActionListener {
        void onRecordRename(TaskGroup record);

        void onRecordDelete(TaskGroup record);
    }
}
