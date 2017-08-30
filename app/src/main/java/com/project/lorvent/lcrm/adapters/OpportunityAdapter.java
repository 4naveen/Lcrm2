package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.details.admin.DetailOpportunityActivity;
import com.project.lorvent.lcrm.models.Opportunity;

import java.util.ArrayList;

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Opportunity>opportunityArrayList;
    private int opp_position,opp_id;
    Button ok;
    private Configuration config ;
    public OpportunityAdapter(Context context, ArrayList<Opportunity> opportunities)
    {
        this.mContext=context;
        opportunityArrayList=opportunities;
        config = mContext.getResources().getConfiguration();

    }
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

        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_opportunity,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final OpportunityAdapter.MyViewHolder holder, final int position) {

        final Opportunity opportunity=opportunityArrayList.get(position);
        holder.opp.setText(opportunity.getOpportunity());
        holder.customer.setText(opportunity.getCompany());
        if (config.smallestScreenWidthDp >= 600) {
            holder.action_date.setText(opportunity.getNext_action_date());
        }
    }

    @Override
    public int getItemCount() {
        return opportunityArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView opp,action_date,customer;
        ImageView details;
        MyViewHolder(View itemView) {
            super(itemView);
            opp=(TextView)itemView.findViewById(R.id.opp);
            if (config.smallestScreenWidthDp >= 600) {
                action_date=(TextView)itemView.findViewById(R.id.action_date);
            }
            customer=(TextView)itemView.findViewById(R.id.customer);
            details=(ImageView)itemView.findViewById(R.id.details);
               itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int opp_id=opportunityArrayList.get(getAdapterPosition()).getId();
            Intent i=new Intent(mContext, DetailOpportunityActivity.class);
            i.putExtra("opp_id",opp_id);
            i.putExtra("opp_position",getAdapterPosition());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i("opp_id_adpater", String.valueOf(opp_id));
            Log.i("opp_position", String.valueOf(getAdapterPosition()));
            mContext.startActivity(i);
        }
    }
}
