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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import jp.co.tweetmap.util.LogUtil;
import jp.co.tweetmap.util.MapUtil;
import jp.co.tweetmap.util.StationInfo;
import jp.co.tweetmap.util.StationInfoParser;

/**
 * Activity for search station screen.
 */
public class StationSearchActivity extends ActionBarActivity {

    /*************************************************************************************
     *                              Class constants                                      *
     *************************************************************************************/
    /** Log tag. */
    private static final String TAG = "StationSearchActivity";


    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** EditText */
    private EditText mEditText;

    /** Adapter for managing search results */
    private ArrayAdapter<String> mAdapter;

    /** List of Items */
    static List<String> mDataList = new ArrayList<String>();

    /** List of Station Info*/
    private ArrayList<StationInfo> mStationInfo =  new ArrayList<StationInfo>();

    /** Progress Dialog for searching the station information */
    private static ProgressDialog mProgressDialog  = null;


    /*************************************************************************************
     *                          Override Methods for Activity                            *
     *************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staticon_search);

        // Initialize the toolbar
        setToolbar();

        // Initialize the status bar
        setStatusBar();

        // Setup EditText
        mEditText = (EditText) findViewById(R.id.edit_search);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mDataList.clear();
                    mStationInfo.clear();
                    searchStation();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });

        // SetUp List View
        ListView list = (ListView) findViewById(R.id.station_list);

        // Clear When the search results remain have
        mDataList.clear();
        mStationInfo.clear();

        // Set adapter to List view
        mAdapter = new ArrayAdapter<String>(this, R.layout.saerch_station_list_item, mDataList);
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Return the latitude and longitude of the specified station to map Fragment
                StationInfo info = mStationInfo.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putDouble("key.latitude", info.y);
                bundle.putDouble("key.longitude", info.x);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (LogUtil.isDebug()) Log.e(TAG, "### onOptionsItemSelected() id = " + item.getItemId() + " ###");

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
     *                        Methods to search station                                  *
     *************************************************************************************/
    /**
     * Prepares the URL of the Web API,and get the center position of the map.
     *
     * @see <a href="http://express.heartrails.com/api.html">Web API</a>
     */
    public void searchStation() {
        if (LogUtil.isDebug()) Log.e(TAG, "### searchStation() ###");

        try {
            // Get the input station name, and generates a request URL
            SpannableStringBuilder inputText = (SpannableStringBuilder)mEditText.getText();
            String input = URLEncoder.encode(inputText.toString(), "utf-8");
            String WebApi = MapUtil.WEB_API_URL + "name=" + input;

            // Initialize LoaderManager
            StationSearchingTask task = new StationSearchingTask(WebApi);
            task.execute();
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows progress dialog.
     */
    private void showDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.station_info_searching_msg));
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

    /**
     * Search the text entered the station in an asynchronous.
     *
     * <p>This class is a good choice as your default {@code AsyncTask} implementation.
     *
     */
    private class StationSearchingTask extends AsyncTask<Void, Integer, String> {
        /*************************************************************************************
         *                              Class variables                                      *
         *************************************************************************************/
        /** Request URL of WebAPI */
        private String mUrl;

        /*************************************************************************************
         *                                Constructor                                        *
         *************************************************************************************/
        /**
         * Constructor.
         *
         * @param url Request URL of WebAPI.
         */
        private StationSearchingTask(String url){
            this.mUrl = url;
        }

        /*************************************************************************************
         *                         Override Methods for AsyncTask                            *
         *************************************************************************************/
        @Override
        protected void onPreExecute() {
            showDialog();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            if (LogUtil.isDebug()) Log.e(TAG, "### doInBackground() ###");
            HttpURLConnection con = null;
            String body = null;

            try {
                // Generate "GET" request and execute.
                URL url = new URL(mUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                int responseCode = con.getResponseCode();
                if( responseCode == HttpURLConnection.HTTP_OK ){
                    body = readIt(con.getInputStream());
                }
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                if(con != null) {
                    con.disconnect();
                }
            }
            return body;
        }

        @Override
        protected void onPostExecute(String result) {
            if (LogUtil.isDebug()) Log.e(TAG, "### onPostExecute() ###");
            StationInfoParser parse = new StationInfoParser();
            parse.loadJson(result);

            // If the search result is of 0, show the Toast
            if (parse.getStationInfo().size() == 0) {
                Toast.makeText(getApplicationContext(), "該当する駅がありません", Toast.LENGTH_SHORT).show();
                dismissDialog();
                return;
            }

            // If has the search result(s), and add the search result(s) into an array
            for (StationInfo info : parse.getStationInfo()) {
                mAdapter.add(info.name + "(" + info.line + ")");
                mStationInfo.add(info);
            }

            // Dismiss the dialog
            dismissDialog();
        }

        /*************************************************************************************
         *                       Methods for read response(body)                             *
         *************************************************************************************/
        /**
         * Read the results of the WebAPI in input stream.
         *
         * @param stream {@link InputStream}
         * @return response(body)
         * @throws IOException {@link IOException}
         */
        public String readIt(InputStream stream) throws IOException {
            Log.e(TAG, "### readIt() ###");
            StringBuffer sb = new StringBuffer();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            try {
                stream.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
    }
}