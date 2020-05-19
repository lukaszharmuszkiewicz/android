package com.example.aplikacja;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;


public class ErozjaDialog extends AppCompatDialogFragment {
    private EditText level;
    private ErozjaDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Poziom")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String password = level.getText().toString();
                      //  listener.applyTexts(level.getText().toString());
                        listener.erozja(level.getText().toString());
                    }
                });

        level = view.findViewById(R.id.level);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ErozjaDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    public interface ErozjaDialogListener {

        void erozja(String border);
    }
}