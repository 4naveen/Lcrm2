package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.daimajia.swipe.SwipeLayout;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Staff;


import java.util.ArrayList;

/**
 * Created by Guest on 10/17/2016.
 */

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.SimpleViewHolder> {
    private SimpleViewHolder svHolder;
    public  ArrayList<Staff> staffs;

    //private ViewGroup parent;
    private String token;
    private String lastName = "";
    private String firstName= "";
    private int staffIdPosition;
    private Context mContext;
    View v;
    public StaffAdapter(Context context, ArrayList<Staff>staffArrayList)
    {    this.mContext=context;
        staffs =staffArrayList;
        this.token= Appconfig.TOKEN;

    }
    @Override
    public StaffAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      //  this.parent=parent;
        Configuration config = mContext.getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_staff_item,parent,false);
        }
        else
        {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_staff_item_mob,parent,false);

        }
//        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_staff_item,parent,false);
     return new SimpleViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final StaffAdapter.SimpleViewHolder viewHolder, int position) {
        final Staff staff= staffs.get(position);

        svHolder=viewHolder;
        viewHolder.tvName.setText(staff.getName());
        viewHolder.tvEmail.setText(staff.getEmail());

        final int staff_id=staff.getId();

           String name=staff.getName();


        //Log.i("position-", String.valueOf(position)+" name--"+lead.getName()+"number--"+lead.getNumber()+"id--"+lead.getId());
        String ch= String.valueOf(name.charAt(0));

        //ColorGenerator generator=ColorGenerator.MATERIAL;
        // int color=generator.getRandomColor();
        TextDrawable.builder().beginConfig().fontSize(20).width(10).height(10).bold().endConfig();

        TextDrawable drawable= TextDrawable.builder().buildRound(ch.toUpperCase(), Color.rgb(97,107,192));

        viewHolder.letter.setImageDrawable(drawable);



    }


    @Override
    public int getItemCount() {
        return staffs.size();
    }


     class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView tvName;
        TextView tvEmail;
        ImageView letter;
        ImageView arrow;
        SimpleViewHolder(View itemView) {
            super(itemView);
            letter=(ImageView)itemView.findViewById(R.id.letter);
            tvName = (TextView) itemView.findViewById(R.id.name);
            tvEmail = (TextView) itemView.findViewById(R.id.email);

            arrow=(ImageView)itemView.findViewById(R.id.arrow);

        }

    }



}