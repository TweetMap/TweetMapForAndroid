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

/**
 * Utility class for map.
 */
public class MapUtil {

    /*************************************************************************************
     *                              Class constants                                      *
     *************************************************************************************/
    /** Equatorial radius */
    public static final double EQUATORIAL_RADIUS =  6378.137;

    /** Equatorial radius */
    public static final double TOKYO_STATION_LATITUDE =  35.681382;

    /** Equatorial radius */
    public static final double TOKYO_STATION_LONGITUDE =  139.766084;

    /** Web API(express.heartrails) URL */
    public static final String WEB_API_URL = "http://express.heartrails.com/api/json?method=getStations&";

    /** Request code for and receive the results from StationSearchActivity for Fragment0 */
    public static final int REQUEST_CODE_FRAGMENT_0 =0x0000;

    /** Request code for and receive the results from StationSearchActivity for Fragment1*/
    public static final int REQUEST_CODE_FRAGMENT_1 =0x0001;

    /** Request code for and receive the results from StationSearchActivity for Fragment2 */
    public static final int REQUEST_CODE_FRAGMENT_2 =0x0010;

    /** Request code of Tweet search for callback */
    public static final int REQUEST_CODE_TWEET_STATUS_LOADER = 100;

}
