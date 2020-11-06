package com.fireflyest.btcontrol.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.adapter.BtinfoList.BtinfoItemAdapter;
import com.fireflyest.btcontrol.bean.Btinfo;
import com.fireflyest.btcontrol.bean.Mode;
import com.fireflyest.btcontrol.bt.BtManager;

import java.util.ArrayList;
import java.util.List;

public class BtinfoDialog extends DialogFragment implements View.OnClickListener  {

    public interface NoticeDialogListener {
        void onDialogDisconnectClick();
    }

    public NoticeDialogListener listener;

    private List<Btinfo> btinfos = new ArrayList<>();

    private String address = "";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_info_bluetooth, null);

        Bundle argument = getArguments();
        if(argument != null){
            address = argument.getString("address");
        }

        BluetoothGatt gatt = BtManager.getBtController().getGatt(address);

        for (BluetoothGattService service : gatt.getServices()){
            btinfos.add(new Btinfo("#服务 ", String.format("%s", service.getUuid())));
            for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                btinfos.add(new Btinfo("特征", String.format("%s", characteristic.getUuid())));
                btinfos.add(new Btinfo(
                        String.format("特征属性: 0x%s", Integer.toHexString(characteristic.getProperties())),
                        String.format("特征作用: %s", getProprty(characteristic.getProperties()))));
            }
            btinfos.add(new Btinfo(" ", " "));
        }

        TextView done = view.findViewById(R.id.dialog_bluetooth_done);
        done.setOnClickListener(this);
        TextView disconnect = view.findViewById(R.id.dialog_bluetooth_disconnect);
        disconnect.setOnClickListener(this);

        RecyclerView infoList = view.findViewById(R.id.dialog_bluetooth_info);
        infoList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        BtinfoItemAdapter itemAdapter = new BtinfoItemAdapter(btinfos);
        infoList.setAdapter(itemAdapter);


        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_bluetooth_done:
                this.dismiss();
                break;
            case R.id.dialog_bluetooth_disconnect:
                BtManager.getBtController().closeConnect(address);
                listener.onDialogDisconnectClick();
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


    private String  getProprty(int p){
        switch (p){
            case 0x01:
                return "广播";
            case 0x02:
                return "读取";
            case 0x04:
                return "无回应写入";
            case 0x08:
                return "写入";
            case 0x10:
                return "反馈通知";
            case 0x20:
                return "指示";
            case 0x40:
                return "带签名写入";
            case 0x80:
                return "扩展属性";
            default:
                return "未知";
        }
    }

}
