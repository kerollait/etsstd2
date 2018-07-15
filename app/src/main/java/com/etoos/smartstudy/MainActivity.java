package com.etoos.smartstudy;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import com.etoos.smartstudy.fragment.DownloadFragment;
import com.etoos.smartstudy.fragment.FavoriteFragment;
import com.etoos.smartstudy.fragment.HomeFragment;
import com.etoos.smartstudy.fragment.StudyListFragment;
import com.etoos.smartstudy.fragment.UserFragment;
import com.etoos.smartstudy.utils.CommonUtils;

import org.apache.cordova.engine.SystemWebView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {
	private String TAG = "EtoosSmartStudy";

	EtoosViewPager viewPager;
	TabLayout tabLayout;
	private View splashScreen;
	private int initStartPage;

	EtoosFragmentPagerAdapter pagerAdapter;

	private int[] imageResId = {
			R.drawable.icon_home,
			R.drawable.icon_study_list,
			R.drawable.icon_favorite,
			R.drawable.icon_download,
			R.drawable.icon_user
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initStartPage = 0;

		pagerAdapter = new EtoosFragmentPagerAdapter(this.getSupportFragmentManager());
		pagerAdapter.addFragment(new HomeFragment());
		pagerAdapter.addFragment(new StudyListFragment());
		pagerAdapter.addFragment(new FavoriteFragment());
		pagerAdapter.addFragment(new DownloadFragment());
		pagerAdapter.addFragment(new UserFragment());

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		viewPager = findViewById(R.id.viewpager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(1);

		tabLayout = findViewById(R.id.tabs);
		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
		tabLayout.setupWithViewPager(viewPager);
		tabLayout.addOnTabSelectedListener(this);

		for (int i = 0; i < imageResId.length; i++) {
			Objects.requireNonNull(tabLayout.getTabAt(i)).setIcon(imageResId[i]);
		}

		Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.icon_home_on);

		splashScreen = findViewById(R.id.splash_screen);

		final Handler handler = new Handler();
		handler.postDelayed(this::removeSplashScreen, 1000);
	}

	private void selectPage(int pageIndex){
		if (viewPager.getCurrentItem() != pageIndex) {
			tabLayout.setScrollPosition(pageIndex, 0f, true);
			viewPager.setCurrentItem(pageIndex);
		}
	}

	private Fragment findFragmentByPosition(int position) {
		return getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + pagerAdapter.getItemId(position));
	}

	public void onTabSelected(TabLayout.Tab tab) {
		viewPager.setCurrentItem(tab.getPosition());
		Fragment fragment = findFragmentByPosition(tab.getPosition());

		switch (tab.getPosition()) {
			case 0:
				tab.setIcon(R.drawable.icon_home_on);
				((HomeFragment) fragment).onTabSelected();
				break;
			case 1:
				tab.setIcon(R.drawable.icon_study_list_on);
				((StudyListFragment) fragment).onTabSelected();
				break;
			case 2:
				tab.setIcon(R.drawable.icon_favorite_on);
				((FavoriteFragment) fragment).onTabSelected();
				break;
			case 3:
				tab.setIcon(R.drawable.icon_download_on);
				((DownloadFragment) fragment).onTabSelected();
				break;
			case 4:
				tab.setIcon(R.drawable.icon_user_on);
				((UserFragment) fragment).onTabSelected();
				break;
		}
	}

	@Override
	public void onTabUnselected(TabLayout.Tab tab) {
		switch (tab.getPosition()) {
			case 0:
				tab.setIcon(R.drawable.icon_home);
				break;
			case 1:
				tab.setIcon(R.drawable.icon_study_list);
				break;
			case 2:
				tab.setIcon(R.drawable.icon_favorite);
				break;
			case 3:
				tab.setIcon(R.drawable.icon_download);
				break;
			case 4:
				tab.setIcon(R.drawable.icon_user);
				break;
		}
	}

	@Override
	public void onTabReselected(TabLayout.Tab tab) {
		Fragment fragment = findFragmentByPosition(tab.getPosition());

		switch (tab.getPosition()) {
			case 0:
				((HomeFragment) fragment).onTabReselected();
				break;
			case 1:
				((StudyListFragment) fragment).onTabReselected();
				break;
			case 2:
				((FavoriteFragment) fragment).onTabReselected();
				break;
			case 3:
				((DownloadFragment) fragment).onTabReselected();
				break;
			case 4:
				((UserFragment) fragment).onTabReselected();
				break;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {

	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	private void removeSplashScreen() {
		AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new DecelerateInterpolator());
		fadeOut.setDuration(200);

		splashScreen.setAnimation(fadeOut);
		splashScreen.startAnimation(fadeOut);

		fadeOut.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				selectPage(initStartPage);
				splashScreen.setVisibility(View.GONE);
				splashScreen = null;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * Called when the activity will start interacting with the user.
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Called when the activity is no longer visible to the user.
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * Called when the activity is becoming visible to the user.
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}