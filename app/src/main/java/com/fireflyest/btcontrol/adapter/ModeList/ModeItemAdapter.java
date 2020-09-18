package com.fireflyest.btcontrol.adapter.ModeList;

import android.content.res.Resources;
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
import com.fireflyest.btcontrol.bean.Mode;
import com.fireflyest.btcontrol.data.DataManager;
import com.fireflyest.btcontrol.util.AnimateUtil;

import java.util.List;

public class ModeItemAdapter extends RecyclerView.Adapter<ModeItemAdapter.ViewHolder> {


    private List<Mode> modes;
    private Handler handler;

    private DataManager dataManager;

    static class ViewHolder extends RecyclerView.ViewHolder{

        Resources resource;
        TextView name;
        TextView desc;
        TextView code;
        TextView delete;

        private ViewHolder(@NonNull View view) {
            super(view);
            resource = view.getResources();
            name = view.findViewById(R.id.item_mode_name);
            desc = view.findViewById(R.id.item_mode_desc);
            code = view.findViewById(R.id.item_mode_code);
            delete = view.findViewById(R.id.item_mode_delete);
        }
    }

    public ModeItemAdapter(List<Mode> modes, Handler handler) {
        this.modes = modes;
        this.handler = handler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Mode mode = modes.get(position);
        holder.name.setText(mode.getName());
        holder.desc.setText(mode.getDesc());
        holder.code.setText(mode.getCode());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(holder.delete.getVisibility() == View.GONE){
                    AnimateUtil.show(holder.delete, 300, 0);
                }else {
                    AnimateUtil.hide(holder.delete, 300, 0);
                }
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = handler.obtainMessage(ModeActivity.BACK_RESULT);
                msg.obj = holder.code.getText().toString();
                msg.sendToTarget();
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dataManager = DataManager.getInstance();
                        dataManager.getModeDao().delete(mode);
                    }
                }).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }


    public void addItem(int position, Mode mode) {
        modes.add(position, mode);
        this.notifyItemInserted(position);
    }

    public void removeItem(int position){
        if(position == -1) return;
        modes.remove(position);
        this.notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
