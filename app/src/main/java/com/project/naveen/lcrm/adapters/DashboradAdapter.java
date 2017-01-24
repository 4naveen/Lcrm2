package com.project.naveen.lcrm.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.naveen.lcrm.R;

import java.util.ArrayList;


/**
 * Created by Guest on 11/9/2016.
 */

public class DashboradAdapter extends RecyclerView.Adapter<DashboradAdapter.DashViewHolder> {
  int images[]={R.drawable.dash_cont, R.drawable.dash_pro,R.drawable.dash_opp,R.drawable.dash_cust};
    String []titles={"Contracts","Products","Opportunity","Customers"};
    private ArrayList <Integer> numbersList;
    int[] colors={R.color.colorContracts,R.color.colorProducts,R.color.colorOpp,R.color.colorCustomer};
    public DashboradAdapter(ArrayList<Integer>arrayList) {
        numbersList=new ArrayList<>();
        for (Integer i:arrayList) {
            numbersList.add(i);
        }

    }

    @Override
    public DashboradAdapter.DashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_row1_dash,parent,false);

        return new DashViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DashboradAdapter.DashViewHolder holder, int position) {
        for (int i = 0; i <titles.length ; i++) {
            Log.i("position--", String.valueOf(position));
            holder.titleIcon.setImageResource(images[position]);
            holder.tvTitle.setText(titles[position]);
            holder.tvNumber.setText(numbersList.get(i));
            holder.cardView.setBackgroundResource(colors[position]);
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    class DashViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle;
        TextView tvNumber;
         ImageView titleIcon;

        DashViewHolder(View itemView) {
            super(itemView);
            tvTitle=(TextView)itemView.findViewById(R.id.name);
            tvNumber=(TextView)itemView.findViewById(R.id.number);
            titleIcon=(ImageView)itemView.findViewById(R.id.iv);
            cardView=(CardView)itemView.findViewById(R.id.cv);
        }
    }


}
