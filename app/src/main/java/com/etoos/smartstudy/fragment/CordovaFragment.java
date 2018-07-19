/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.etoos.smartstudy.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etoos.smartstudy.EtoosSwipeToRefresh;
import com.etoos.smartstudy.EtoosTabLayout;
import com.etoos.smartstudy.R;
import com.etoos.smartstudy.data.EtoosConstant;
import com.etoos.smartstudy.data.EtoosData;
import com.etoos.smartstudy.data.EtoosUrls;

import org.apache.cordova.BuildConfig;
import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.PluginManager;
import org.apache.cordova.engine.SystemWebViewClient;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * This class is the main Android activity that represents the Cordova
 * application. It should be extended by the user to load the specific
 * html file that contains the application.
 * <p>
 * As an example:
 * <p>
 * <pre>
 *     package org.apache.cordova.examples;
 *
 *     import android.os.Bundle;
 *     import org.apache.cordova.*;
 *
 *     public class Example extends CordovaActivity {
 *       &#64;Override
 *       public void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         super.init();
 *         // Load your application
 *         loadUrl(launchUrl);
 *       }
 *     }
 * </pre>
 * <p>
 * Cordova xml configuration: Cordova uses a configuration file at
 * res/xml/config.xml to specify its settings. See "The config.xml File"
 * guide in cordova-docs at http://cordova.apache.org/docs for the documentation
 * for the configuration. The use of the set*Property() methods is
 * deprecated in favor of the config.xml file.
 */
public class CordovaFragment extends Fragment {

	public static String TAG = "EtoosSmartStudy";

	// The webview for our app
	public CordovaWebView appView;

	public CordovaWebView getAppView() {
		return appView;
	}

	// Keep app running when pause is received. (default = true)
	// If true, then the JavaScript and native code continue to run in the background
	// when another application (activity) is started.
	protected boolean keepRunning = true;

	// Read from config.xml:
	protected CordovaPreferences preferences;
	protected String launchUrl;
	protected ArrayList<PluginEntry> pluginEntries;
	protected CordovaInterfaceImpl cordovaInterface;

	private View contentView;
	private boolean isUrlLoaded = false;
	private int index;

	public static CordovaFragment newInstance(int index, boolean isUrlLoaded) {
		CordovaFragment fragment = new CordovaFragment();
		Bundle b = new Bundle();
		b.putInt("index", index);
		b.putBoolean("initUrlLoad", isUrlLoaded);

		fragment.setArguments(b);

		return fragment;
	}


	public View getContentView() {
		return contentView;
	}

	public void setContentView(View contentView) {
		this.contentView = contentView;
	}

	public void onTabSelected() {
		if (appView != null) {
			appView.loadUrl("javascript:onTabSelected();");
		}
	}

	public void onTabReselected() {
		if (appView != null) {
			appView.loadUrl("javascript:onTabReselected();");
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)	{
		super.setUserVisibleHint(isVisibleToUser);

		if (appView != null) {
			if (!isUrlLoaded) {
				appView.loadUrl(launchUrl);
				isUrlLoaded = true;
			} else {
				if (isVisibleToUser) {
					appView.loadUrl("javascript:setUserVisibleHint(true);");
				} else {
					appView.loadUrl("javascript:setUserVisibleHint(false);");
				}
			}
		}
	}

	public void setHeaderTitle(String type, final Activity activity, String title, final String headerLink) {
		if ("home".equals(type)) {
			activity.runOnUiThread(() -> {
				String gradeName = EtoosData.getGradeName(Objects.requireNonNull(getContext()));

				activity.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);
				activity.findViewById(R.id.tv_title).setVisibility(View.GONE);

				TextView titleGrade = activity.findViewById(R.id.tv_title_grade);
				titleGrade.setText(gradeName);

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
					Typeface typeface = ResourcesCompat.getFont(activity.getApplicationContext(), R.font.noto_sans_kr);
					titleGrade.setTypeface(typeface, Typeface.BOLD);
				}

				if (!TextUtils.isEmpty(headerLink)) {
					activity.findViewById(R.id.ll_title).setOnClickListener(view -> appView.loadUrlIntoView(headerLink, false));
				}
			});
		} else {
			activity.runOnUiThread(() -> {
				TextView tvTitle = activity.findViewById(R.id.tv_title);
				tvTitle.setText(title);

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
					Typeface typeface = ResourcesCompat.getFont(activity.getApplicationContext(), R.font.noto_sans_kr);
					tvTitle.setTypeface(typeface, Typeface.BOLD);
				}

				if (!TextUtils.isEmpty(headerLink)) {
					tvTitle.setOnClickListener(view -> {
						appView.loadUrlIntoView(headerLink, false);
					});
				}

				activity.findViewById(R.id.ll_title).setVisibility(View.GONE);
				tvTitle.setVisibility(View.VISIBLE);
			});

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (contentView == null) {
			init();
		}

		if (isUrlLoaded) {
			appView.loadUrl(launchUrl);
		} else {
			appView.loadUrl("about:blank");
		}

