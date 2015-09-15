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

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import jp.co.tweetmap.util.LogUtil;
import jp.co.tweetmap.util.TwitterUtils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Activity of Twitter OAuth screen.
 */
public class TwitterOAuthActivity extends ActionBarActivity {

    /*************************************************************************************
     *                              Class constants                                      *
     *************************************************************************************/
    /** Log tag. */
    private static final String TAG = "TwitterOAuthActivity";
    /** Twitter Web Page URL */
    private static final String TWITTER_WEB_URL = "<a href=\"https://twitter.com/?lang=ja\">Twitterアカウントを新規作成</a>";

    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** Call back URL for Twitter OAuth */
    private String mCallbackURL;
    /** Twitter Instance */
    private Twitter mTwitter;
    /** Request Token for Twitter OAuth */
    private RequestToken mRequestToken;


    /*************************************************************************************
     *                          Override Methods for Activity                            *
     *************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (LogUtil.isDebug()) Log.e(TAG, "### onCreate() ###");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_oauth);


        // Set HTML
        TextView newAccount = (TextView) findViewById(R.id.text_twitter_url);
        MovementMethod movementmethod = LinkMovementMethod.getInstance();
        newAccount.setMovementMethod(movementmethod);
        CharSequence spanned = Html.fromHtml(TWITTER_WEB_URL);
        newAccount.setText(spanned);

        // Initialize the toolbar
        setToolbar();

        // Initialize the status bar
        setStatusBar();

        mCallbackURL = getString(R.string.twitter_callback_url);
        mTwitter = TwitterUtils.getTwitterInstance(this);

        findViewById(R.id.action_start_oauth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TwitterUtils.hasAccessToken(getApplicationContext())) {
                    startAuthorize();
                } else {
                    showToast(getResources().getString(R.string.drawer_header_toast));
                }
            }
        });
        /*
        findViewById(R.id.action_cancel_oauth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        */
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (LogUtil.isDebug()) Log.e(TAG, "### onNewIntent() ###");
        if (intent == null
                || intent.getData() == null
                || !intent.getData().toString().startsWith(mCallbackURL)) {
            return;
        }
        String verifier = intent.getData().getQueryParameter("oauth_verifier");

        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {

            @Override
            protected AccessToken doInBackground(String... params) {
                if (LogUtil.isDebug()) Log.e(TAG, "### AccessToken doInBackground() ###");
                try {
                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (LogUtil.isDebug()) Log.e(TAG, "### onPostExecute() ###");
                if (accessToken != null) {
                    showToast(getResources().getString(R.string.toast_oauth_complete_text));
                    successOAuth(accessToken);
                } else {
                    showToast(getResources().getString(R.string.toast_oauth_failed_text));
                }
            }
        };
        task.execute(verifier);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_search_tool_bar);
        // Use the Toolbar equivalent to the ActionBar
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if(null != bar) {
            bar.setDisplayShowHomeEnabled(false);
            bar.setDisplayShowTitleEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getResources().getString(R.string.init_title_bar_name));
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

    /*************************************************************************************
     *                       Methods for authenticate with Twitter                      *
     *************************************************************************************/
    /**
     * Create a request token, to start the Twitter login page in a browser application.
     *
     */
    private void startAuthorize() {
        if (LogUtil.isDebug()) Log.e(TAG, "### startAuthorize() ###");
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (LogUtil.isDebug()) Log.e(TAG, "### RequestToken doInBackground() ###");
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken(mCallbackURL);
                    return mRequestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                if (LogUtil.isDebug()) Log.e(TAG, "### onPostExecute() ###");
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            }
        };
        task.execute();
    }

    /**
     * Stores the acquired access token preferences
     * it called at the time of access token success.
     *
     * @param accessToken {@link AccessToken}
     */
    private void successOAuth(AccessToken accessToken) {
        if (LogUtil.isDebug()) Log.e(TAG, "### successOAuth() ###");
        TwitterUtils.storeAccessToken(this, accessToken);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Show toast with param string.
     *
     * @param text String to toast Display.
     */
    private void showToast(String text) {
        if (LogUtil.isDebug()) Log.e(TAG, "### showToast() ###");
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
