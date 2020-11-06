package com.fireflyest.btcontrol.adapter.BtinfoList;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bean.Btinfo;

import java.util.List;

public class BtinfoItemAdapter extends RecyclerView.Adapter<BtinfoItemAdapter.ViewHolder> {


    private List<Btinfo> btinfos;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        TextView title;
        Resources resources;

        private ViewHolder(@NonNull View view) {
            super(view);
            resources = view.getResources();
            text = view.findViewById(R.id.item_btinfo_text);
            title = view.findViewById(R.id.item_btinfo_title);
        }
    }

    public BtinfoItemAdapter(List<Btinfo> btinfos) {
        this.btinfos = btinfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_btinfo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Btinfo btinfo = btinfos.get(position);
        if("#服务 ".equals(btinfo.getTitle())){
            holder.title.setBackgroundResource(R.color.colorPrimaryDark);
            holder.title.setTextColor(holder.resources.getColor(R.color.foreground));
            holder.text.setTextColor(holder.resources.getColor(R.color.colorIcon));
        }else if("特征".equals(btinfo.getTitle())){
            holder.title.setTextColor(holder.resources.getColor(R.color.colorPrimaryDark));
            holder.text.setTextColor(holder.resources.getColor(R.color.colorPrimary));
        }else {
            holder.title.setTextColor(holder.resources.getColor(R.color.colorPrimary));
            holder.text.setTextColor(holder.resources.getColor(R.color.colorPrimary));
        }
        holder.title.setText(String.format("%s", btinfo.getTitle()));
        holder.text.setText(String.format("%s", btinfo.getText()));
    }

    @Override
    public int getItemCount() {
        return btinfos.size();
    }


    public void addItem(Btinfo btinfo) {
        btinfos.add(btinfo);
        this.notifyItemInserted(btinfos.size());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
