package com.etoos.smartstudy;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.etoos.smartstudy.data.EtoosConstant;
import com.etoos.smartstudy.data.EtoosData;
import com.etoos.smartstudy.data.EtoosUrls;
import com.etoos.smartstudy.fragment.CordovaFragment;
import com.etoos.smartstudy.utils.CommonUtils;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {
	private String TAG = "EtoosSmartStudy";

	EtoosViewPager viewPager;
	TabLayout tabLayout;
	private View splashScreen;
	private int initStartPage;
	private boolean doubleBackToExitPressedOnce = false;
	private boolean[] isMenuLoaded = {false, false, false, false, false};

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

		pagerAdapter.addFragment(CordovaFragment.newInstance(0));
		pagerAdapter.addFragment(CordovaFragment.newInstance(1));
		pagerAdapter.addFragment(CordovaFragment.newInstance(2));
		pagerAdapter.addFragment(CordovaFragment.newInstance(3));
		pagerAdapter.addFragment(CordovaFragment.newInstance(4));

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		viewPager = findViewById(R.id.viewpager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(4);
		viewPager.setPageTransformer(false, new FadePageTransformer());
		viewPager.addOnPageChangeListener(this);

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
		handler.postDelayed(this::removeSplashScreen, 1500);
	}

	private void selectPage(int pageIndex){
		CommonUtils.showLoader(this);

		if (viewPager.getCurrentItem() != pageIndex) {
			tabLayout.setScrollPosition(pageIndex, 0f, true);
			viewPager.setCurrentItem(pageIndex);
		}

		isMenuLoaded[initStartPage] = true;
	}

	private Fragment findFragmentByPosition(int position) {
		return getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + pagerAdapter.getItemId(position));
	}

	@Override
	public void onTabSelected(TabLayout.Tab tab) {

		if (!isMenuLoaded[tab.getPosition()]) {
			CommonUtils.showLoader(this);
		}

		int prevPosition = viewPager.getCurrentItem();
		boolean isSmoothSlide = true;
		if ((tab.getPosition() - prevPosition) > 1 || (prevPosition - tab.getPosition()) > 1) {
			isSmoothSlide = false;
		}

		viewPager.setCurrentItem(tab.getPosition(), isSmoothSlide);

		CordovaFragment fragment = (CordovaFragment) findFragmentByPosition(tab.getPosition());

		switch (tab.getPosition()) {
			case 0:
				tab.setIcon(R.drawable.icon_home_on);
				if (fragment.appView.getUrl().matches("(?i).*/www/app/index.html")) {
					fragment.setHeaderTitle("home", this, EtoosData.getGradeName(getApplicationContext()), EtoosUrls.HOME);
				}
				break;
			case 1:
				tab.setIcon(R.drawable.icon_study_list_on);
				fragment.setHeaderTitle("sub", this, EtoosConstant.TITLE_STUDY_LIST, EtoosUrls.STUDY_LIST);
				break;
			case 2:
				tab.setIcon(R.drawable.icon_favorite_on);
				fragment.setHeaderTitle("sub", this, EtoosConstant.TITLE_FAVORITE, EtoosUrls.FAVORITE);
				break;
			case 3:
				tab.setIcon(R.drawable.icon_download_on);
				fragment.setHeaderTitle("sub", this, EtoosConstant.TITLE_DOWNLOAD, EtoosUrls.DOWNLOAD);
				break;
			case 4:
				tab.setIcon(R.drawable.icon_user_on);
				fragment.setHeaderTitle("sub", this, EtoosConstant.TITLE_USER, EtoosUrls.USER);
				break;
		}

		if (!isSmoothSlide) {
			fragment.willBeDisplayed();
		}

		fragment.onTabSelected();
		isMenuLoaded[tab.getPosition()] = true;
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
		CordovaFragment fragment = (CordovaFragment) findFragmentByPosition(tab.getPosition());
		fragment.onTabReselected();
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

	public class FadePageTransformer implements ViewPager.PageTransformer {

		public void transformPage(View view, float position) {
			if (position <= -1.0F || position >= 1.0F) {
				view.setTranslationX(view.getWidth() * position);
				view.setAlpha(0.0F);
			} else if ( position == 0.0F ) {
				view.setTranslationX(view.getWidth() * position);
				view.setAlpha(1.0F);
			} else {
				// position is between -1.0F & 0.0F OR 0.0F & 1.0F
				view.setTranslationX(view.getWidth() * -position);
				view.setAlpha(1.0F - Math.abs(position));
			}
		}
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
				splashScreen.setVisibility(View.GONE);
				splashScreen = null;
				selectPage(initStartPage);
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

	@Override
	public void onBackPressed() {

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		String url = "";
		if (viewPager.getCurrentItem() == 0) {
			url = ((CordovaFragment) findFragmentByPosition(0)).getAppView().getUrl();
		}

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			return true;
		} else {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
					&& (url.matches("(?i).*/www/app/index.html") || viewPager.getCurrentItem() > 0)) {

				fnAppCloseMessageShow();
				return true;
			} else {
				super.dispatchKeyEvent(event);
			}
		}

		return true;
	}

	private void fnAppCloseMessageShow() {

		if (doubleBackToExitPressedOnce) {
			finish();
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(getApplicationContext(), "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
	}
}