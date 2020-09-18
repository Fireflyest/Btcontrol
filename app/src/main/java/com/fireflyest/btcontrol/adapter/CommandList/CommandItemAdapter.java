package com.fireflyest.btcontrol.adapter.CommandList;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bean.Command;
import com.fireflyest.btcontrol.util.CalendarUtil;

import java.util.List;

public class CommandItemAdapter extends RecyclerView.Adapter<CommandItemAdapter.ViewHolder> {


    private List<Command> commands;

    private Transition transition = new AutoTransition().setDuration(100);

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView receive;
        TextView send;
        ConstraintLayout receiveBox;
        ConstraintLayout sendBox;
        ConstraintLayout left;
        ConstraintLayout right;
        TextView sendTime;
        TextView receiveTime;
        TextView system;
        Resources resources;

        private ViewHolder(@NonNull View view) {
            super(view);
            resources = view.getResources();
            receive = view.findViewById(R.id.item_command_receive);
            send = view.findViewById(R.id.item_command_send);
            receiveBox = view.findViewById(R.id.item_command_receive_box);
            sendBox = view.findViewById(R.id.item_command_send_box);
            left = view.findViewById(R.id.item_command_left);
            right = view.findViewById(R.id.item_command_right);
            sendTime = view.findViewById(R.id.item_command_send_time);
            receiveTime = view.findViewById(R.id.item_command_receive_time);
            system = view.findViewById(R.id.item_command_system);
        }
    }

    public CommandItemAdapter(List<Command> commands) {
        this.commands = commands;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_command, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Command command = commands.get(position);
        switch (command.getType()){
            case "Receive":
                holder.receive.setText(String.format("%s", command.getText()));
                holder.receiveTime.setText(CalendarUtil.convertTime(command.getTime()));
                holder.receiveBox.setVisibility(View.VISIBLE);
                break;
            case "Send":
                holder.send.setText(String.format("%s", command.getText()));
                holder.sendTime.setText(CalendarUtil.convertTime(command.getTime()));
                holder.sendBox.setVisibility(View.VISIBLE);
                if(!command.isSuccess()) holder.right.setBackgroundResource(R.color.colorAccentDark);
                break;
            case "System":
                holder.system.setText(String.format("%s", command.getText()));
                holder.system.setVisibility(View.VISIBLE);
                break;
            default:
        }

        final ConstraintSet receiveConstraintSet = new ConstraintSet();
        final ConstraintSet sendConstraintSet = new ConstraintSet();
        receiveConstraintSet.clone(holder.receiveBox);
        sendConstraintSet.clone(holder.sendBox);

        holder.receiveBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(holder.receiveBox, transition);
                if(holder.receiveTime.getVisibility() == View.GONE){
                    holder.receiveTime.setVisibility(View.VISIBLE);
                }else {
                    holder.receiveTime.setVisibility(View.GONE);
                }
                receiveConstraintSet.applyTo(holder.receiveBox);
            }
        });

        holder.sendBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(holder.sendBox, transition);
                if(holder.sendTime.getVisibility() == View.GONE){
                    holder.sendTime.setVisibility(View.VISIBLE);
                }else {
                    holder.sendTime.setVisibility(View.GONE);
                }
                sendConstraintSet.applyTo(holder.sendBox);
            }
        });

    }

    @Override
    public int getItemCount() {
        return commands.size();
    }


    public void addItem(Command command) {
        commands.add(command);
        this.notifyItemInserted(commands.size());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
