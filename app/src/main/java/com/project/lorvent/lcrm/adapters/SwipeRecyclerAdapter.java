package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Events;

import java.util.ArrayList;



public class SwipeRecyclerAdapter extends RecyclerView.Adapter<SwipeRecyclerAdapter.SimpleViewHolder>{
    private Context context;
    private ArrayList<Events> eventsArrayList;

    public SwipeRecyclerAdapter(Context context, ArrayList<Events> eventsArrayList) {
        this.context = context;
        this.eventsArrayList=eventsArrayList;
    }

    @Override
    public SwipeRecyclerAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_swipe_rv,parent,false);
        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SwipeRecyclerAdapter.SimpleViewHolder holder, int position) {
        Events events=eventsArrayList.get(position);
        if (events!=null)
        {
            holder.tvTitle.setText(events.getTitle());
            holder.tvtype.setText(events.getType().replace(events.getType().charAt(0),Character.toUpperCase(events.getType().charAt(0))));
            holder.end_date.setText(events.getEnd());
        }
       else {
            holder.tvtype.setText("No Data Available !");

        }
    }

    @Override
    public int getItemCount() {
        return eventsArrayList.size();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle,tvtype,end_date;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            tvTitle=(TextView)itemView.findViewById(R.id.title);
            tvtype=(TextView)itemView.findViewById(R.id.type);
            end_date=(TextView)itemView.findViewById(R.id.end_date);
        }
    }
}
