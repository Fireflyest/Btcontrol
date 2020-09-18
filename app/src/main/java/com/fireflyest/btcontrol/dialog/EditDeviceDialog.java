package com.fireflyest.btcontrol.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bean.Mode;
import com.fireflyest.btcontrol.util.CalendarUtil;

public class EditDeviceDialog extends DialogFragment implements View.OnClickListener  {

    public interface NoticeDialogListener {
        void onDialogDoneClick(String name, long progress);
        void onDialogDeleteClick();
    }

    public NoticeDialogListener listener;

    private EditText nameEdit;
    private EditText progressEdit;

    private String name = "";
    private long progress;
    private long create;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_device, null);

        Bundle argument = getArguments();
        if(argument != null){
            name = argument.getString("name");
            progress = argument.getLong("progress");
            create = argument.getLong("create");
        }

        nameEdit = view.findViewById(R.id.dialog_device_name);
        progressEdit = view.findViewById(R.id.dialog_device_progress);
        TextView createText = view.findViewById(R.id.dialog_device_create);
        TextView add = view.findViewById(R.id.dialog_device_add);
        TextView close = view.findViewById(R.id.dialog_device_close);
        ImageButton deleteButton = view.findViewById(R.id.dialog_device_delete);
        TextView deleteText = view.findViewById(R.id.dialog_device_delete_text);


        nameEdit.setText(name);
        if(0 != progress) progressEdit.setText(String.format("%s", progress));
        createText.setText(CalendarUtil.convertTime(create));

        add.setOnClickListener(this);
        close.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        deleteText.setOnClickListener(this);

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_device_close:
                this.dismiss();
                break;
            case R.id.dialog_device_add:
                String name = nameEdit.getText().toString();
                long progress = Long.parseLong(progressEdit.getText().toString());
                listener.onDialogDoneClick(name, progress);
                this.dismiss();
                break;
            case R.id.dialog_device_delete_text:
            case R.id.dialog_device_delete:
                listener.onDialogDeleteClick();
                this.dismiss();
                break;
            default:
        }
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(this.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
