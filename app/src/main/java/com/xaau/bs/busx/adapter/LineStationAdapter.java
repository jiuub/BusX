package com.xaau.bs.busx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xaau.bs.busx.R;
import com.xaau.bs.busx.domain.Dessert;
import com.xaau.bs.busx.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class LineStationAdapter extends RecyclerView.Adapter<LineStationAdapter.ViewHolder> {
    private List<Station> list;
    private List bus;
    private ArrayList<Dessert> items;

    public LineStationAdapter(List<Station> list , List bus) {
        this.list = list;
        this.bus = bus;

    }
    public LineStationAdapter(){
        items = new ArrayList<Dessert>();
        Dessert item = new Dessert("1".charAt(0),"站点信息","暂无");
        items.add(item);
    }

    @Override
    public LineStationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line_station,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LineStationAdapter.ViewHolder holder, int position) {
        if(list!=null&&!list.isEmpty()){
            Station item = list.get(position);
            holder.id.setText(String.valueOf(item.getSta_No()));
            holder.title.setText(item.getSta_Name());
            holder.details.setText(bus.get(position).toString());
        }else {
            Dessert item = items.get(position);
            holder.id.setText(String.valueOf(item.getLetter()));
            holder.title.setText(item.getName());
            holder.details.setText(item.getDesc());
        }
    }

    @Override
    public int getItemCount() {
        if (list!=null&&!list.isEmpty()){
            return list.size();
        }else {
            return items.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView id;
        TextView title;
        TextView details;
        ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.item_id);
            title = itemView.findViewById(R.id.item_title);
            details=itemView.findViewById(R.id.item_details);
        }

    }
}
