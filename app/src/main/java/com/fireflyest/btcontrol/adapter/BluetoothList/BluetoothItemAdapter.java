package com.fireflyest.btcontrol.adapter.BluetoothList;

import android.bluetooth.BluetoothClass;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.btcontrol.ConnectActivity;
import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bean.Bluetooth;
import com.fireflyest.btcontrol.layout.VerticalBar;
import com.fireflyest.btcontrol.util.AnimateUtil;
import com.fireflyest.btcontrol.util.ConvertUtil;

import java.util.List;

public class BluetoothItemAdapter extends RecyclerView.Adapter<BluetoothItemAdapter.ViewHolder> {


    private List<Bluetooth> bluetooths;
    private Handler handler;

    static class ViewHolder extends RecyclerView.ViewHolder{

        Resources resource;
        TextView name;
        TextView address;
        ProgressBar rssi;
        TextView connect;
        ImageView type;

        private ViewHolder(@NonNull View view) {
            super(view);
            resource = view.getResources();
            name = view.findViewById(R.id.item_bluetooth_name);
            address = view.findViewById(R.id.item_bluetooth_address);
            rssi = view.findViewById(R.id.item_bluetooth_rssi);
            connect = view.findViewById(R.id.item_bluetooth_connect);
            type = view.findViewById(R.id.item_bluetooth_type);
        }
    }

    public BluetoothItemAdapter(List<Bluetooth> bluetooths, Handler handler) {
        this.bluetooths = bluetooths;
        this.handler = handler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Bluetooth bluetooth = bluetooths.get(position);
        holder.name.setText(bluetooth.getName());
        holder.address.setText(bluetooth.getAddress());
        int rssi = 140+bluetooth.getRssi();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.rssi.setProgress(rssi, true);
        }else {
            holder.rssi.setProgress(rssi);
        }
//        Log.e("tag", bluetooth.getName() + bluetooth.getType());

        switch (bluetooth.getType()){
            case BluetoothClass.Device.Major.PHONE:
                holder.type.setImageResource(R.drawable.ic_phone);
                break;
            case BluetoothClass.Device.Major.COMPUTER:
                holder.type.setImageResource(R.drawable.ic_computer);
                break;
            case BluetoothClass.Device.Major.NETWORKING:
                holder.type.setImageResource(R.drawable.ic_networking);
                break;
            case BluetoothClass.Device.Major.WEARABLE:
                holder.type.setImageResource(R.drawable.ic_wearable);
                break;
            case BluetoothClass.Device.Major.HEALTH:
                holder.type.setImageResource(R.drawable.ic_health);
                break;
            case BluetoothClass.Device.Major.TOY:
                holder.type.setImageResource(R.drawable.ic_toy);
                break;
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                holder.type.setImageResource(R.drawable.ic_audio);
                break;
            case BluetoothClass.Device.Major.PERIPHERAL:
                holder.type.setImageResource(R.drawable.ic_peripheral);
                break;
            case 0x1F00:
                if(bluetooth.getName().contains("Mi Smart Band")) {
                    holder.type.setImageResource(R.drawable.ic_wearable);
                }else {
                    holder.type.setImageResource(R.drawable.ic_bluetooth);
                }
                break;
            default:
                holder.type.setImageResource(R.drawable.ic_bluetooth);
                break;
        }
        holder.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = handler.obtainMessage(ConnectActivity.START_CONNECTION);
                message.obj = bluetooth;
                message.sendToTarget();
                AnimateUtil.hide(v, 300, 0);

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.connect.getVisibility() == View.GONE){
                    AnimateUtil.show(holder.connect, 300, 0, 1, true);
                    AnimateUtil.hide(holder.rssi, 300, 0);
                }else {
                    AnimateUtil.hide(holder.connect, 300, 0);
                    AnimateUtil.show(holder.rssi, 300, 0);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bluetooths.size();
    }


    public void addItem(Bluetooth bluetooth) {
        bluetooths.add(bluetooth);
        this.notifyItemInserted(this.getItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
