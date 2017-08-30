package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.details.admin.DetailSalesTeamActivity;
import com.project.lorvent.lcrm.models.SalesTeam;

import java.util.ArrayList;

/**
 * Created by Guest on 10/22/2016.
 */

public class SalesTeamAdapter extends RecyclerView.Adapter<SalesTeamAdapter.SimpleViewHolder> {
    private Context context;
    public static ArrayList<SalesTeam>teamArrayList;
    private Configuration config ;

      public SalesTeamAdapter(Context mContext, ArrayList<SalesTeam> teams)
{
    context=mContext;
    teamArrayList=teams;
    config = mContext.getResources().getConfiguration();

}
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
     View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_salesteam,parent,false);

        return new SimpleViewHolder(v);
    }
    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        SalesTeam team=teamArrayList.get(position);
        holder.tvSalesTeamName.setText(team.getSalesteam());
        holder.invoice_target.setText(team.getTarget());
        if (config.smallestScreenWidthDp >= 600) {
             holder.actual_invoice.setText(team.getActual_invoice());
        }
    }

    @Override
    public int getItemCount() {
        return teamArrayList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvSalesTeamName,invoice_target,actual_invoice;
        SimpleViewHolder(View itemView) {
            super(itemView);
            tvSalesTeamName=(TextView)itemView.findViewById(R.id.salesteamName);
            invoice_target=(TextView)itemView.findViewById(R.id.invoice_target);
            if (config.smallestScreenWidthDp >= 600) {
                actual_invoice=(TextView)itemView.findViewById(R.id.actual_invoice);
            }
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int sales_team_id=teamArrayList.get(getAdapterPosition()).getId();
            Intent i=new Intent(context, DetailSalesTeamActivity.class);
            i.putExtra("sales_team_id",String.valueOf(sales_team_id));
            i.putExtra("sales_team_id_position",getAdapterPosition());
            //Log.i("sales_team_id_position", String.valueOf(position));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);
        }
    }
}
