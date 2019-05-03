package com.xaau.bs.busx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xaau.bs.busx.domain.Dessert;
import com.xaau.bs.busx.R;

import java.util.ArrayList;

public class DessertAdapter extends RecyclerView.Adapter<DessertAdapter.ViewHolder> {

    private ArrayList<Dessert> items;

    public DessertAdapter(){
        items = new ArrayList<Dessert>();
        Dessert item = new Dessert("⚠".charAt(0), "Welcome", "欢迎使用busX");
        items.add(item);
    }

    public DessertAdapter(ArrayList<Dessert> items){
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dessert item = items.get(position);
        holder.labelLetter.setText(String.valueOf(item.getLetter()));
        holder.labelName.setText(item.getName());
        holder.labelDesc.setText(item.getDesc());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView labelLetter;
        TextView labelName;
        TextView labelDesc;

        public ViewHolder(View view) {
            super(view);
            labelLetter = (TextView) view.findViewById(R.id.item_dessert_label_letter);
            labelName   = (TextView) view.findViewById(R.id.item_dessert_label_name);
            labelDesc   = (TextView) view.findViewById(R.id.item_dessert_label_desc);
        }
    }
}
