package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.details.admin.SalesOrderDetailsActivity;
import com.project.lorvent.lcrm.models.SalesOrder;

import java.util.ArrayList;



public class SalesOrderAdapter extends RecyclerView.Adapter<SalesOrderAdapter.MyViewHolder> {

    private ArrayList<SalesOrder> salesOrders;
    LayoutInflater inflater;
    Context context;
    private Configuration config ;

    public SalesOrderAdapter(Context context, ArrayList<SalesOrder> salesOrders) {
        this.context = context;
        this.salesOrders = salesOrders;
        config = context.getResources().getConfiguration();

    }

    @Override
    public SalesOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_sales_order,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SalesOrderAdapter.MyViewHolder holder, int position) {
        SalesOrder order=salesOrders.get(position);
        holder.customer.setText(order.getCustomer());
        holder.total.setText(order.getFinal_price());
        if (config.smallestScreenWidthDp >= 600) {
             holder.sales_person.setText(order.getSalesPerson());
        }

    }

    @Override
    public int getItemCount() {
        return salesOrders.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView options;
        TextView customer,total,sales_person;

        MyViewHolder(View itemView) {
            super(itemView);
            customer=(TextView)itemView.findViewById(R.id.customer);
            total=(TextView)itemView.findViewById(R.id.total);
            if (config.smallestScreenWidthDp >= 600) {
                sales_person=(TextView)itemView.findViewById(R.id.salesperson);
            }
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int sales_order_id=salesOrders.get(getAdapterPosition()).getId();
            Intent i=new Intent(context, SalesOrderDetailsActivity.class);
            i.putExtra("sales_order_id",String.valueOf(sales_order_id));
            i.putExtra("sales_order_id_position",getAdapterPosition());
            //Log.i("sales_order_id_position", String.valueOf(position));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);
        }
    }
}
