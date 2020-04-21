package com.informationcollector;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.informationcollector.fragments.AppListFragment;
import com.informationcollector.fragments.BatteryFragment;
import com.informationcollector.fragments.CPUFragment;
import com.informationcollector.fragments.DisplayFragment;
import com.informationcollector.fragments.GeneralFragment;
import com.informationcollector.fragments.LocationFragment;
import com.informationcollector.fragments.MemoryFragment;
import com.informationcollector.fragments.NetworkFragment;
import com.informationcollector.fragments.OSFragment;
import com.informationcollector.fragments.OperatorFragment;
import com.informationcollector.fragments.PlaceholderFragment;
import com.informationcollector.fragments.SensorFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{
            R.string.tab_general,
            R.string.tab_cpu,
            R.string.tab_memory,
            R.string.tab_display,
            R.string.tab_battery,
            R.string.tab_network,
            R.string.tab_location,
            R.string.tab_os,
            R.string.tab_app_list,
            R.string.tab_operator,
            R.string.tab_sensor
    };
    private final Context mContext;

    TabPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new GeneralFragment();
            case 1:
                return new CPUFragment();
            case 2:
                return new MemoryFragment();
            case 3:
                return new DisplayFragment();
            case 4:
                return new BatteryFragment();
            case 5:
                return new NetworkFragment();
            case 6:
                return new LocationFragment();
            case 7:
                return new OSFragment();
            case 8:
                return new AppListFragment();
            case 9:
                return new OperatorFragment();
            case 10:
                return new SensorFragment();
            default:
                return new PlaceholderFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}