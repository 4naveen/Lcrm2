package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.details.admin.ContractDetailActivity;
import com.project.lorvent.lcrm.models.Contracts;

import java.util.ArrayList;



public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.SimpleViewHolder> {
    private ArrayList<Contracts> contractsArrayList;
    private Context context;
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
      /*  final TextView date = new TextView(context);
        final TextView description= new TextView(context);*/
        final Contracts contracts=contractsArrayList.get(position);
        viewHolder.tvCompany.setText(contracts.getContact());
        viewHolder.tvResponsible.setText(contracts.getResponsible());

    }
    @Override
    public int getItemCount() {
        return contractsArrayList.size();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        SwipeLayout swipeLayout;
        TextView tvCompany;
        TextView tvResponsible;
        ImageView arrow;
       /* ImageView arrow1;
        LinearLayout layout_date;
        LinearLayout layout_description;
*/
        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            arrow=(ImageView)itemView.findViewById(R.id.arrow);
//            arrow1=(ImageView)itemView.findViewById(R.id.arrow1);
            tvResponsible = (TextView) itemView.findViewById(R.id.responsible);
            tvCompany = (TextView) itemView.findViewById(R.id.company);

          /*  layout_description=(LinearLayout)itemView.findViewById(R.id.layout_description);
            layout_date=(LinearLayout)itemView.findViewById(R.id.layout_date);*/
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int contract_id=contractsArrayList.get(getAdapterPosition()).getId();
            Intent i=new Intent(context, ContractDetailActivity.class);
            i.putExtra("contract_id",String.valueOf(contract_id));
            i.putExtra("contract_id_position",getAdapterPosition());
            //Log.i("contract_position", String.valueOf(position));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);
        }
    }
}
