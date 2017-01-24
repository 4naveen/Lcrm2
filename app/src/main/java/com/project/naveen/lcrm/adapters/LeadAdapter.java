package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.detailsactivity.DetailsLeadActivity;
import com.project.naveen.lcrm.models.Leads;

import java.util.ArrayList;

public class LeadAdapter extends RecyclerView.Adapter<LeadAdapter.SimpleViewHolder> {

    private int leadIdPosition;
    private ArrayList<Leads> leadsArrayList;
    private TextView opp,salesTeam,salesPerson,customer,phone,mobile,fax,priority;
    private Button ok;
    private SimpleViewHolder svHolder;
    private String token;
    private Context mContext;
    View v;
   // private  ViewGroup mParent;
    public LeadAdapter(Context context, ArrayList<Leads>leadsArrayList) {
         mContext = context;
        this.leadsArrayList=leadsArrayList;
        this.token= Appconfig.TOKEN;
    }
/*    public LeadAdapter(ArrayList<Leads> leadsArrayList1) {
        this.leadsArrayList=leadsArrayList;
    }*/
    public void remove(int position)
    {
        leadsArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,leadsArrayList.size());
    }
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Configuration config = mContext.getResources().getConfiguration();

        if (config.smallestScreenWidthDp >= 600) {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_lead, parent, false);
        }
        else
        {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_lead_mob_item, parent, false);

        }
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_lead_item, parent, false);
            // mParent=parent;
        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        final Leads lead= leadsArrayList.get(position);
        svHolder=holder;
        final int opp_id=lead.getId();
        holder.opp.setText(lead.getOpportunity());
        holder.email.setText(lead.getEmail());
        holder.sales_team.setText(lead.getSales_team());
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, DetailsLeadActivity.class);
               /* i.putExtra("opp_position",holder.getAdapterPosition());
                i.putExtra("opp_id",opp_id);*/
                mContext.startActivity(i);
            }
        });





        // mItemManger is member in RecyclerSwipeAdapter Class

    }

    @Override
    public int getItemCount() {
        return leadsArrayList.size();
    }



    //  ViewHolder Class

    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView opp,sales_team,email;
        ImageView details;
       // LinearLayout layoutleft;
        //LinearLayout layoutright;
         SimpleViewHolder(View itemView) {
            super(itemView);
             opp=(TextView)itemView.findViewById(R.id.opp);
             sales_team=(TextView)itemView.findViewById(R.id.sales_team);
             email=(TextView)itemView.findViewById(R.id.email);
             details=(ImageView)itemView.findViewById(R.id.details);
        }

    }


}
