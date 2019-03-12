package com.dnl.to_do.ui.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.dnl.to_do.R;


public class UserConfirmationDialog {
    public static void show(Context context, int messageResId, final UserConfirmationDialogResultListener resultListener) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    resultListener.onOk();
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder = builder.setTitle(R.string.user_confirmation_dialog_are_you_sure)
                .setPositiveButton(R.string.user_confirmation_dialog_yes, dialogClickListener)
                .setNegativeButton(R.string.user_confirmation_dialog_no, null);


        if (messageResId != 0) {
            builder.setMessage(messageResId);
        }

        builder.show();
    }

    public interface UserConfirmationDialogResultListener {
        void onOk();
    }
}
