package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.SalesTeam;

import java.util.ArrayList;

/**
 * Created by Guest on 10/22/2016.
 */

public class SalesTeamAdapter extends RecyclerView.Adapter<SalesTeamAdapter.SimpleViewHolder> {
    private Context context;
    public static ArrayList<SalesTeam>teamArrayList;
      public SalesTeamAdapter(Context mContext, ArrayList<SalesTeam> teams)
{
    context=mContext;
    teamArrayList=teams;

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
        holder.actual_invoice.setText(team.getActual_invoice());
    }

    @Override
    public int getItemCount() {
        return teamArrayList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        TextView tvSalesTeamName,invoice_target,actual_invoice;
        SimpleViewHolder(View itemView) {
            super(itemView);
            tvSalesTeamName=(TextView)itemView.findViewById(R.id.salesteamName);
            invoice_target=(TextView)itemView.findViewById(R.id.invoice_target);
            actual_invoice=(TextView)itemView.findViewById(R.id.actual_invoice);

        }
    }
}
