package com.dnl.to_do.ui.common;


import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dnl.to_do.R;

public class TextInputDialog {
    public static void show(Context context, String initialValue, InputListener inputListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(R.string.text_input_dialog_enter_name);

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.item_padding);

        final EditText editText = new EditText(context);
        editText.setSelectAllOnFocus(true);

        if (initialValue != null)
            editText.setText(initialValue);

        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(editTextParams);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setPadding(padding, padding, padding, padding);
        linearLayout.addView(editText);

        editText.setOnFocusChangeListener((v, hasFocus) -> editText.post(() -> {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }));
        editText.requestFocus();

        alert.setView(linearLayout);

        alert.setPositiveButton(R.string.text_input_dialog_ok, (dialog, whichButton) -> {
            String value = editText.getText().toString();
            if (inputListener != null)
                inputListener.onInput(value);
        });

        alert.setNegativeButton(R.string.text_input_dialog_cancel, null);

        alert.show();
    }

    public interface InputListener {
        void onInput(String value);
    }
}
