package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.project.lorvent.lcrm.activities.details.admin.DetailMeetingActivity;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Meeting;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {

    private Context mContext;
    private ArrayList<Meeting> meetingArrayList;
    private int companyIdPosition;
    private int companyId;
    private Button ok;
    private String token;
    public MeetingAdapter(Context mContext,ArrayList<Meeting> meetingArrayList) {
        this.mContext = mContext;
        this.token = Appconfig.TOKEN;
        this.meetingArrayList = meetingArrayList;
    }
    public void remove(int position)
    {
        meetingArrayList.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public MeetingAdapter.MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_meeting,parent,false);

        return new MeetingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MeetingAdapter.MeetingViewHolder holder, int position) {
        Meeting meeting=meetingArrayList.get(position);
        ArrayList<String>start_date=new ArrayList<>();
        holder.meeting_subject.setText(meeting.getMeeting_subject());
        StringTokenizer tk1=new StringTokenizer(meeting.getStarting_date());
        Log.i("start_date",meeting.getStarting_date());
        while (tk1.hasMoreTokens()) {
            start_date.add(tk1.nextToken());
        }
        holder.start_date.setText(start_date.get(0)+""+start_date.get(1));
       // holder.start_date.setText(meeting.getStarting_date().substring(0,10));
    }

    @Override
    public int getItemCount() {
        return meetingArrayList.size();
    }

    class MeetingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView meeting_subject,responsible,start_date;
        MeetingViewHolder(View itemView) {
            super(itemView);
            meeting_subject=(TextView)itemView.findViewById(R.id.meeting_subject);
            start_date=(TextView)itemView.findViewById(R.id.start_date);
            itemView.setOnClickListener(this);


        }
        @Override
        public void onClick(View v) {
                    int meeting_id=meetingArrayList.get(getAdapterPosition()).getId();
                    Intent i=new Intent(mContext, DetailMeetingActivity.class);
                    i.putExtra("meeting_id",String.valueOf(meeting_id));
                    i.putExtra("meeting_id__position",getAdapterPosition());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(i);
                }



        }
    }

