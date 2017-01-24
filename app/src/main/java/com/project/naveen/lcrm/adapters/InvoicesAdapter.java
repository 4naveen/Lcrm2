package com.project.naveen.lcrm.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.naveen.lcrm.R;


/**
 * Created by Guest on 11/21/2016.
 */

public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesAdapter.InvoiceViewHolder>{
    String []titles={"Invoice Total","Open Invoice","Overdue Invoices","Paid Invoices"};
    String []numbers={"52456$","33545$","4667$","24345$"};
    @Override
    public InvoicesAdapter.InvoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_row1_invoices,parent,false);
        return new InvoiceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InvoicesAdapter.InvoiceViewHolder holder, int position) {
        for (int i = 0; i <titles.length ; i++) {
            Log.i("position--", String.valueOf(position));
            holder.tvTitle.setText(titles[position]);
            holder.tvNumber.setText(numbers[position]);
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvNumber;

        public InvoiceViewHolder(View itemView) {
            super(itemView);
            tvTitle=(TextView)itemView.findViewById(R.id.name);
            tvNumber=(TextView)itemView.findViewById(R.id.number);
        }
    }
}
