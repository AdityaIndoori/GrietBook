package com.example.aditya.firebaseuser;

import android.content.Context;
import android.util.AttributeSet;

import com.astuetz.PagerSlidingTabStrip;

/**
 * Created by aditya on 21-02-2017.
 */

public class ViewPagerTab extends PagerSlidingTabStrip {
    public ViewPagerTab(Context context) {
        super(context);
    }

    public ViewPagerTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setTextSize(int textSizePx) {
        super.setTextSize(textSizePx);
    }
}