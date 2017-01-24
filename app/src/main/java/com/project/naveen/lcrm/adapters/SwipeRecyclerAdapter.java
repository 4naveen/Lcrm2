package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Events;

import java.util.ArrayList;

/**
 * Created by Guest on 1/23/2017.
 */

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
        holder.tvTitle.setText(events.getTitle());
        holder.tvDescription.setText(events.getDescription());
        holder.end_date.setText(events.getEnd());
    }

    @Override
    public int getItemCount() {
        return eventsArrayList.size();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle,tvDescription,end_date;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            tvTitle=(TextView)itemView.findViewById(R.id.title);
            tvDescription=(TextView)itemView.findViewById(R.id.description);
            end_date=(TextView)itemView.findViewById(R.id.end_date);
        }
    }
}
