/*
 * Copyright 2015 TweetMap All Right Reserved.
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

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import jp.co.util.LogUtil;

/**
 * Activity for App information screen.
 */
public class AppInfoActivity extends ActionBarActivity {

    /*************************************************************************************
     *                              Class constants                                      *
     *************************************************************************************/
    /** Log tag. */
    private static final String TAG = "AppInfoActivity";

    /*************************************************************************************
     *                          Override Methods for Activity                            *
     *************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        // Initialize the toolbar
        setToolbar();

        // Initialize the status bar
        setStatusBar();

        // Set AppInfoFragment
        if (savedInstanceState == null) {
            Fragment fragment = new AppInfoFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.app_info_container, fragment, null)
                    .commit();
        }


    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.app_info_container);
        if (!(fragment instanceof AppInfoFragment)) {
            getFragmentManager().popBackStack();
        } else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (LogUtil.isDebug()) Log.e(TAG, "### onOptionsItemSelected() id = " + item.getItemId() + " ###");

        switch (item.getItemId()) {
            case android.R.id.home:
                Fragment fragment = getFragmentManager().findFragmentById(R.id.app_info_container);
                if (!(fragment instanceof AppInfoFragment)) {
                    getFragmentManager().popBackStack();
                } else {
                    finish();
                    return true;
                }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*************************************************************************************
     *                    Methods to set up the screen element                           *
     *************************************************************************************/
    /**
     * Setup toolbar as actionbar, and set title text.
     */
    private void setToolbar(){
        // Make toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_app_info_tool_bar);
        // Use the Toolbar equivalent to the ActionBar
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayShowHomeEnabled(false);
            bar.setDisplayShowTitleEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Set status bar color as tool bar.
     */
    protected void setStatusBar(){
        // Set status bar color if the SDK version Lollipop more
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.statusbar_background));
        }
    }

    /**
     * Processing at the time of pressing open source license button.
     *
     * @param view {@link View}
     */
    public final void onClickOpenSourceLicense(final View view) {
        Fragment fragment = new OpenSourceLicenseFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.app_info_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}