package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Contracts;


import java.util.ArrayList;

/**
 * Created by Guest on 11/17/2016.
 */

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.SimpleViewHolder> {
    private ArrayList<Contracts> contractsArrayList;
    private Context context;
    private String token;
    private SimpleViewHolder svHolder;
    private Button ok;
    public ContractAdapter(ArrayList<Contracts> contractsArrayList, Context context) {
        this.contractsArrayList = contractsArrayList;
        this.context = context;
    }

    @Override
    public ContractAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_contract, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContractAdapter.SimpleViewHolder viewHolder, int position) {
        final TextView date = new TextView(context);
        final TextView description= new TextView(context);
        svHolder=viewHolder;
        final Contracts contracts=contractsArrayList.get(position);
        viewHolder.tvCompany.setText(contracts.getContact());
        viewHolder.tvResponsible.setText(contracts.getResponsible());



    }
    @Override
    public int getItemCount() {
        return contractsArrayList.size();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView tvCompany;
        TextView tvResponsible;
        ImageView arrow;
        ImageView arrow1;
        LinearLayout layout_date;
        LinearLayout layout_description;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            arrow=(ImageView)itemView.findViewById(R.id.arrow);
//            arrow1=(ImageView)itemView.findViewById(R.id.arrow1);
            tvResponsible = (TextView) itemView.findViewById(R.id.responsible);
            tvCompany = (TextView) itemView.findViewById(R.id.company);

          /*  layout_description=(LinearLayout)itemView.findViewById(R.id.layout_description);
            layout_date=(LinearLayout)itemView.findViewById(R.id.layout_date);*/

        }
    }
}
