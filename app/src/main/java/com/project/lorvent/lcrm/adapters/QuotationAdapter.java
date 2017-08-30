package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.lorvent.lcrm.activities.details.admin.QuotationDetailsActivity;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Quotation;


import java.util.ArrayList;


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
   /* public QuotationAdapter(ArrayList<Quotation>quotationArrayList) {

        quotations =quotationArrayList;

    }*/
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
       // viewHolder.tvTotal.setText(quotation.getFinal_price());


    }

    @Override
    public int getItemCount() {
        return quotations.size();
    }


    class QuotationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvCustomer,tvSalesperson,tvTotal;
        ImageView details;
        QuotationViewHolder(View itemView) {
            super(itemView);
           // tvTotal = (TextView) itemView.findViewById(R.id.total);
            tvCustomer = (TextView) itemView.findViewById(R.id.customer);
            tvSalesperson = (TextView) itemView.findViewById(R.id.salesperson);
            details=(ImageView)itemView.findViewById(R.id.arrow);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int quotationId=quotations.get(getAdapterPosition()).getId();
            Intent i=new Intent(mContext, QuotationDetailsActivity.class);
            i.putExtra("quotationId",String.valueOf(quotationId));
            i.putExtra("quotationIdPosition",getAdapterPosition());
            //Log.i("quotationIdPosition", String.valueOf(position));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(i);
        }
    }
}
