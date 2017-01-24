package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.detailsactivity.DetailsLoggedCallActivity;
import com.project.naveen.lcrm.models.LoggedCalls;


import java.util.ArrayList;

/**
 * Created by Guest on 11/15/2016.
 */

public class LoggedCallAdapter extends RecyclerView.Adapter<LoggedCallAdapter.CallsViewHolder> {
    private ArrayList<LoggedCalls> loggedCalls;
    private Context context;
    private Button ok;
    View v;
    public LoggedCallAdapter(Context context,ArrayList<LoggedCalls>callArrayList) {
        this.context=context;
         loggedCalls=callArrayList;
    }

    @Override
    public LoggedCallAdapter.CallsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Configuration config = context.getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_logged_call, parent, false);
        }
        else
        {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_logged_call_mob, parent, false);

        }
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_logged_call, parent, false);
        return new CallsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final LoggedCallAdapter.CallsViewHolder viewHolder,  int position) {

        LoggedCalls calls=loggedCalls.get(position);
        viewHolder.tvContacts.setText(calls.getCompany());
        viewHolder.tvResponsible.setText(calls.getResponsible());

        String name=calls.getCompany();


        //Log.i("position-", String.valueOf(position)+" name--"+lead.getName()+"number--"+lead.getNumber()+"id--"+lead.getId());
        String ch= String.valueOf(name.charAt(0));

        //ColorGenerator generator=ColorGenerator.MATERIAL;
        // int color=generator.getRandomColor();
        TextDrawable.builder().beginConfig().fontSize(20).width(10).height(10).endConfig();

        TextDrawable drawable= TextDrawable.builder().buildRound(ch.toUpperCase(), Color.rgb(97,107,192));
        viewHolder.letter.setImageDrawable(drawable);

        viewHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return loggedCalls.size();
    }



    class CallsViewHolder extends RecyclerView.ViewHolder {

        TextView tvContacts;
        TextView tvResponsible;
        ImageView letter;
        ImageView arrow;



        CallsViewHolder(View itemView) {
            super(itemView);
            letter=(ImageView)itemView.findViewById(R.id.letter);
            // tvDate= (TextView) itemView.findViewById(R.id.date);
            // tvCallSummary = (TextView) itemView.findViewById(R.id.call_summary);
            tvResponsible = (TextView) itemView.findViewById(R.id.responsible);
            tvContacts = (TextView) itemView.findViewById(R.id.contacts);
           arrow=(ImageView)itemView.findViewById(R.id.arrow);
        }
    }
}
