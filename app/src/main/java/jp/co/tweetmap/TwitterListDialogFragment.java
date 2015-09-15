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


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import jp.co.tweetmap.util.TwitterListAdapter;

/**
 * Dialog Fragment for TweetList..
 */
public class TwitterListDialogFragment extends DialogFragment {

    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** Dialog Title String */
    private String mTitle;
    /** Dialog List Adapter */
    private TwitterListAdapter mAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_tweet_list, null);

        // Set list adapter
        ListView tweets = (ListView)v.findViewById(R.id.custom_list);
        tweets.setAdapter(mAdapter);

        TextView emptyTextView = (TextView)v.findViewById(R.id.tweet_list_empty_text);
        tweets.setEmptyView(emptyTextView);

        // Set dialog title string
        TextView title = (TextView)v.findViewById(R.id.custom_list_title);
        title.setText(mTitle);

        // Set close button image
        ImageView closeButton = (ImageView)v.findViewById(R.id.close_tweet_list_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(v);

        return builder.create();
    }

    /*************************************************************************************
     *                                  Setter Methods                                   *
     *************************************************************************************/
    /**
     * Set the title of the dialog
     *
     * @param title Dialog title string.
     */
    public void setTitle (String title){
        this.mTitle = title;
    }

    /**
     * Set the adapter for the list display of dialog.
     *
     * @param adapter tweet list adapter.
     */
    public void setAdapter (TwitterListAdapter adapter){
        this.mAdapter = adapter;
    }
}