package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Meeting;

import java.util.ArrayList;

/**
 * Created by Guest on 11/2/2016.
 */

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
        holder.meeting_subject.setText(meeting.getMeeting_subject());
        holder.start_date.setText(meeting.getStarting_date());

    }

    @Override
    public int getItemCount() {
        return meetingArrayList.size();
    }

    class MeetingViewHolder extends RecyclerView.ViewHolder {
        TextView meeting_subject,responsible,start_date;
        MeetingViewHolder(View itemView) {
            super(itemView);
            meeting_subject=(TextView)itemView.findViewById(R.id.meeting_subject);
            start_date=(TextView)itemView.findViewById(R.id.start_date);

        }
    }
}
