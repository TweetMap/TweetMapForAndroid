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

package jp.co.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import jp.co.tweetmap.R;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Class for using the Twitter4J library operation of Twitter of instances and access token.
 *
 * @see <a href="http://twitter4j.org/ja/index.html">Twitter4J</a>
 */
public class TwitterUtils {

    /*************************************************************************************
     *                              Class constants                                      *
     *************************************************************************************/
    /** Access Token */
    private static final String ACCESS_TOKEN = "access_token";
    /** Access Token Secret */
    private static final String ACCESS_TOKEN_SECRET = "token_secret";
    /** Name of the preference to save the Access Token and Secret */
    private static final String PREF_NAME = "twitter_access_token";

    /**
     * Gets the Twitter instance. Access token is automatically set if it is saved.
     *
     * @param context {@link Context}, not {@code null}.
     * @return Twitter instance.
     */
    public static Twitter getTwitterInstance(Context context) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret_key);

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        if (hasAccessToken(context)) {
            twitter.setOAuthAccessToken(loadAccessToken(context));
        }
        return twitter;
    }

    /**
     * Save the access token to shared preference.
     *
     * @param context {@link Context}, not {@code null}.
     * @param accessToken {@link AccessToken}.
     */
    public static void storeAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(ACCESS_TOKEN, accessToken.getToken());
        editor.putString(ACCESS_TOKEN_SECRET, accessToken.getTokenSecret());
        editor.apply();
    }

    /**
     * Load the access token to shared preference.
     *
     * @param context {@link Context}, not {@code null}.
     * @return access token to use Twitter API.
     */
    public static AccessToken loadAccessToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String token = preferences.getString(ACCESS_TOKEN, null);
        String tokenSecret = preferences.getString(ACCESS_TOKEN_SECRET, null);
        if (token != null && tokenSecret != null) {
            return new AccessToken(token, tokenSecret);
        } else {
            return null;
        }
    }

    /**
     * Check the access token are saved shared preferences.
     *
     * @param context {@link Context}, not {@code null}.
     * @return {@code true} if access token is stored, {@code false} otherwise.
     */
    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }
}