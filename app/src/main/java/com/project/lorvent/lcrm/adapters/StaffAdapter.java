package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.project.lorvent.lcrm.activities.details.admin.StaffDetailActivity;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Staff;

import java.util.ArrayList;



public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.SimpleViewHolder> {
    private SimpleViewHolder svHolder;
    public  ArrayList<Staff> staffs;

    //private ViewGroup parent;
    private String token;
    private String lastName = "";
    private String firstName= "";
    private int staffIdPosition;
    private Context mContext;

    public StaffAdapter(Context context, ArrayList<Staff>staffArrayList)
    {    this.mContext=context;
        staffs =staffArrayList;
        this.token= Appconfig.TOKEN;

    }
    @Override
    public StaffAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

 View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_staff_item,parent,false);
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

        TextDrawable drawable= TextDrawable.builder().buildRound(ch.toUpperCase(), Color.rgb(53,106,195));

        viewHolder.letter.setImageDrawable(drawable);



    }


    @Override
    public int getItemCount() {
        return staffs.size();
    }


     class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
            itemView.setOnClickListener(this);

        }

         @Override
         public void onClick(View v) {
             int staff_id=staffs.get(getAdapterPosition()).getId();
             Intent i=new Intent(mContext,StaffDetailActivity.class);
             i.putExtra("staff_id",String.valueOf(staff_id));
             i.putExtra("staff_id_position",getAdapterPosition());
            // Log.i("staff_id_position", String.valueOf(position));
             i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             mContext.startActivity(i);
         }
     }
}