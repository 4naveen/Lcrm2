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
import com.project.lorvent.lcrm.activities.details.admin.QtemplateDetailsActivity;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Qtemplate;

import java.util.ArrayList;



public class QtemplateAdapter extends RecyclerView.Adapter<QtemplateAdapter.QtemplateViewHolder> {
    private QtemplateViewHolder svHolder;
    private static ArrayList<Qtemplate> qtemplates;
    //private ViewGroup parent;
    private String token;
    private int qtemplateIdPosition;
    private Context mContext;
    public QtemplateAdapter(Context context, ArrayList<Qtemplate>qtemplateArrayList) {
        this.mContext=context;
         qtemplates =qtemplateArrayList;
        this.token= Appconfig.TOKEN;
    }

    @Override
    public QtemplateAdapter.QtemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_qtemplate,parent,false);
        return new QtemplateViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final QtemplateAdapter.QtemplateViewHolder viewHolder, int position) {
        final Qtemplate qtemplate= qtemplates.get(position);

        svHolder=viewHolder;
        viewHolder.tvQtemplate.setText(qtemplate.getQuatation_template());
        viewHolder.tvQduration.setText(qtemplate.getQuatation_duration());
        final int qtemplateId=qtemplate.getId();




    }

    @Override
    public int getItemCount() {
        return qtemplates.size();
    }


    class QtemplateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SwipeLayout swipeLayout;
        TextView tvQtemplate,tvQduration;
        ImageView arrow;

        QtemplateViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvQtemplate = (TextView) itemView.findViewById(R.id.quotation_template);
            tvQduration = (TextView) itemView.findViewById(R.id.quotation_duration);

            arrow=(ImageView)itemView.findViewById(R.id.arrow);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
             int qtemplateId=qtemplates.get(getAdapterPosition()).getId();
            Intent i=new Intent(mContext, QtemplateDetailsActivity.class);
            i.putExtra("qtemplateId",String.valueOf(qtemplateId));
            i.putExtra("qtemplateId_position",getAdapterPosition());
           // Log.i("qtemplateId_position", String.valueOf(position));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(i);
        }
    }



}
