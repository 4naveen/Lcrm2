package com.project.naveen.lcrm.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.detailsactivity.DetailOpportunityActivity;

import com.project.naveen.lcrm.models.Opportunity;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Guest on 10/21/2016.
 */

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Opportunity>opportunityArrayList;
    private int opp_position,opp_id;
    Button ok;
    View v;
    public OpportunityAdapter(Context context, ArrayList<Opportunity> opportunities)
    {
        this.mContext=context;
        opportunityArrayList=opportunities;

    }

 /*   public OpportunityAdapter(ArrayList<Opportunity> opportunities) {
        opportunityArrayList=opportunities;
    }*/

    public void remove(int position)
    {
        opportunityArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,opportunityArrayList.size());
        notifyDataSetChanged();
        Log.i("position--", String.valueOf(position));
        Log.i("listsize -", String.valueOf(opportunityArrayList.size()));
    }
    @Override
    public OpportunityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Configuration config = mContext.getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_opportunity,parent,false);
        }
        else
        {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_opportunity_mob,parent,false);

        }
       // View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_opportunity,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final OpportunityAdapter.MyViewHolder holder, int position) {

        Opportunity opportunity=opportunityArrayList.get(position);
        final int opp_id=opportunity.getId();
        holder.opp.setText(opportunity.getOpportunity());
        holder.action_date.setText(opportunity.getNext_action_date());
        holder.customer.setText(opportunity.getCompany());
 /*       holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, DetailOpportunityActivity.class);
                i.putExtra("opp_position",holder.getLayoutPosition());
                i.putExtra("opp_id",opp_id);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                mContext.startActivity(i);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return opportunityArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView opp,action_date,customer;
        ImageView details;
        MyViewHolder(View itemView) {
            super(itemView);
            opp=(TextView)itemView.findViewById(R.id.opp);
            action_date=(TextView)itemView.findViewById(R.id.action_date);
            customer=(TextView)itemView.findViewById(R.id.customer);
            details=(ImageView)itemView.findViewById(R.id.details);
        }
    }
}
