package com.xaau.bs.busx.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xaau.bs.busx.R;
import com.xaau.bs.busx.domain.Bus;
import com.xaau.bs.busx.domain.Station;

import java.util.List;

public class LineStationAdapter extends RecyclerView.Adapter<LineStationAdapter.ViewHolder> {
    private List<Station> list;
    private List bus;

    public LineStationAdapter(List<Station> list , List bus) {
        this.list = list;
        this.bus = bus;

    }

    @Override
    public LineStationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line_station,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LineStationAdapter.ViewHolder holder, int position) {
        Station item = list.get(position);
        holder.id.setText(String.valueOf(item.getSta_No()));
        holder.title.setText(item.getSta_Name());
       // Bus bus=this.bus.get(position);
        holder.details.setText(bus.get(position).toString());
//        while (bus.get(position)!=null||bus.get(position).get(position)!=null){
//            Bus busItem= bus.get(position).get(position);
//            holder.details.setText(busItem.getBusName());
//        }
//        List<Bus> busList=bus.get(position);
//        Log.e("this",busList.toString());
//        for (int i=0;i<busList.size();i++){
//            Bus busItem= busList.get(i);
//            holder.details.setText(busItem.getBusName());
//            Log.e("this",busItem.getBusName()+i);
//        }
    }

    @Override
    public int getItemCount() {
        return list.size();
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
