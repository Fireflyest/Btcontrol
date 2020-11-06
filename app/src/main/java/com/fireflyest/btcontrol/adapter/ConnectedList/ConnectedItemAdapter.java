package com.fireflyest.btcontrol.adapter.ConnectedList;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.btcontrol.ModeActivity;
import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bean.Connected;
import com.fireflyest.btcontrol.data.DataManager;
import com.fireflyest.btcontrol.dialog.BtinfoDialog;
import com.fireflyest.btcontrol.dialog.EditDeviceDialog;
import com.fireflyest.btcontrol.util.AnimateUtil;

import java.util.List;

import static com.fireflyest.btcontrol.ConnectActivity.SHOW_BT_INFO_DIALOG;

public class ConnectedItemAdapter extends RecyclerView.Adapter<ConnectedItemAdapter.ViewHolder> {


    private List<Connected> connecteds;
    private Handler handler;

    private DataManager dataManager;

    static class ViewHolder extends RecyclerView.ViewHolder{

        Resources resource;
        TextView name;
        TextView address;
        TextView rssi;

        private ViewHolder(@NonNull View view) {
            super(view);
            resource = view.getResources();
            name = view.findViewById(R.id.item_connected_name);
            address = view.findViewById(R.id.item_connected_address);
//            rssi = view.findViewById(R.id.item_mode_name);

        }
    }

    public ConnectedItemAdapter(List<Connected> connecteds, Handler handler) {
        this.connecteds = connecteds;
        this.handler = handler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_connected, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Connected connected = connecteds.get(position);
        holder.name.setText(connected.getName());
        if(connected.isEnable()){
            holder.itemView.setBackgroundResource(R.drawable.round_green);
            holder.name.setTextColor(holder.resource.getColor(R.color.colorGreenDark));
        }else {
            holder.itemView.setBackgroundResource(R.drawable.round_accent);
            holder.name.setTextColor(holder.resource.getColor(R.color.colorAccentDark));
        }
        holder.address.setText(connected.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.obtainMessage(SHOW_BT_INFO_DIALOG, connected.getAddress()).sendToTarget();
            }
        });
    }

    @Override
    public int getItemCount() {
        return connecteds.size();
    }


    public void addItem(int position, Connected connected) {
        connecteds.add(position, connected);
        this.notifyItemInserted(position);
    }

    public void removeItem(int position){
        if(position == -1) return;
        connecteds.remove(position);
        this.notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
