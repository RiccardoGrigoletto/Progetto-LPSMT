package com.example.marco.utils;

import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marco.arcview.R;

/**
 * Created by Marco on 06/10/2017.
 */

public class AlertDialogOnClickListener implements View.OnClickListener {
    private final Dialog dialog;

    public AlertDialogOnClickListener(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onClick(View view) {
        String mValue = ((EditText)view.findViewById(R.id.editText2)).getText().toString();
        if(mValue != null){
            dialog.dismiss();
        }else{
            Toast.makeText(view.getContext(), "Invalid data", Toast.LENGTH_SHORT).show();
        }
    }
}
