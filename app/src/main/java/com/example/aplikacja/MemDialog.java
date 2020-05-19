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


public class MemDialog extends AppCompatDialogFragment {
    private EditText upup, updown, downup, downdown;
    private ExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mem_layout_dialog, null);

        builder.setView(view)
                .setTitle("Mem")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        listener.addText(upup.getText().toString().toUpperCase(), updown.getText().toString().toUpperCase(),
                                downup.getText().toString().toUpperCase(), downdown.getText().toString().toUpperCase());
                    }
                });

        upup = view.findViewById(R.id.upup);
        updown = view.findViewById(R.id.updown);
        downup = view.findViewById(R.id.downup);
        downdown = view.findViewById(R.id.downdown);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    public interface ExampleDialogListener {
        void addText(String s1, String s2, String s3, String s4);
    }
}