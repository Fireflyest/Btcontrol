package com.fireflyest.btcontrol.adapter.BluetoothList;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.btcontrol.ConnectActivity;
import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bean.Bluetooth;
import com.fireflyest.btcontrol.util.AnimateUtil;
import com.fireflyest.btcontrol.util.ConvertUtil;

import java.util.List;

public class BluetoothItemAdapter extends RecyclerView.Adapter<BluetoothItemAdapter.ViewHolder> {


    private List<Bluetooth> bluetooths;
    private Handler handler;

    static class ViewHolder extends RecyclerView.ViewHolder{

        Resources resource;
        TextView name;
        TextView rssi;
        TextView connect;
//        TextView action;

        private ViewHolder(@NonNull View view) {
            super(view);
            resource = view.getResources();
            name = view.findViewById(R.id.item_bluetooth_name);
            rssi = view.findViewById(R.id.item_bluetooth_rssi);
            connect = view.findViewById(R.id.item_bluetooth_connect);
//            action = view.findViewById(R.id.item_bluetooth_action);
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
        short rssi = bluetooth.getRssi();
        String bars = "";
        if(-40 < rssi){
            bars = ConvertUtil.convertBar(4);
            holder.rssi.setTextColor(holder.resource.getColor(R.color.colorGreenDark));
        }else if(-70 < rssi){
            bars = ConvertUtil.convertBar(3);
            holder.rssi.setTextColor(holder.resource.getColor(R.color.colorPrimaryDark));
        }else if(-100 < rssi){
            bars = ConvertUtil.convertBar(2);
            holder.rssi.setTextColor(holder.resource.getColor(R.color.colorYellowDark));
        }else {
            bars = ConvertUtil.convertBar(1);
            holder.rssi.setTextColor(holder.resource.getColor(R.color.colorAccentDark));
        }
        holder.rssi.setText(bars);
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
