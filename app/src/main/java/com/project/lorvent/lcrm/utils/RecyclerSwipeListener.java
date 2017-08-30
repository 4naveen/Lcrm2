package com.project.lorvent.lcrm.utils;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.project.lorvent.lcrm.adapters.InboxAdapter;
import com.project.lorvent.lcrm.adapters.SentMailAdapter;


/**
 * Created by Guest on 12/10/2016.
 */

public class RecyclerSwipeListener extends ItemTouchHelper.SimpleCallback {
private InboxAdapter inboxAdapter;
private SentMailAdapter sentMailAdapter;
    private int reqCode;
    public RecyclerSwipeListener(InboxAdapter inboxAdapter, int reqCode) {
        super(ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT);
        this.inboxAdapter=inboxAdapter;
        this.reqCode=reqCode;
    }

    public RecyclerSwipeListener(SentMailAdapter sentMailAdapter, int reqCode) {
        super(ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT);
        this.sentMailAdapter = sentMailAdapter;
        this.reqCode=reqCode;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        switch (reqCode)
        {
            case 0:
            {
                inboxAdapter.remove(viewHolder.getAdapterPosition());
                break;
            }
            case 1:
            {
                sentMailAdapter.remove(viewHolder.getAdapterPosition());
                break;
            }
        }
    }


}
