package com.etoos.smartstudy;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class EtoosTabLayout extends TabLayout {

	private boolean swipeEnable = true;

	public EtoosTabLayout(Context context) {
		super(context);
	}

	public EtoosTabLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EtoosTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.swipeEnable) {
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.swipeEnable) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	public void setPagingEnabled(boolean enabled) {
		this.swipeEnable = enabled;
	}
}
