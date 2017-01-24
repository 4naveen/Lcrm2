package com.project.naveen.lcrm.adapters.customers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.customer.Invoice;

import java.util.ArrayList;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>{
    private  ArrayList<Invoice> invoiceArrayList;
    //private ViewGroup parent;
    private String token;
    private int quotationIdPosition;
    private Context mContext;

    public InvoiceAdapter(Context mContext,ArrayList<Invoice> invoiceArrayList) {
        this.mContext = mContext;
        this.invoiceArrayList=invoiceArrayList;
    }

    @Override
    public InvoiceAdapter.InvoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_cust_invoice,parent,false);
        return new InvoiceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InvoiceAdapter.InvoiceViewHolder holder, int position) {

        Invoice invoice=invoiceArrayList.get(position);
        holder.tviNumber.setText(invoice.getInvoice_number());
        holder.tvDueDate.setText(invoice.getDue_date());
        holder.tvUnpaid.setText(invoice.getUnpaid_amount());
        holder.tvStatus.setText(invoice.getStatus());
        
    }

    @Override
    public int getItemCount() {
        return invoiceArrayList.size();
    }

    class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvDueDate,tviNumber,tvUnpaid,tvStatus;


        InvoiceViewHolder(View itemView) {
            super(itemView);
            tvDueDate = (TextView) itemView.findViewById(R.id.dueDate);
            tviNumber = (TextView) itemView.findViewById(R.id.iNumber);
            tvUnpaid = (TextView) itemView.findViewById(R.id.unpaid_amount);
            tvStatus = (TextView) itemView.findViewById(R.id.status);
            
        }
    }
}
