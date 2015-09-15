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


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;

import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.net.URL;

import jp.co.util.LogUtil;
import jp.co.util.TwitterUtils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Activity of the Top screen.
 */
public class MainActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener {

    /*************************************************************************************
     *                              Class constants                                      *
     *************************************************************************************/
    /** Log tag. */
    private static final String TAG = "MainActivity";

    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** DrawerLayout */
    private DrawerLayout mDrawer;

    /** DrawerToggle */
    private ActionBarDrawerToggle mDrawerToggle;

    /** Progress Dialog for loading information of Twitter */
    private static ProgressDialog mProgressDialog  = null;

    /** AsyncTask for loading information of Twitter */
    private static TwitterLoadingTask mTwitterTask  = null;

    /** Preference key of GPS alert dialog display check */
    public final static String GPS_DLG_DISPLAY = "GPS_ALERT_DIALOG_DISPLAY";

    /** GPS alert dialog display checkbox state */
    boolean isState;

    /*************************************************************************************
     *                          Override Methods for Activity                            *
     *************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check GooglePlayServices is Available
        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
            case ConnectionResult.SUCCESS:
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.google_play_services_update), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms&hl=ja"));
                startActivity(intent);
                finish();
                break;
            default:
        }

        // Initialize the toolbar
        setToolBar();

        // Initialize the status bar
        setStatusBar();

        // Initialize the drawer
        initDrawer();

        // Initialize the tabs
        initTabs();

        // Show Gps setting
        checkGpsService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpTwitterIconIfNeeded();
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mTwitterTask = null;
    }

    @Override
    public void onBackPressed() {
        if (LogUtil.isDebug()) Log.e(TAG, "### onBackPressed() called!! ###");
        if(mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Set a NavUp to toggle icon
        if (LogUtil.isDebug()) Log.e("onOptionsItemSelected", "### item : " + item.getItemId());

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*************************************************************************************
     *                    Methods to set up the screen element                           *
     *************************************************************************************/

    /**
     * Setup toolbar as actionbar, and set title text.
     */
    protected void setToolBar(){
        // Make toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        // Use the Toolbar equivalent to the ActionBar
        setSupportActionBar(toolbar);
        // Set title sting
        setTitle(getResources().getString(R.string.top_title_bar_name));
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
     * Setup drawer toggle.
     */
    private void initDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawer.setDrawerListener(mDrawerToggle);

        // Set OnClickListener to the DrawerHeader
        LinearLayout drawerHeader = (LinearLayout) findViewById(R.id.drawer_header);
        drawerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LogUtil.isDebug()) Log.e(TAG, "### onClick() drawer_header ###");
                Intent intent = new Intent(getApplicationContext(), TwitterOAuthActivity.class);
                startActivity(intent);
            }
        });

        ActionBar bar = getSupportActionBar();
        if(null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_drawer_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Setup Tabs.
     */
    private void initTabs() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager  viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setAdapter(new MapFragmentStatePagerAdapter(getSupportFragmentManager()));
        // TODO : Marge Bug FIX
        // Issue 175204: android.support.design.widget.TabLayout does not respect selected tab on setup
        // mViewPager.setCurrentItem(INIT_TAB);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Check GPS is confirmed valid, and displays an alert dialog If it is not valid.
     */
    private void checkGpsService() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (null == locationManager || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            // If check box is checked, return here.
            final SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean isHide = sharePref.getBoolean(GPS_DLG_DISPLAY, false);
            if (isHide) {
                return;
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            LayoutInflater factory = LayoutInflater.from(this);
            final View inputView = factory.inflate(R.layout.dialog_gps_alert, null);

            CheckBox checkbox = (CheckBox)inputView.findViewById(R.id.dialog_checkbox);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isState = isChecked;
                }
            });

            alertDialogBuilder.setView(inputView)
                    .setPositiveButton(getResources().getString(R.string.gps_alert_yes_button), new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.gps_alert_no_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sharePref.edit().putBoolean(GPS_DLG_DISPLAY, isState).apply();
                            dialog.cancel();
                        }
                    });

            alertDialogBuilder.setTitle(getResources().getString(R.string.gps_alert_dlg_title));
            alertDialogBuilder.setMessage(getResources().getString(R.string.gps_alert_dlg_body));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.create();
            alertDialogBuilder.show();
        }
    }

    /*************************************************************************************
     *                    Methods for load Twitter information                            *
     *************************************************************************************/
    /**
     * Check GPS is confirmed valid, and displays an alert dialog If it is not valid.
     */
    private void setUpTwitterIconIfNeeded(){
        if (TwitterUtils.hasAccessToken(this)) {
            if (mTwitterTask == null) {
                mTwitterTask = new TwitterLoadingTask();
                mTwitterTask.execute();
            }

        }
    }

    /**
     * Shows progress dialog.
     */
    private void showDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.twitter_info_loading_msg));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
    }

    /**
     * dismiss progress dialog and set null.
     */
    private void dismissDialog() {
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    /*************************************************************************************
     *                 Override Methods for OnNavigationItemSelectedListener             *
     *************************************************************************************/
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (LogUtil.isDebug()) Log.e(TAG, "### onNavigationItemSelected() ###");
        int id = item.getItemId();
        Intent intet = null;

        switch (id) {
            case R.id.drawer_menu_info:
                intet = new Intent(this, AppInfoActivity.class);
                startActivity(intet);
                break;
            case R.id.drawer_menu_help:
                intet = new Intent(this, HelpActivity.class);
                startActivity(intet);
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * From Twitter information that the user has authenticated, asynchronous processing for acquiring the user name and icon.
     *
     * <p>This class is a good choice as your default {@code AsyncTask} implementation.
     *
     */
    private class TwitterLoadingTask extends AsyncTask<Void, Integer, String> {

        /*************************************************************************************
         *                              Class variables                                      *
         *************************************************************************************/
        /** User name of Twitter that is authenticating */
        private String mTwitterUserName;

        /** User Icon of Twitter that is authenticating */
        private Bitmap mTwitterIcon;

        /*************************************************************************************
         *                         Override Methods for AsyncTask                            *
         *************************************************************************************/
        @Override
        protected void onPreExecute() {
            showDialog();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try {
                //Get user name and icon url
                Twitter twitter = TwitterUtils.getTwitterInstance(getApplicationContext());
                mTwitterUserName = twitter.getScreenName();
                User user = twitter.showUser(mTwitterUserName);
                String url = user.getProfileImageURL();
                URL iconUrl = new URL(url);
                // Create Bitmap image
                mTwitterIcon =  BitmapFactory.decodeStream(iconUrl.openConnection().getInputStream());
            } catch (IOException e) {
                // Acquisition of the icon to the failure
                mTwitterIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_log_in);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Get views
            ImageView userIcon = (ImageView) findViewById(R.id.header_image);
            TextView userName = (TextView) findViewById(R.id.header_user_name);
            // Set user name and icon
            userName.setText(mTwitterUserName);
            userIcon.setImageBitmap(mTwitterIcon);

            dismissDialog();
            mDrawerToggle.syncState();
        }
    }
}
