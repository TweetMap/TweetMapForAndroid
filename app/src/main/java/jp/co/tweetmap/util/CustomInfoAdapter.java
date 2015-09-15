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

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import jp.co.tweetmap.R;

/**
 * Class for displaying custom info window to be displayed on the map fragment.
 */
public class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {

    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** View of Window  */
    private final View mWindow;

    /*************************************************************************************
     *                                Constructor                                        *
     *************************************************************************************/
    /**
     * Constructor.
     *
     * @param activity activity that generated this class.
     */
    public CustomInfoAdapter(Activity activity) {
        mWindow =activity.getLayoutInflater().inflate(R.layout.info_window, null);
    }

    /*************************************************************************************
     *                      Override Methods for InfoWindowAdapter                       *
     *************************************************************************************/
    @Override
    public View getInfoWindow(Marker marker) {
//        render(marker, mWindow);
//        return mWindow;
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, mWindow);
        return mWindow;

//        return null;
    }

    /*************************************************************************************
     *                         Methods for rendering InfoWindow                          *
     *************************************************************************************/
    /**
     * Shows InfoWindow on map.
     *
     * @param marker {@link Marker}
     * @param view {@link View}
     */
    private void render(Marker marker, View view) {
        ImageView badge = (ImageView) view.findViewById(R.id.info_image);
        badge.setImageResource(R.drawable.ic_twitter);
        badge.setScaleType(ImageView.ScaleType.FIT_XY);
        TextView title = (TextView) view.findViewById(R.id.info_title);
        TextView snippet = (TextView) view.findViewById(R.id.info_snippet);
        title.setText(marker.getTitle());
        snippet.setText("("+marker.getSnippet()+")");
    }
}