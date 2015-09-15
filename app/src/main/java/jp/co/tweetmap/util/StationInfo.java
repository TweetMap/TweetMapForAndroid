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

/**
 * Class for station information.
 */
public class StationInfo {

    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** Station name */
    public String name;

    /** Prev station name.(The starting station if null) */
    public String prev;

    /** Next station name. (The terminus if null) */
    public String next;

    /** Longitude of nearest station  */
    public Double x;

    /** Latitude of nearest station */
    public Double y;

    /** Line name of the nearest station */
    public String line;
}
