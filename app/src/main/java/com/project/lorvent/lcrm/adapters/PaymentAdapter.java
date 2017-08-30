package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Payment;

import java.util.ArrayList;

/**
 * Created by Guest on 11/22/2016.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.SimpleViewHolder> {
    private Context mContext;
    private ArrayList<Payment> paymentArrayList;

    public PaymentAdapter(Context mContext, ArrayList<Payment> paymentArrayList) {
        this.mContext = mContext;
        this.paymentArrayList = paymentArrayList;
    }

    @Override
    public PaymentAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_payment,parent,false);

        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PaymentAdapter.SimpleViewHolder holder, int position) {

        Payment payment=paymentArrayList.get(position);
        holder.customer.setText(payment.getCustomer());
        holder.pay_number.setText(payment.getPay_number());
       // holder.pay_date.setText(payment.getPay_date());
        holder.pay_received.setText(payment.getPay_received());

    }

    @Override
    public int getItemCount() {
        return paymentArrayList.size();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView pay_number,customer,pay_date,pay_received;
        ImageView details;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            pay_number=(TextView)itemView.findViewById(R.id.pay_number);
            customer=(TextView)itemView.findViewById(R.id.customer);
            //pay_date=(TextView)itemView.findViewById(R.id.pay_date);
            pay_received=(TextView)itemView.findViewById(R.id.pay_received);
        details=(ImageView)itemView.findViewById(R.id.details);
        }
    }
}
