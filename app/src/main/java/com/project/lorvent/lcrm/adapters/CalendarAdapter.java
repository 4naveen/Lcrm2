package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Events;

import java.util.ArrayList;

/**
 * Created by Guest on 11/1/2016.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.SimpleViewHolder>{
    private Context context;
    private static ArrayList<Events> eventsArrayList;

    public CalendarAdapter(Context context,ArrayList<Events>eventses) {
        this.context = context;
        eventsArrayList=eventses;

    }

    @Override
    public CalendarAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_calendar,parent,false);
        Configuration config = context.getResources().getConfiguration();


        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CalendarAdapter.SimpleViewHolder holder, int position) {
        Events events=eventsArrayList.get(position);
        holder.tvTitle.setText(events.getTitle());
        holder.tvDescription.setText(events.getType());
        holder.end_date.setText(events.getEnd());
    }

    @Override
    public int getItemCount() {
        return eventsArrayList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle,tvDescription,end_date;
        SimpleViewHolder(View itemView) {

            super(itemView);
            tvTitle=(TextView)itemView.findViewById(R.id.event_title);
            tvDescription=(TextView)itemView.findViewById(R.id.description);
            end_date=(TextView)itemView.findViewById(R.id.end_date);
        }
    }
}
