package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.SalesOrder;

import java.util.ArrayList;

/**
 * Created by Guest on 11/16/2016.
 */

public class SalesOrderAdapter extends RecyclerView.Adapter<SalesOrderAdapter.MyViewHolder> {

    private ArrayList<SalesOrder> salesOrders;
    LayoutInflater inflater;
    Context context;
     View v;
    public SalesOrderAdapter(Context context, ArrayList<SalesOrder> salesOrders) {
        this.context = context;
        this.salesOrders = salesOrders;
    }

    @Override
    public SalesOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Configuration config =context.getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_sales_order,parent,false);
        }
        else
        {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_sales_order_mob,parent,false);

        }
//        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_sales_order,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SalesOrderAdapter.MyViewHolder holder, int position) {
        SalesOrder order=salesOrders.get(position);
        holder.customer.setText(order.getCustomer());
        holder.salesPerson.setText(order.getSalesPerson());
        holder.total.setText(order.getFinal_price());


    }

    @Override
    public int getItemCount() {
        return salesOrders.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView options;
        TextView customer,salesPerson,total;

        MyViewHolder(View itemView) {
            super(itemView);
            customer=(TextView)itemView.findViewById(R.id.customer);
            salesPerson=(TextView)itemView.findViewById(R.id.salesperson);
            total=(TextView)itemView.findViewById(R.id.total);


        }
    }
}
