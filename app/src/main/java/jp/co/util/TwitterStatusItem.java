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

import android.graphics.Bitmap;

/**
 * Class of status information.
 */
public class TwitterStatusItem {
    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** Twitter icon */
    private Bitmap mProfileImage;

    /** Message Body of Tweet */
    private String mMessage;

    /** User name of Tweet */
    private String mUserName;

    /** Timestamp of Tweet */
    private String mTimestamp;

    /*************************************************************************************
     *                         Methods (Getter and Setter)                               *
     *************************************************************************************/
    /**
     * GetTwitter icon(Bitmap) of Tweet.
     *
     * @return Twitter icon of Tweet.
     */
    public Bitmap getProfileImage() {
        return mProfileImage;
    }

    /**
     * Set profile image(Bitmap) of Tweet.
     *
     * @param image profile image of Tweet.
     */
    public void setProfileImage(Bitmap image) {
        this.mProfileImage = image;
    }

    /**
     * Get message body of Tweet.
     *
     * @return message body of Tweet.
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Set message body of Tweet.
     *
     * @param msg message body of Tweet.
     */
    public void setMessage(String msg) {
        this.mMessage = msg;
    }

    /**
     * Get user name of Tweet.
     *
     * @return user name of Tweet.
     */
    public String getUserName() {
        return mUserName;
    }

    /**
     * Set user name of Tweet.
     *
     * @param name user name of Tweet.
     */
    public void setUserName(String name) {
        this.mUserName = name;
    }

    /**
     * Get timestamp of Tweet.
     *
     * @return timestamp of Tweet.
     */
    public String getTimestamp() {
        return mTimestamp;
    }

    /**
     * Set timestamp of Tweet.
     *
     * @param time timestamp of Tweet.
     */
    public void setTimestamp(String time) {
        this.mTimestamp = time;
    }

}