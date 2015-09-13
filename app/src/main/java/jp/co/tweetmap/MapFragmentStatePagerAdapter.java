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

package jp.co.tweetmap;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Adapter for holding once Fragment generated when switching the page as it is in the memory.
 */
public class MapFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** Tab title strings */
    private final String[] TITLES = {"30分以内", "1時間以内", "24時間以内"};

    /*************************************************************************************
     *                                Constructor                                        *
     *************************************************************************************/
    /**
     * Constructor.
     *
     * @param manager {@link FragmentManager}.
     */
    public MapFragmentStatePagerAdapter(FragmentManager manager) {
        super(manager);
    }

    /*************************************************************************************
     *                   Override Methods for FragmentStatePagerAdapter                  *
     *************************************************************************************/
    @Override
    public Fragment getItem( int position) {
        switch (position){
            case 0 :
                return new Fragment0();
            case 1 :
                return new Fragment1();
            case 2 :
                return new Fragment2();
            default :
                return null;
        }
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle( int position) {
        return TITLES[position];
    }
}
