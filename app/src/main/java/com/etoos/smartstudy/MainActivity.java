package com.etoos.smartstudy;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

		initStartPage = 0;
		isMenuLoaded[initStartPage] = true;

		pagerAdapter = new EtoosFragmentPagerAdapter(this.getSupportFragmentManager());

		pagerAdapter.addFragment(CordovaFragment.newInstance(0, isMenuLoaded[0]));
		pagerAdapter.addFragment(CordovaFragment.newInstance(1, isMenuLoaded[1]));
		pagerAdapter.addFragment(CordovaFragment.newInstance(2, isMenuLoaded[2]));
		pagerAdapter.addFragment(CordovaFragment.newInstance(3, isMenuLoaded[3]));
		pagerAdapter.addFragment(CordovaFragment.newInstance(4, isMenuLoaded[4]));

		viewPager = findViewById(R.id.viewpager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(4);
		viewPager.setPageTransformer(false, new FadePageTransformer());
		viewPager.addOnPageChangeListener(this);

		tabLayout = findViewById(R.id.headerTab);
		tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

		tabLayout.setupWithViewPager(viewPager);
		tabLayout.addOnTabSelectedListener(this);

		View tabMenu = getLayoutInflater().inflate(R.layout.header_tab, null);
		TextView tab1 = tabMenu.findViewById(R.id.header_tab_home);
		TextView tab2 = tabMenu.findViewById(R.id.header_tab_teacher);
		TextView tab3 = tabMenu.findViewById(R.id.header_tab_study);
		TextView tab4 = tabMenu.findViewById(R.id.header_tab_download);
		TextView tab5 = tabMenu.findViewById(R.id.header_tab_myroom);

		tab1.setText(EtoosConstant.TITLE_HOME);
		tab2.setText(EtoosConstant.TITLE_TEACHER);
		tab3.setText(EtoosConstant.TITLE_STUDY_LIST);
		tab4.setText(EtoosConstant.TITLE_DOWNLOAD);
		tab5.setText(EtoosConstant.TITLE_USER);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.noto_sans_kr_black_);
			tab1.setTypeface(typeface);
		}

		try {
			Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(tab1);
			Objects.requireNonNull(tabLayout.getTabAt(1)).setCustomView(tab2);
			Objects.requireNonNull(tabLayout.getTabAt(2)).setCustomView(tab3);
			Objects.requireNonNull(tabLayout.getTabAt(3)).setCustomView(tab4);
			Objects.requireNonNull(tabLayout.getTabAt(4)).setCustomView(tab5);
		} catch(Exception e) {
			e.printStackTrace();
		}

		wrapTabIndicatorToTitle(tabLayout, 24, 24);


		splashScreen = findViewById(R.id.splash_screen);

		final Handler handler = new Handler();
		handler.postDelayed(this::removeSplashScreen, 1500);
	}

	public void wrapTabIndicatorToTitle(TabLayout tabLayout, int externalMargin, int internalMargin) {
		View tabStrip = tabLayout.getChildAt(0);
		if (tabStrip instanceof ViewGroup) {
			ViewGroup tabStripGroup = (ViewGroup) tabStrip;
			int childCount = ((ViewGroup) tabStrip).getChildCount();
			for (int i = 0; i < childCount; i++) {
				View tabView = tabStripGroup.getChildAt(i);
				//set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
				tabView.setMinimumWidth(0);
				// set padding to 0 for wrapping indicator as title
				tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
				// setting custom margin between tabs
				if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
					ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
					if (i == 0) {
						// left
						settingMargin(layoutParams, externalMargin, internalMargin);
					} else if (i == childCount - 1) {
						// right
						settingMargin(layoutParams, internalMargin, externalMargin);
					} else {
						// internal
						settingMargin(layoutParams, internalMargin, internalMargin);
					}
				}
			}

			tabLayout.requestLayout();
		}
	}

	private void settingMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
		layoutParams.setMarginStart(start);
		layoutParams.setMarginEnd(end);
	}

	private void selectPage(int pageIndex){
		CommonUtils.showLoader(this);

		if (viewPager.getCurrentItem() != pageIndex) {
			tabLayout.setScrollPosition(pageIndex, 0f, true);
			viewPager.setCurrentItem(pageIndex);
		}


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

		if (!isSmoothSlide) {
			fragment.willBeDisplayed();
		}

		View view = tab.getCustomView();
		TextView selectedTextView = null;
		switch (tab.getPosition()) {
			case 0:
				selectedTextView = view.findViewById(R.id.header_tab_home);
				fragment.setHeaderTitle("home", this, EtoosData.getGradeName(this), EtoosUrls.HOME);
				break;

			case 1:
				selectedTextView = view.findViewById(R.id.header_tab_teacher);
				fragment.setHeaderTitle("sub", this, EtoosConstant.TITLE_TEACHER, EtoosUrls.TEACHER_LIST);
				break;

			case 2:
				selectedTextView = view.findViewById(R.id.header_tab_study);
				fragment.setHeaderTitle("sub", this, EtoosConstant.TITLE_STUDY_LIST, EtoosUrls.STUDY_LIST);
				break;

			case 3:
				selectedTextView = view.findViewById(R.id.header_tab_download);
				fragment.setHeaderTitle("sub", this, EtoosConstant.TITLE_DOWNLOAD, EtoosUrls.DOWNLOAD);
				break;

			case 4:
				selectedTextView = view.findViewById(R.id.header_tab_myroom);
				fragment.setHeaderTitle("sub", this, EtoosConstant.TITLE_USER, EtoosUrls.USER);
				break;
		}

		setTabTextColorChange(selectedTextView, R.color.defaultColor);

		fragment.onTabSelected();
		isMenuLoaded[tab.getPosition()] = true;
	}

	@Override
	public void onTabUnselected(TabLayout.Tab tab) {
		View view = tab.getCustomView();
		TextView selectedTextView = null;
		switch (tab.getPosition()) {
			case 0:
				selectedTextView = view.findViewById(R.id.header_tab_home);
				break;

			case 1:
				selectedTextView = view.findViewById(R.id.header_tab_teacher);
				break;

			case 2:
				selectedTextView = view.findViewById(R.id.header_tab_study);
				break;

			case 3:
				selectedTextView = view.findViewById(R.id.header_tab_download);
				break;

			case 4:
				selectedTextView = view.findViewById(R.id.header_tab_myroom);
				break;
		}

		setTabTextColorChange(selectedTextView, R.color.defaultTextColor);
	}

	private void setTabTextColorChange(TextView textView, int color) {
		if (textView != null) {
			textView.setTextColor(ContextCompat.getColor(getApplicationContext(), color));
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