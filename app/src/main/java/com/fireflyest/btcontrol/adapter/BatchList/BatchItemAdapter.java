package com.fireflyest.btcontrol.adapter.BatchList;

import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bean.Device;
import com.fireflyest.btcontrol.data.DataManager;

import java.util.ArrayList;
import java.util.List;

public class BatchItemAdapter extends RecyclerView.Adapter<BatchItemAdapter.ViewHolder> {


    private List<Device> devices;
    private Handler handler;

    private List<String> select = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder{

        Resources resource;
        TextView name;
        TextView id;

        private ViewHolder(@NonNull View view) {
            super(view);
            resource = view.getResources();
            name = view.findViewById(R.id.item_batch_name);
        }
    }

    public BatchItemAdapter(List<Device> devices, Handler handler) {
        this.devices = devices;
        this.handler = handler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_batch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Device device = devices.get(position);
        holder.name.setText(device.getName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSelect(device.getAddress(), v);
            }
        });
        if(select.contains(device.getAddress())){
            holder.name.setBackgroundResource(R.drawable.round_primary_light);
        }
    }

    private void updateSelect(String address, View view){
        if(select.contains(address)){
            view.setBackgroundResource(R.drawable.round_primary_light);
            select.remove(address);
        }else {
            view.setBackgroundResource(R.drawable.round_green);
            select.add(address);
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }


    public void addItem(int position, Device device) {
        devices.add(position, device);
        this.notifyItemInserted(position);
    }

    public void removeItem(int position){
        if(position == -1) return;
        devices.remove(position);
        this.notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
