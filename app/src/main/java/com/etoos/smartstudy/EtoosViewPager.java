package com.etoos.smartstudy;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.apache.cordova.engine.SystemWebView;

public class EtoosViewPager extends ViewPager {

	public EtoosViewPager(Context context) {
		super(context);
	}

	public EtoosViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.getPagingStatus() && super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		try {
			return this.getPagingStatus() && super.onInterceptTouchEvent(event);
		}
		catch(IllegalArgumentException exception){
			exception.printStackTrace();
		}
		return false;

	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		return this.getPagingStatus() && super.canScroll(v, checkV, dx, x, y);
	}

	@Override
	public void removeView(View view) {
		super.removeView(view);
	}


	public Boolean getPagingStatus() {
		return SystemWebView.viewPagerState;
	}

}
