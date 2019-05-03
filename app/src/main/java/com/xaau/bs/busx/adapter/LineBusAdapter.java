package com.xaau.bs.busx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xaau.bs.busx.R;
import com.xaau.bs.busx.domain.Bus;
import com.xaau.bs.busx.domain.Dessert;

import java.util.ArrayList;
import java.util.List;

public class LineBusAdapter extends RecyclerView.Adapter<LineBusAdapter.ViewHolder>{
    private List buslist;
    private ArrayList<Dessert> items;
    public LineBusAdapter() {
        items = new ArrayList<Dessert>();
        Dessert item = new Dessert("⚠".charAt(0),"无查询结果","😭😭😭");
        items.add(item);
    }

    public LineBusAdapter(List bus) {
        this.buslist = bus;
    }

    @Override
    public LineBusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station_bus,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LineBusAdapter.ViewHolder holder, int position) {
        if (buslist!=null&&!buslist.isEmpty()){
            Bus busItem=(Bus) buslist.get(position);
            holder.name.setText(busItem.getBusName());
            holder.time.setText(busItem.getBusStart()+" 至 "+busItem.getBusEnd());
            holder.price.setText(busItem.getBusPrice()+"元");
        }else {
            Dessert item = items.get(position);
            holder.name.setText(String.valueOf(item.getLetter()));
            holder.time.setText(item.getName());
            holder.price.setText(item.getDesc());
        }

    }

    @Override
    public int getItemCount() {
        if (buslist!=null&&!buslist.isEmpty()){
            return buslist.size();
        }else {
            return items.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView price;
        ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.item_name);
            time=itemView.findViewById(R.id.item_time);
            price=itemView.findViewById(R.id.item_price);
        }
    }
}
