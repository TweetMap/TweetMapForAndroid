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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jp.co.tweetmap.R;

/**
 * Adapter for tweet list display.
 */
public class TwitterListAdapter extends ArrayAdapter<TwitterStatusItem> {

    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** Inflater of Tweet list screen generate */
    private LayoutInflater inflater;


    /*************************************************************************************
     *                                Constructor                                        *
     *************************************************************************************/
    /**
     * Constructor.
     *
     * @param context {@link Context}
     * @param resource resource id.
     * @param objects list of status.
     */
    public TwitterListAdapter(Context context, int resource, List<TwitterStatusItem> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*************************************************************************************
     *                      Override Methods for ArrayAdapter                            *
     *************************************************************************************/
    @Override
    public View getView(int position, View v, ViewGroup parent) {
        TwitterStatusItem item = getItem(position);
        if (null == v) {
            v = inflater.inflate(R.layout.listview_tweet_list, null);
        }

        // Set User Icon
        ImageView twitterIcon = (ImageView)v.findViewById(R.id.twitter_prof_icon);
        twitterIcon.setImageBitmap(item.getProfileImage());
        // Set User name
        TextView userName = (TextView)v.findViewById(R.id.tweet_user);
        userName.setText(item.getUserName());
        // Set Message
        TextView message = (TextView)v.findViewById(R.id.tweet_msg);
        message.setText(item.getMessage());
        // Set Message
        TextView timestamp = (TextView)v.findViewById(R.id.tweet_timestamp);
        timestamp.setText("["+item.getTimestamp()+"]");
        return v;
    }
}