		return contentView;
	}

	/**
	 * Called when the activity is first created.
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		index = getArguments() != null ? getArguments().getInt("index", 0) : 0;
		isUrlLoaded = getArguments() != null && getArguments().getBoolean("initUrlLoad", false);

		if (index == 0) {
			launchUrl = EtoosUrls.HOME;
		} else if (index == 1) {
			launchUrl = EtoosUrls.TEACHER_LIST;
		} else if (index == 2) {
			launchUrl = EtoosUrls.STUDY_LIST;
		} else if (index == 3) {
			launchUrl = EtoosUrls.DOWNLOAD;
		} else if (index == 4) {
			launchUrl = EtoosUrls.USER;
		}

		loadConfig();

		super.onCreate(savedInstanceState);

		cordovaInterface = makeCordovaInterface();
		if (savedInstanceState != null) {
			cordovaInterface.restoreInstanceState(savedInstanceState);
		}
	}

	protected void init() {
		appView = makeWebView();
		createViews();
		if (!appView.isInitialized()) {
			appView.init(cordovaInterface, pluginEntries, preferences);
		}
		cordovaInterface.onCordovaInit(appView.getPluginManager());

		// Wire the hardware volume controls to control media if desired.
		String volumePref = preferences.getString("DefaultVolumeStream", "");
		if ("media".equals(volumePref.toLowerCase(Locale.ENGLISH))) {
			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
		}

		if (BuildConfig.DEBUG) {
			((WebView) appView.getView()).setWebViewClient(new SystemWebViewClient((SystemWebViewEngine) appView.getEngine()) {
				@Override
				public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
					handler.proceed();
				}
			});
		}
	}

	@SuppressWarnings("deprecation")
	protected void loadConfig() {
		ConfigXmlParser parser = new ConfigXmlParser();
		parser.parse(this.getActivity());
		preferences = parser.getPreferences();
		preferences.setPreferencesBundle(getActivity().getIntent().getExtras());
		pluginEntries = parser.getPluginEntries();
	}

	//Suppressing warnings in AndroidStudio
	@SuppressWarnings({"deprecation", "ResourceType"})
	protected void createViews() {

		LayoutInflater inflater = (LayoutInflater) appView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		assert inflater != null;
		View contentMain = inflater.inflate(R.layout.content_main, null);

		//Why are we setting a constant as the ID? This should be investigated
		appView.getView().setId(100);
		appView.getView().setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		EtoosSwipeToRefresh swipeLayout = contentMain.findViewById(R.id.swipeContainer);

		swipeLayout.setColorSchemeColors(getResources().getColor(R.color.defaultColor));
		swipeLayout.setOnRefreshListener(
				() -> {
					appView.loadUrl("javascript:fnSwipePullRefresh();");

					new Handler().postDelayed(() -> swipeLayout.setRefreshing(false), 1000);
				}
		);


		swipeLayout.addView(appView.getView());

		setContentView(contentMain);

		appView.getView().requestFocusFromTouch();
	}


	/**
	 * Construct the default web view object.
	 * <p>
	 * Override this to customize the webview that is used.
	 */
	protected CordovaWebView makeWebView() {
		return new CordovaWebViewImpl(makeWebViewEngine());
	}

	protected CordovaWebViewEngine makeWebViewEngine() {
		return CordovaWebViewImpl.createEngine(this.getActivity(), preferences);
	}

	protected CordovaInterfaceImpl makeCordovaInterface() {
		return new CordovaInterfaceImpl(getActivity()) {
			@Override
			public Object onMessage(String id, Object data) {
				// Plumb this to CordovaActivity.onMessage for backwards compatibility
				return CordovaFragment.this.onMessage(id, data);
			}


		};
	}

	/**
	 * Load the url into the webview.
	 */
	public void loadUrl(String url) {
		if (appView == null) {
			init();
		}

		// If keepRunning
		this.keepRunning = preferences.getBoolean("KeepRunning", true);

		appView.loadUrlIntoView(url, true);
	}

	/**
	 * Called when the activity will start interacting with the user.
	 */
	@Override
	public void onResume() {
		super.onResume();
		LOG.d(TAG, "CordovaFragResumed the activity.");

		if (this.appView == null) {
			return;
		}

		appView.loadUrl("javascript:try{cordova.fireDocumentEvent('resume');} catch(e){console.log('exception firing resume event from native');}");

		// Force window to have focus, so application always
		// receive user input. Workaround for some devices (Samsung Galaxy Note 3 at least)
		this.getActivity().getWindow().getDecorView().requestFocus();

		this.appView.handleResume(this.keepRunning);
	}

	/**
	 * Called when the activity is no longer visible to the user.
	 */
	@Override
	public void onStop() {
		super.onStop();
		LOG.d(TAG, "Stopped the activity.");

		if (this.appView == null) {
			return;
		}
		this.appView.handleStop();
	}

	/**
	 * Called when the activity is becoming visible to the user.
	 */
	@Override
	public void onStart() {
		super.onStart();
		LOG.d(TAG, "Started the activity.");

		if (this.appView == null) {
			return;
		}
		this.appView.handleStart();
	}

	/**
	 * The final call you receive before your activity is destroyed.
	 */
	@Override
	public void onDestroy() {
		LOG.d(TAG, "CordovaActivity.onDestroy()");
		super.onDestroy();

		if (this.appView != null) {
			appView.handleDestroy();
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// Capture requestCode here so that it is captured in the setActivityResultCallback() case.
		cordovaInterface.setActivityResultRequestCode(requestCode);
		super.startActivityForResult(intent, requestCode);
	}

	/**
	 * Called when an activity you launched exits, giving you the requestCode you started it with,
	 * the resultCode it returned, and any additional data from it.
	 *
	 * @param requestCode The request code originally supplied to startActivityForResult(),
	 *                    allowing you to identify who this result came from.
	 * @param resultCode  The integer result code returned by the child activity through its setResult().
	 * @param intent      An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		LOG.d(TAG, "Incoming Result. Request code = " + requestCode);
		super.onActivityResult(requestCode, resultCode, intent);
		cordovaInterface.onActivityResult(requestCode, resultCode, intent);
	}

	/**
	 * Report an error to the host application. These errors are unrecoverable (i.e. the main resource is unavailable).
	 * The errorCode parameter corresponds to one of the ERROR_* constants.
	 *
	 * @param errorCode   The error code corresponding to an ERROR_* value.
	 * @param description A String describing the error.
	 * @param failingUrl  The url that failed to load.
	 */
	public void onReceivedError(final int errorCode, final String description, final String failingUrl) {
		final CordovaFragment me = this;

		// If errorUrl specified, then load it
		final String errorUrl = preferences.getString("errorUrl", null);
		if ((errorUrl != null) && (!failingUrl.equals(errorUrl)) && (appView != null)) {
			// Load URL on UI thread
			me.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					me.appView.showWebPage(errorUrl, false, true, null);
				}
			});
		}
		// If not, then display error dialog
		else {
			final boolean exit = !(errorCode == WebViewClient.ERROR_HOST_LOOKUP);
			me.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					if (exit) {
						me.appView.getView().setVisibility(View.GONE);
						me.displayError("Application Error", description + " (" + failingUrl + ")", "OK", exit);
					}
				}
			});
		}
	}

	/**
	 * Display an error dialog and optionally exit application.
	 */
	public void displayError(final String title, final String message, final String button, final boolean exit) {
		final CordovaFragment me = this;
		me.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				try {
					AlertDialog.Builder dlg = new AlertDialog.Builder(me.getActivity());
					dlg.setMessage(message);
					dlg.setTitle(title);
					dlg.setCancelable(false);
					dlg.setPositiveButton(button,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									if (exit) {
										getActivity().finish();
									}
								}
							});
					dlg.create();
					dlg.show();
				} catch (Exception e) {
					getActivity().finish();
				}
			}
		});
	}

	/*
	 * Hook in Cordova for menu plugins
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		if (appView != null) {
			appView.getPluginManager().postMessage("onCreateOptionsMenu", menu);
		}
		super.onCreateOptionsMenu(menu, menuInflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (appView != null) {
			appView.getPluginManager().postMessage("onPrepareOptionsMenu", menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (appView != null) {
			appView.getPluginManager().postMessage("onOptionsItemSelected", item);
		}
		return true;
	}

	/**
	 * Called when a message is sent to plugin.
	 *
	 * @param id   The message id
	 * @param data The message data
	 * @return Object or null
	 */
	public Object onMessage(String id, Object data) {
		if ("onReceivedError".equals(id)) {
			JSONObject d = (JSONObject) data;
			try {
				this.onReceivedError(d.getInt("errorCode"), d.getString("description"), d.getString("url"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if ("exit".equals(id)) {
//            getActivity().finish();
		}
		return null;
	}

	public void onSaveInstanceState(Bundle outState) {
		cordovaInterface.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Called by the system when the device configuration changes while your activity is running.
	 *
	 * @param newConfig The new device configuration
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.appView == null) {
			return;
		}
		PluginManager pm = this.appView.getPluginManager();
		if (pm != null) {
			pm.onConfigurationChanged(newConfig);
		}
	}

	/**
	 * Called by the system when the user grants permissions
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[],
										   int[] grantResults) {
		try {
			cordovaInterface.onRequestPermissionResult(requestCode, permissions, grantResults);
		} catch (JSONException e) {
			LOG.d(TAG, "JSONException: Parameters fed into the method are not valid");
			e.printStackTrace();
		}

	}

	/**
	 * Called when a fragment will be displayed
	 */
	public void willBeDisplayed() {
		// Do what you want here, for example animate the content
		if (appView != null) {
			Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
			appView.getView().startAnimation(fadeIn);
		}
	}

	/**
	 * Called when a fragment will be hidden
	 */
	public void willBeHidden() {
		if (appView != null) {
			Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
			appView.getView().startAnimation(fadeOut);
		}
	}
}
