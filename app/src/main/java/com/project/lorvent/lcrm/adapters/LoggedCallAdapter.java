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
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.details.admin.DetailsLoggedCallActivity;
import com.project.lorvent.lcrm.models.LoggedCalls;

import java.util.ArrayList;



public class LoggedCallAdapter extends RecyclerView.Adapter<LoggedCallAdapter.CallsViewHolder> {
    private ArrayList<LoggedCalls> loggedCalls;
    private Context context;


    public LoggedCallAdapter(Context context,ArrayList<LoggedCalls>callArrayList) {
        this.context=context;
         loggedCalls=callArrayList;
    }

    @Override
    public LoggedCallAdapter.CallsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_logged_call, parent, false);
        return new CallsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LoggedCallAdapter.CallsViewHolder viewHolder,  int position) {

        LoggedCalls calls=loggedCalls.get(position);
        viewHolder.tvContacts.setText(calls.getCompany());
        viewHolder.tvResponsible.setText(calls.getResponsible());

        String name=calls.getCompany();


        //Log.i("position-", String.valueOf(position)+" name--"+lead.getName()+"number--"+lead.getNumber()+"id--"+lead.getId());
        String ch= String.valueOf(name.charAt(0));

        //ColorGenerator generator=ColorGenerator.MATERIAL;
        // int color=generator.getRandomColor();
        TextDrawable.builder().beginConfig().fontSize(20).width(10).height(10).endConfig();

        TextDrawable drawable= TextDrawable.builder().buildRound(ch.toUpperCase(), Color.rgb(53,106,195));
        viewHolder.letter.setImageDrawable(drawable);

        viewHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return loggedCalls.size();
    }



    class CallsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvContacts;
        TextView tvResponsible;
        ImageView letter;
        ImageView arrow;



        CallsViewHolder(View itemView) {
            super(itemView);
            letter=(ImageView)itemView.findViewById(R.id.letter);
            // tvDate= (TextView) itemView.findViewById(R.id.date);
            // tvCallSummary = (TextView) itemView.findViewById(R.id.call_summary);
            tvResponsible = (TextView) itemView.findViewById(R.id.responsible);
            tvContacts = (TextView) itemView.findViewById(R.id.contacts);
           arrow=(ImageView)itemView.findViewById(R.id.arrow);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int call_id=loggedCalls.get(getAdapterPosition()).getId();
            Intent i=new Intent(context, DetailsLoggedCallActivity.class);
            i.putExtra("call_id",String.valueOf(call_id));
            i.putExtra("call_id_position",getAdapterPosition());
           // Log.i("call_id_position", String.valueOf(position));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);
        }
    }
}
