package com.etoos.smartstudy.plugin;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.etoos.smartstudy.R;
import com.etoos.smartstudy.utils.CommonUtils;
import com.etoos.smartstudy.data.EtoosData;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class EtoosPlugin extends CordovaPlugin {

	private static final String LOG_TAG = "EtoosPlugin";
	private static CallbackContext subscribeCallbackContext = null;
	private static CallbackContext subscribeExitCallbackContext = null;
	private static JSONArray results = null;
	private boolean doubleBackToExitPressedOnce = false;

	/**
	 * Executes the request and returns PluginResult.
	 *
	 * @param action          The action to execute.
	 * @param args            JSONArry of arguments for the plugin.
	 * @param callbackContext The callback id used when calling back into JavaScript.
	 * @return True if the action was valid, false if not.
	 */
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("showLoading")) {
			LOG.d(LOG_TAG, "showLoading");

			CommonUtils.showLoader(this.cordova.getActivity());

			JSONObject r = new JSONObject();
			r.put("responseCode", "ok");
			callbackContext.success(r);
		} else if (action.equals("hideLoading")) {
			LOG.d(LOG_TAG, "hideLoading");

			CommonUtils.hideLoader(this.cordova.getActivity());

			JSONObject r = new JSONObject();
			r.put("responseCode", "ok");
			callbackContext.success(r);
		} else if (action.equals("setLoginToken")) {
			LOG.d(LOG_TAG, "setLoginToken : args = " + args);

			if (args != null) {
				String token = "";
				try {
					token = args.getString(0);
				} catch (Exception e) {

				}

				EtoosData.setToken(cordova.getContext(), token);
				callbackContext.success("ok");
			} else {
				callbackContext.error("error");
			}
		} else if (action.equals("setGrade")) {
			LOG.d(LOG_TAG, "setGrade : args = " + args);

			if (args != null) {
				String grade = "";
				try {
					grade = args.getString(0);
				} catch (Exception e) {

				}

				EtoosData.setGrade(cordova.getContext(), grade);
				callbackContext.success("ok");
			} else {
				callbackContext.error("error");
			}
		} else if (action.equals("setGradeName")) {
			LOG.d(LOG_TAG, "setGradeName : args = " + args);

			if (args != null) {
				String gradeName = "";
				try {
					gradeName = args.getString(0);
				} catch (Exception e) {

				}

				EtoosData.setGradeName(cordova.getContext(), gradeName);
				callbackContext.success("ok");
			} else {
				callbackContext.error("error");
			}
		} else if (action.equals("setHeaderTitle")) {
			LOG.d(LOG_TAG, "setHeaderTitle : args = " + args);

			if (args != null) {
				cordova.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String headerType = "home";
						String title = "";
						String titleLink = "";

						try {
							headerType = args.getString(0);
						} catch (Exception e) {
						}

						try {
							title = args.getString(1);
						} catch (Exception e) {
						}

						try {
							titleLink = args.getString(2);
						} catch (Exception e) {
						}

						try {
							if (headerType != null) {

								if (headerType.equals("home")) {
									cordova.getActivity().findViewById(R.id.ll_title).setVisibility(View.VISIBLE);
									cordova.getActivity().findViewById(R.id.tv_title).setVisibility(View.GONE);

									TextView titleGrade = cordova.getActivity().findViewById(R.id.tv_title_grade);
									titleGrade.setText(title);

									final String headerLink = titleLink;

									if (!TextUtils.isEmpty(headerLink)) {
										cordova.getActivity().findViewById(R.id.ll_title).setOnClickListener(view -> {
											webView.loadUrlIntoView(headerLink, false);
										});
									}
								} else {
									if (!TextUtils.isEmpty(title)) {
										TextView tvTitle = cordova.getActivity().findViewById(R.id.tv_title);
										tvTitle.setText(title);

										if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
											Typeface typeface = ResourcesCompat.getFont(cordova.getContext(), R.font.noto_sans_kr_black_);
											tvTitle.setTypeface(typeface);
										}

										final String headerLink = titleLink;

										if (!TextUtils.isEmpty(headerLink)) {
											tvTitle.setOnClickListener(view -> {
												webView.loadUrlIntoView(headerLink, false);
											});
										}

										cordova.getActivity().findViewById(R.id.ll_title).setVisibility(View.GONE);
										tvTitle.setVisibility(View.VISIBLE);
									} else {
										cordova.getActivity().findViewById(R.id.header).setVisibility(View.GONE);
									}
								}
							}

						} catch (Exception e) {
							LOG.e(LOG_TAG, "setHeaderTitle Exception!!");
							e.printStackTrace();
						}
					}
				});

			}

			callbackContext.success("ok");

		} else if (action.equals("exitApp")) {
			fnAppCloseMessageShow();
		} else {
			return false;
		}

		return true;
	}

	private void fnAppCloseMessageShow() {

		if (doubleBackToExitPressedOnce) {
			cordova.getActivity().finish();
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(cordova.getActivity(), "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
	}
}

