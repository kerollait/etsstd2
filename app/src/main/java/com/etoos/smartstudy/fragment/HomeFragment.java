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

import android.os.Bundle;

public class HomeFragment extends CordovaFragment {
	public String TAG = "EtoosSmartStudy - HomeFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		super.init();

		super.loadUrl("file:///android_asset/www/app/index.html");
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (appView != null) {
				loadUrl("javascript:etoos.setHeaderTitle('home', EtoosData.getGradeName(), EtoosServiceUrl.home);");
			}
		} else {

		}
	}
}
