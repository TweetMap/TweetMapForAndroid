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

package jp.co.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.co.tweetmap.R;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;

/**
 * Query search of tweets With the progress dialog.
 *
 * <p>This class is used when you want to do asynchronous processing with the progress dialog display.
 * If you want to use this class, create an instance of this class, you must implement the doInBackground ().
 *
 */
public class TweeterStatusLoader extends DialogFragment {

    /*************************************************************************************
     *                              Class constants                                      *
     *************************************************************************************/
    /** Log tag. */
    private static final String TAG = "TweeterStatusLoader";


    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** ProgressDialog Instance */
    private static ProgressDialog mProgressDialog = null;

    /** List of status */
    private ArrayList<TwitterStatusItem> mItemList = null;

    /** Context */
    private static Context mContext = null;


    /*************************************************************************************
     *                                Constructor                                        *
     *************************************************************************************/
    public static TweeterStatusLoader newInstance(Bundle bundle, Fragment fragment, Context context) {
        Log.e(TAG, "### newInstance() ###");
        TweeterStatusLoader instance = new TweeterStatusLoader();
        instance.setArguments(bundle);
        instance.setTargetFragment(fragment, MapUtil.REQUEST_CODE_TWEET_STATUS_LOADER);

        mContext = context;

        return instance;
    }

    /*************************************************************************************
     *                       Override Methods for DialogFragment                         *
     *************************************************************************************/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.e(TAG, "### onCreateDialog() ###");
        if (null != mProgressDialog) {
            return mProgressDialog;
        }

        String message = getArguments().getString("MESSAGE");

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        return mProgressDialog;
    }

    @Override
    public Dialog getDialog(){
        return mProgressDialog;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mProgressDialog = null;
    }

    /*************************************************************************************
     *                                  Methods                                          *
     *************************************************************************************/
    /**
     * Run a tweet search asynchronously.
     */
    public void doAsyncTask(){
        new BackgroundDataLoad().execute();
    }

    /**
     * Execute tweet Search
     */
    class BackgroundDataLoad extends AsyncTask<Void, Void, Void> {

        /*************************************************************************************
         *                       Override Methods for AsyncTask                              *
         *************************************************************************************/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            String name = getArguments().getString("NAME");
            String line = getArguments().getString("LINE");
            String field = getArguments().getString("FIELD_TYPE");

            mItemList = new ArrayList<TwitterStatusItem>();

            String[] array_str = mContext.getResources().getStringArray(R.array.search_key);
            String key = "";
            for(int i = 0; i < array_str.length; ++i) {
                key += " " + array_str[i];
            }

            String keyword = name + " OR " + line + key;

            Query query = new Query();
            query.setQuery(keyword);
            query.setLocale("ja");
            query.setCount(20);
            query.resultType(Query.RECENT);

            Log.e(TAG, "### doInBackground() keyword : " + keyword + " ###");
            try {
                QueryResult result = TwitterUtils.getTwitterInstance(mContext).search(query);
                List<twitter4j.Status> statuses = result.getTweets();
                for (twitter4j.Status status : statuses) {

                    TwitterStatusItem item = new TwitterStatusItem();

                    // Get timestamp
                    Date timestamp = status.getCreatedAt();
                    Calendar tweetTime = Calendar.getInstance();
                    tweetTime.setTime(timestamp);

                    // Set the search scope
                    int type = Calendar.MINUTE;
                    if (null != field) {
                        if (field.equals("HOUR_OF_DAY")) {
                            type = Calendar.HOUR_OF_DAY;
                        } else if (field.equals("DATE")) {
                            type = Calendar.DATE;
                        } else {
                            //N.O.P
                        }
                    }
                    int range = getArguments().getInt("RANGE");
                    Calendar searchTime = Calendar.getInstance();
                    searchTime.add(type, range);
                    int diff = tweetTime.compareTo(searchTime);

                    // Add the tweet in the scope to the list
                    if (diff >= 0) {
                        // Set Icon image to adapter
                        URL imageUrl = new URL(status.getUser().getProfileImageURL().toString());
                        InputStream imageIs = imageUrl.openStream();
                        Bitmap image = BitmapFactory.decodeStream(imageIs);
                        item.setProfileImage(image);

                        // Set user name to adapter
                        item.setUserName(status.getUser().getScreenName());

                        // Set message to adapter
                        item.setMessage(status.getText());

                        // Set timestamp to adapter
                        item.setTimestamp(timestamp.toString());

                        // Add item
                        mItemList.add(item);
                    }
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (getTargetFragment() != null) {
                Intent intent = new Intent();
                intent.putExtra("LIST", mItemList);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            }

            mProgressDialog.dismiss();
        }
    }
}