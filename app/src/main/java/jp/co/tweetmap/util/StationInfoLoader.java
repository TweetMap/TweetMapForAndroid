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

package jp.co.tweetmap.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * Get the nearest station information in the asynchronous processing in WebAPI.
 * Parse the body of the response
 *
 */
public class StationInfoLoader extends AsyncTaskLoader<String> {

    /*************************************************************************************
     *                              Class constants                                      *
     *************************************************************************************/
    /** Log tag. */
    private static final String TAG = "NearestStationLoader";

    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** Web API url */
    private String mUrl =null;


    /*************************************************************************************
     *                                Constructor                                        *
     *************************************************************************************/
    /**
     * Constructor.
     *
     * @param context {@link Context}
     * @param url Web API URL
     */
    public StationInfoLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    /*************************************************************************************
     *                          Override Methods for AsyncTask                           *
     *************************************************************************************/
    @Override
    public String loadInBackground() {
        if (LogUtil.isDebug()) Log.e(TAG, "### getBody() ###");
        HttpURLConnection con = null;
        String body = null;

        try {
            // Generate "GET" request and excute.
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
