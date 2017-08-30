package com.project.lorvent.lcrm.utils;

import android.content.Context;
import android.util.AttributeSet;

import com.project.lorvent.lcrm.R;


public class TokenTextView extends android.support.v7.widget.AppCompatTextView {

    public TokenTextView(Context context) {
        super(context);
    }

    public TokenTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setCompoundDrawablesWithIntrinsicBounds(0, 0, selected ? R.mipmap.ic_clear_black_18dp : 0, 0);
    }
}
