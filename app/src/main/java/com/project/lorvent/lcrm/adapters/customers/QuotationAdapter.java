package com.project.lorvent.lcrm.adapters.customers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.details.customers.QuotationDetailActivity;
import com.project.lorvent.lcrm.models.customer.Quotation;

import java.util.ArrayList;

/**
 * Created by Guest on 11/23/2016.
 */

public class QuotationAdapter extends RecyclerView.Adapter<QuotationAdapter.QuotationViewHolder> {
    private QuotationViewHolder svHolder;
    private static ArrayList<Quotation> quotationArrayList;
    //private ViewGroup parent;
    private String token;
    private int quotationIdPosition;
    private Context mContext;
    View v;
    public QuotationAdapter(Context mContext,ArrayList<Quotation>quotations) {
        this.mContext = mContext;
        quotationArrayList=quotations;
    }

    @Override
    public QuotationAdapter.QuotationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_quotation1,parent,false);
        return new QuotationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(QuotationAdapter.QuotationViewHolder viewHolder, int position) {

        Quotation quotation= quotationArrayList.get(position);
        viewHolder.tvQNumber.setText(quotation.getQuotations_number());
        viewHolder.tvSalesperson.setText(quotation.getPerson());
        viewHolder.tvTotal.setText(quotation.getFinal_price());
    }

    @Override
    public int getItemCount() {
        return quotationArrayList.size();
    }

    class QuotationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvSalesperson,tvQNumber,tvTotal,tvDate;

        QuotationViewHolder(View itemView) {
            super(itemView);
            tvTotal = (TextView) itemView.findViewById(R.id.total);
            tvQNumber = (TextView) itemView.findViewById(R.id.qNumber);
            tvSalesperson = (TextView) itemView.findViewById(R.id.salesperson);
           // tvDate = (TextView) itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int quotationId=quotationArrayList.get(getAdapterPosition()).getId();
            Intent i=new Intent(mContext, QuotationDetailActivity.class);
            i.putExtra("quotation_id",String.valueOf(quotationId));
            mContext.startActivity(i);
        }
    }
}
