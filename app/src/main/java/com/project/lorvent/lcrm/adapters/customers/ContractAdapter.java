package com.project.lorvent.lcrm.adapters.customers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.customer.Contracts;

import java.util.ArrayList;

/**
 * Created by Guest on 11/24/2016.
 */

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ContractViewHolder> {
    private static ArrayList<Contracts> contractsArrayList;
    //private ViewGroup parent;
    private String token;
    private int quotationIdPosition;
    private Context mContext;

    public ContractAdapter(Context context, ArrayList<Contracts> contractses) {
        mContext = context;
        contractsArrayList = contractses;

    }

    @Override
    public ContractAdapter.ContractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_row_contracts1, parent, false);
        return new ContractViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContractAdapter.ContractViewHolder holder, int position) {

        Contracts contracts = contractsArrayList.get(position);
        holder.tvContact.setText(contracts.getCompany());
        holder.tvResponsible.setText(contracts.getUser());
        holder.tvDuration.setText(contracts.getStart_date() + "-" + contracts.getEnd_date());
        holder.tvDescription.setText(contracts.getDescription());

    }

    @Override
    public int getItemCount() {
        return contractsArrayList.size();
    }

    class ContractViewHolder extends RecyclerView.ViewHolder {
        TextView tvResponsible, tvContact, tvDuration, tvDescription;
        ContractViewHolder(View itemView) {
            super(itemView);
            tvResponsible = (TextView) itemView.findViewById(R.id.responsible);
            tvContact = (TextView) itemView.findViewById(R.id.contacts);
            tvDescription = (TextView) itemView.findViewById(R.id.description);
            tvDuration = (TextView) itemView.findViewById(R.id.duration);
        }
    }
}
