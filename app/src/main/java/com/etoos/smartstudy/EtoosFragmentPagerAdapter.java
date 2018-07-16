package com.etoos.smartstudy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

public class EtoosFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments = new ArrayList<>();

	public EtoosFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	//ADD PAGE
	public void addFragment(Fragment f) {
		fragments.add(f);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return null;
	}
}
