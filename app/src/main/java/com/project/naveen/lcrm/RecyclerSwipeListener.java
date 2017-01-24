package com.project.naveen.lcrm;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.project.naveen.lcrm.adapters.MeetingAdapter;

/**
 * Created by Guest on 12/10/2016.
 */

public class RecyclerSwipeListener extends ItemTouchHelper.SimpleCallback {
private MeetingAdapter meetingAdapter;

    public RecyclerSwipeListener(MeetingAdapter meetingAdapter) {
        super(ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT);
        this.meetingAdapter=meetingAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
       meetingAdapter.remove(viewHolder.getAdapterPosition());
    }


}
