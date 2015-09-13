/*
 * Copyright 2015 TweetMap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.tweetmap;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Fragment for App Open Source License.
 */
public class OpenSourceLicenseFragment extends Fragment {
    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        // Create layout
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_open_source_license, container, false);

        // Set HTML
        WebView webView = (WebView) layout.findViewById(R.id.openSourceLicenseView);
        WebSettings settings = webView.getSettings();
        settings.setTextZoom(100);
        webView.loadUrl("file:///android_asset/html/open_source_license.html");

        return layout;
    }

    @Override
    public final void onStart() {
        super.onStart();

        // Set title text
        getActivity().setTitle(R.string.oss_title_bar_name);
    }
}