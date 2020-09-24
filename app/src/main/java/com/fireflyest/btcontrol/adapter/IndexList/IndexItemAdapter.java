package com.fireflyest.btcontrol.adapter.IndexList;

import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fireflyest.btcontrol.R;
import com.fireflyest.btcontrol.bean.Index;
import com.fireflyest.btcontrol.util.AnimateUtil;

import java.util.List;

public class IndexItemAdapter extends RecyclerView.Adapter<IndexItemAdapter.ViewHolder> {


    private List<Index> indices;

    static class ViewHolder extends RecyclerView.ViewHolder{

        Resources resource;
        View dot;
        View select;

        private ViewHolder(@NonNull View view) {
            super(view);
            resource = view.getResources();
            dot = view.findViewById(R.id.item_index_dot);
            select = view.findViewById(R.id.item_index_select);
        }
    }

    public IndexItemAdapter(List<Index> indices) {
        this.indices = indices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_index, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Index index = indices.get(position);
        holder.select.setVisibility(View.GONE);
        holder.dot.setVisibility(View.GONE);
        if(index.isSelect()){
            AnimateUtil.show(holder.select, 300, 0);
        }else {
            AnimateUtil.show(holder.dot, 300, 0);
        }
    }

    public void addItem(Index index) {
        indices.add(index);
        this.notifyItemInserted(this.getItemCount());
    }

    public void moveItem(int from, int to){
        if(to >= indices.size() || to < 0 || from >= indices.size() || from < 0)return;
        indices.get(from).setSelect(false);
        indices.get(to).setSelect(true);
        this.notifyItemChanged(from);
        this.notifyItemChanged(to);
    }

    public void removeItem(int position) {
        indices.remove(position);
        this.notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return indices.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
