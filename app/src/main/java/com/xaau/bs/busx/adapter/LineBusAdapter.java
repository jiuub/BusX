package com.xaau.bs.busx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xaau.bs.busx.R;
import com.xaau.bs.busx.domain.Bus;

import java.util.List;

public class LineBusAdapter extends RecyclerView.Adapter<LineBusAdapter.ViewHolder>{
    private List buslist;
    public LineBusAdapter() {
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
        Bus busItem=(Bus) buslist.get(position);
        holder.name.setText(busItem.getBusName());
        holder.time.setText(busItem.getBusStart()+"------"+busItem.getBusEnd());
        holder.price.setText(busItem.getBusPrice()+"元");
    }

    @Override
    public int getItemCount() {
        return buslist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView price;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.item_name);
            time=itemView.findViewById(R.id.item_time);
            price=itemView.findViewById(R.id.item_price);
        }
    }
}
