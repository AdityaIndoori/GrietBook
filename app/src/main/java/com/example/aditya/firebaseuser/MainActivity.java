package com.example.aditya.firebaseuser;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public static final int NUM_PAGES = 2;
    static ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPager = (ViewPager)findViewById(R.id.pager);
        context = this;
        mPagerAdapter = new LoginRegisterPagerAdapter(getSupportFragmentManager(),context);
        mPager.setAdapter(mPagerAdapter);
        ViewPagerTab tabsStrip = (ViewPagerTab) findViewById(R.id.tabs);
        tabsStrip.setTextSize(32);
        tabsStrip.setViewPager(mPager);
    }
}
