package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.detailsactivity.QuotationDetailsActivity;
import com.project.naveen.lcrm.models.Quotation;


import java.util.ArrayList;

/**
 * Created by Guest on 11/3/2016.
 */

public class QuotationAdapter extends RecyclerView.Adapter<QuotationAdapter.QuotationViewHolder> {
    private QuotationViewHolder svHolder;
    private static ArrayList<Quotation> quotations;
    //private ViewGroup parent;
    private String token;
    private int quotationIdPosition;
    private Context mContext;

    public QuotationAdapter(Context context, ArrayList<Quotation>quotationArrayList) {

        this.mContext=context;
        quotations =quotationArrayList;
        this.token= Appconfig.TOKEN;
    }
    public QuotationAdapter(ArrayList<Quotation>quotationArrayList) {

        quotations =quotationArrayList;

    }
    public void remove(int position)
    {
        quotations.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,quotations.size());

        this.notifyDataSetChanged();
        Log.i("listsize -", String.valueOf(quotations.size()));

    }
    @Override
    public QuotationAdapter.QuotationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_quotation,parent,false);

        return new QuotationViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final QuotationAdapter.QuotationViewHolder viewHolder, int position) {
        final Quotation quotation= quotations.get(position);
        final int quotationId=quotation.getId();
        svHolder=viewHolder;
        viewHolder.tvCustomer.setText(quotation.getCustomer());
        viewHolder.tvSalesperson.setText(quotation.getPerson());
        viewHolder.tvTotal.setText(quotation.getFinal_price());


    }

    @Override
    public int getItemCount() {
        return quotations.size();
    }


    class QuotationViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomer,tvSalesperson,tvTotal;
        ImageView details;


        QuotationViewHolder(View itemView) {
            super(itemView);
            tvTotal = (TextView) itemView.findViewById(R.id.total);
            tvCustomer = (TextView) itemView.findViewById(R.id.customer);
            tvSalesperson = (TextView) itemView.findViewById(R.id.salesperson);
            details=(ImageView)itemView.findViewById(R.id.arrow);

        }
    }


}
