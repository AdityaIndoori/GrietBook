package com.example.aditya.firebaseuser;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import static com.example.aditya.firebaseuser.MainActivity.NUM_PAGES;

/**
 * Created by aditya on 21-02-2017.
 */
public class LoginRegisterPagerAdapter extends FragmentStatePagerAdapter {

    Context context;
    public LoginRegisterPagerAdapter(android.support.v4.app.FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new LoginFragment();
            case 1: return new RegisterFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return context.getString(R.string.login_heading);
            case 1: return context.getString(R.string.register_heading);
            default: return null;
        }
    }
}