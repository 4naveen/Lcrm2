package com.project.lorvent.lcrm.adapters;

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

import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.details.admin.DetailsLeadActivity;
import com.project.lorvent.lcrm.models.Leads;

import java.util.ArrayList;

public class LeadAdapter extends RecyclerView.Adapter<LeadAdapter.SimpleViewHolder> {

    private int leadIdPosition;
    private ArrayList<Leads> leadsArrayList;
    private TextView opp,salesTeam,salesPerson,customer,phone,mobile,fax,priority;
    private Button ok;
    private String token;
    private Context mContext;
    View v;
    private Configuration config ;

    // private  ViewGroup mParent;
    public LeadAdapter(Context context, ArrayList<Leads>leadsArrayList) {
         mContext = context;
        this.leadsArrayList=leadsArrayList;
        this.token= Appconfig.TOKEN;
        config = mContext.getResources().getConfiguration();

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
        v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_lead, parent, false);
        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        final Leads lead= leadsArrayList.get(position);

        final int opp_id=lead.getId();
        holder.opp.setText(lead.getOpportunity());
        holder.sales_team.setText(lead.getSales_team());
        if (config.smallestScreenWidthDp >= 600) {
            holder.email.setText(lead.getEmail());
        }
        // mItemManger is member in RecyclerSwipeAdapter Class

    }

    @Override
    public int getItemCount() {
        return leadsArrayList.size();
    }

    //  ViewHolder Class

    class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView opp,sales_team,email;
        ImageView details;
       // LinearLayout layoutleft;
        //LinearLayout layoutright;
         SimpleViewHolder(View itemView) {
            super(itemView);
             opp=(TextView)itemView.findViewById(R.id.opp);
             sales_team=(TextView)itemView.findViewById(R.id.sales_team);
             if (config.smallestScreenWidthDp >= 600) {
                 email=(TextView)itemView.findViewById(R.id.email);
             }
             details=(ImageView)itemView.findViewById(R.id.details);
             itemView.setOnClickListener(this);

         }

        @Override
        public void onClick(View v) {
            int lead_id=leadsArrayList.get(getAdapterPosition()).getId();
            Intent i=new Intent(mContext, DetailsLeadActivity.class);
            i.putExtra("lead_id",lead_id);
            i.putExtra("lead_id_position",getAdapterPosition());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(i);
        }
    }


}
