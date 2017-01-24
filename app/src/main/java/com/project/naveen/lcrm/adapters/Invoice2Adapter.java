package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Invoice;


import java.util.ArrayList;

/**
 * Created by Guest on 11/21/2016.
 */

public class Invoice2Adapter extends RecyclerView.Adapter<Invoice2Adapter.Invoice2ViewHolder> {
    private ArrayList<Invoice> invoiceArrayList;
    private Context context;
    View v;
    public Invoice2Adapter(ArrayList<Invoice> invoiceArrayList, Context context) {
         this.invoiceArrayList = invoiceArrayList;
         this.context = context;
    }

    @Override
    public Invoice2Adapter.Invoice2ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Configuration config = context.getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_invoices, parent, false);
        }
        else
        {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_invoices_mob, parent, false);

        }
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_invoices, parent, false);
        return new Invoice2ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final Invoice2Adapter.Invoice2ViewHolder viewHolder, int position) {
        Invoice invoice=invoiceArrayList.get(position);
        Log.i("customer--",invoice.getCustomer());
        viewHolder.tvCustomer.setText(invoice.getCustomer());
        viewHolder.tvInvoiceDate.setText(invoice.getInvoice_date());
        viewHolder.tvStatus.setText(invoice.getStatus());
   /*     viewHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,InvoicesDetailsActivity.class);
               *//* i.putExtra("opp_position",holder.getAdapterPosition());
                i.putExtra("opp_id",opp_id);*//*
                context.startActivity(i);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return invoiceArrayList.size();
    }

    class Invoice2ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCustomer;
        TextView tvStatus;
        TextView tvInvoiceDate;
        ImageView arrow;
        Invoice2ViewHolder(View itemView) {
            super(itemView);
            tvCustomer = (TextView) itemView.findViewById(R.id.customer);
            tvStatus = (TextView) itemView.findViewById(R.id.status);
            tvInvoiceDate=(TextView)itemView.findViewById(R.id.invoice_date);
            arrow=(ImageView)itemView.findViewById(R.id.details);
        }
    }
}
