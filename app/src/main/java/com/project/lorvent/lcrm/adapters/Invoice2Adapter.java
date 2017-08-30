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
import com.project.lorvent.lcrm.activities.details.admin.InvoicesDetailsActivity;
import com.project.lorvent.lcrm.models.Invoice;


import java.util.ArrayList;



public class Invoice2Adapter extends RecyclerView.Adapter<Invoice2Adapter.Invoice2ViewHolder> {
    private ArrayList<Invoice> invoiceArrayList;
    private Context context;
    private Configuration config ;

    public Invoice2Adapter(ArrayList<Invoice> invoiceArrayList, Context context) {
         this.invoiceArrayList = invoiceArrayList;
         this.context = context;
        config = context.getResources().getConfiguration();

    }

    @Override
    public Invoice2Adapter.Invoice2ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_invoices, parent, false);
        return new Invoice2ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Invoice2Adapter.Invoice2ViewHolder viewHolder, int position) {
        Invoice invoice=invoiceArrayList.get(position);
       // Log.i("customer--",invoice.getCustomer());
        viewHolder.tvCustomer.setText(invoice.getCustomer());
        viewHolder.tvInvoiceDate.setText(invoice.getInvoice_date());
        if (config.smallestScreenWidthDp >= 600) {
            viewHolder.tvStatus.setText(invoice.getStatus());
        }

    }

    @Override
    public int getItemCount() {
        return invoiceArrayList.size();
    }

    class Invoice2ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvCustomer;
        TextView tvStatus;
        TextView tvInvoiceDate;
        ImageView arrow;
        Invoice2ViewHolder(View itemView) {
            super(itemView);
            tvCustomer = (TextView) itemView.findViewById(R.id.customer);
            if (config.smallestScreenWidthDp >= 600) {
                tvStatus = (TextView) itemView.findViewById(R.id.status);
            }
            tvInvoiceDate=(TextView)itemView.findViewById(R.id.invoice_date);
            arrow=(ImageView)itemView.findViewById(R.id.details);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            final int invoice_id=invoiceArrayList.get(getAdapterPosition()).getId();
            Intent i=new Intent(context, InvoicesDetailsActivity.class);
            i.putExtra("invoice_id",invoice_id);
            i.putExtra("invoice_id_position",getAdapterPosition());
           // Log.i("invoice_id_position", String.valueOf(position));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);
        }
    }
}
