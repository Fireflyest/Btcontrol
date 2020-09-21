package com.fireflyest.btcontrol.adapter.RecordList;

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
import com.fireflyest.btcontrol.bean.Record;
import com.fireflyest.btcontrol.data.SettingManager;
import com.fireflyest.btcontrol.util.CalendarUtil;

import java.util.List;

public class RecordItemAdapter extends RecyclerView.Adapter<RecordItemAdapter.ViewHolder> {


    private List<Record> records;

    private Transition transition = new AutoTransition().setDuration(100);

    static class ViewHolder extends RecyclerView.ViewHolder{

        Resources resources;
        TextView from;
        TextView to;
        TextView time;
        View line;

        private ViewHolder(@NonNull View view) {
            super(view);
            resources = view.getResources();
            from = view.findViewById(R.id.item_record_from);
            to = view.findViewById(R.id.item_record_to);
            time = view.findViewById(R.id.item_record_time);
            line = view.findViewById(R.id.item_record_line);
        }
    }

    public RecordItemAdapter(List<Record> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Record record = records.get(position);
        switch (record.getType()){
            case "Change":
                holder.from.setText(record.getFrom());
                holder.to.setText(record.getTo());
                holder.time.setText(CalendarUtil.convertTime(record.getTime()));
                holder.line.setVisibility(View.VISIBLE);
                break;
            case "Close":
                holder.from.setText(record.getFrom());
                holder.to.setText(String.format("%s", "设备关闭"));
                holder.time.setText(CalendarUtil.convertTime(record.getTime()));
                break;
            case "Open":
                holder.to.setText(record.getTo());
                holder.from.setText(String.format("%s", "开启模式"));
                holder.time.setText(CalendarUtil.convertTime(record.getTime()));
                break;
            default:
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }


    public void addItem(Record record) {
        records.add(record);
        this.notifyItemInserted(records.size());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
