package com.fireflyest.btcontrol.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bean.Mode;

public class AddModeDialog extends DialogFragment implements View.OnClickListener  {

    public interface NoticeDialogListener {
        void onDialogAddClick(Mode mode);
    }

    public NoticeDialogListener listener;

    private NumberPicker picker;
    private EditText name;
    private EditText desc;

    private int id = 0;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_mode, null);

        picker = view.findViewById(R.id.dialog_mode_code);
        name = view.findViewById(R.id.dialog_mode_name);
        desc = view.findViewById(R.id.dialog_mode_desc);
        TextView add = view.findViewById(R.id.dialog_mode_add);
        TextView close = view.findViewById(R.id.dialog_mode_close);

        add.setOnClickListener(this);
        close.setOnClickListener(this);


        picker.setMinValue(301);
        picker.setMaxValue(310);

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_mode_close:
                this.dismiss();
                break;
            case R.id.dialog_mode_add:
                Mode mode = new Mode();
                mode.setName(name.getText().toString());
                mode.setDesc(desc.getText().toString());
                mode.setCode(String.valueOf(picker.getValue()));
                listener.onDialogAddClick(mode);
                this.dismiss();
                break;
            default:
        }
    }

    @Override
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
