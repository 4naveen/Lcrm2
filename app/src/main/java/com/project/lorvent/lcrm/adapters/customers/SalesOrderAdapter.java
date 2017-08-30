package com.project.lorvent.lcrm.adapters.customers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.details.customers.SalesOrederDetailActivity;
import com.project.lorvent.lcrm.models.customer.SalesOrder;

import java.util.ArrayList;

/**
 * Created by Guest on 11/24/2016.
 */

public class SalesOrderAdapter extends RecyclerView.Adapter<SalesOrderAdapter.SalesViewHolder> {
    private  ArrayList<SalesOrder> salesOrderArrayList;
    //private ViewGroup parent;
    private String token;
    private int quotationIdPosition;
    private Context mContext;
    View v;
    public SalesOrderAdapter(Context mContext,ArrayList<SalesOrder> salesOrderArrayList) {
        this.mContext = mContext;
        this.salesOrderArrayList=salesOrderArrayList;
    }

    @Override
    public SalesOrderAdapter.SalesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_cust_salesorder,parent,false);
        return new SalesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SalesOrderAdapter.SalesViewHolder holder, int position) {
       SalesOrder salesOrder=salesOrderArrayList.get(position);
        holder.tvSNumber.setText(salesOrder.getSales_number());
        holder.tvTotal.setText(salesOrder.getGrand_total());
        holder.tvSalesperson.setText(salesOrder.getPerson());
        holder.tvStatus.setText(salesOrder.getStatus());

    }

    @Override
    public int getItemCount() {
        return salesOrderArrayList.size();
    }

    class SalesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvSalesperson,tvSNumber,tvTotal,tvStatus;
        SalesViewHolder(View itemView) {
            super(itemView);
            tvTotal = (TextView) itemView.findViewById(R.id.total);
            tvSNumber = (TextView) itemView.findViewById(R.id.sNumber);
            tvSalesperson = (TextView) itemView.findViewById(R.id.salesperson);
            tvStatus = (TextView) itemView.findViewById(R.id.status);
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            int sales_order_id=salesOrderArrayList.get(getAdapterPosition()).getId();
            Intent i=new Intent(mContext,SalesOrederDetailActivity.class);
            i.putExtra("sales_order_id",String.valueOf(sales_order_id));
            mContext.startActivity(i);
        }
    }
}
