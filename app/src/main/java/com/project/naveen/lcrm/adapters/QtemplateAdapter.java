package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.detailsactivity.QtemplateDetailsActivity;
import com.project.naveen.lcrm.models.Qtemplate;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Guest on 11/4/2016.
 */

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


    class QtemplateViewHolder extends RecyclerView.ViewHolder {

        SwipeLayout swipeLayout;
        TextView tvQtemplate,tvQduration;
        ImageView arrow;

        QtemplateViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvQtemplate = (TextView) itemView.findViewById(R.id.quotation_template);
            tvQduration = (TextView) itemView.findViewById(R.id.quotation_duration);

            arrow=(ImageView)itemView.findViewById(R.id.arrow);

        }
    }



}
