package com.arcusweather.androiddevutilities.adapter;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.arcusweather.androiddevutilities.fragment.InfoFragment;
 
public class MainFragmentPagerAdapter extends FragmentStatePagerAdapter{
 
    final int PAGE_COUNT = 1;
    public Location mFusedData;
 
    /** Constructor of the class */
    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
	@Override
	public Fragment getItem(int position) {
		InfoFragment fragment = new InfoFragment();
		fragment.setFusedData(mFusedData);
		return fragment;
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return "Info";
		}
		return null;
	}

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }
    
    public void setData(Location fusedData)
    {
    	mFusedData = fusedData;
    }
}