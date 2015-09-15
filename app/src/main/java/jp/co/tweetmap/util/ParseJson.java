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

import java.io.IOException;
import android.util.Log;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Convert a JSON string to JsonNode object..
 */
public class ParseJson {

    /*************************************************************************************
     *                              Class variables                                      *
     *************************************************************************************/
    protected String content;


    /*************************************************************************************
     *                                  Methods                                          *
     *************************************************************************************/
    /** Convert a JSON string to JsonNode object.
     *
     * @param str JSON string.
     * @return JsonNode object.
     */
    protected JsonNode getJsonNode(String str) {
        try {
            return new ObjectMapper().readTree(str);
        } catch (IOException e) {
            Log.d(getClass().getName(), e.getMessage());
        }
        return null;
    }

    /**
     * Read a JSON string.
     *
     * @param str JSON string.
     */
    public void loadJson(String str) {
        // N.O.P
    }

    /**
     * Get content.
     *
     * @return content string.
     */
    public String getContent() {
        return this.content;
    }
}
