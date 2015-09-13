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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Parse the response of WebAPI.
 * To get line name, station name, latitude, the information of longitude, etc.
 *
 */
public class StationInfoParser extends ParseJson {

    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    /** List of Station Information */
    private List<StationInfo> mInfo = new ArrayList<StationInfo>();


    /*************************************************************************************
     *                                Constructor                                        *
     *************************************************************************************/
    /**
     * Constructor.
     *
     * @return array list of station information
     */
    public List<StationInfo> getStationInfo() {
        return mInfo;
    }


    /*************************************************************************************
     *                         Override Methods for ParseJson                            *
     *************************************************************************************/
    @Override
    public void loadJson(String str) {
        JsonNode root = getJsonNode(str);
        if (root != null){

            Iterator<JsonNode> ite = root.path("response").path("station").elements();
            // Get the elements of station information
            while (ite.hasNext()) {
                JsonNode j = ite.next();
                StationInfo info = new StationInfo();

                info.x = j.path("x").asDouble();
                info.y = j.path("y").asDouble();

                info.name = j.path("name").asText();
                info.next = j.path("next").asText();
                info.prev = j.path("prev").asText();
                info.line = j.path("line").asText();

                // Add to list
                mInfo.add(info);
            }
        }
    }
}
