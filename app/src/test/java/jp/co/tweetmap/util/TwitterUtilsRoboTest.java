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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import jp.co.tweetmap.CustomRobolectricTestRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Config(sdk = 18) @RunWith(CustomRobolectricTestRunner.class)
public class TwitterUtilsRoboTest {
  @Before
  public void setUp() {
    ShadowLog.stream = System.out;
  }

  @Test @SmallTest @SuppressLint("CommitPrefEdits")
  public void Twitterアクセストークンの保持確認() {
    Context context = RuntimeEnvironment.application;
    SharedPreferences.Editor pref =
        context.getSharedPreferences(TwitterUtils.PREF_NAME, Context.MODE_PRIVATE).edit();

    {
      // ダミーのアクセストークンをPreferenceに記録し、疑似的にトークン保持状態で確認する。
      pref.putString(TwitterUtils.ACCESS_TOKEN, "TEST_TOKEN");
      pref.putString(TwitterUtils.ACCESS_TOKEN_SECRET, "TEST_SECRET_TOKEN");
      pref.commit();
      assertTrue("保存したアクセストークンを取得できていない", TwitterUtils.hasAccessToken(context));
    }

    {
      // アクセストークン(null)をPreferenceに記録し、トークン保持状態を確認する。
      pref.putString(TwitterUtils.ACCESS_TOKEN, null);
      pref.putString(TwitterUtils.ACCESS_TOKEN_SECRET, null);
      pref.commit();
      assertFalse("アクセストークンがnullにも関わらず保持状態と判定", TwitterUtils.hasAccessToken(context));
    }

    {
      // アクセストークン(空文字)をPreferenceに記録し、トークン保持状態を確認する。
      pref.putString(TwitterUtils.ACCESS_TOKEN, "");
      pref.putString(TwitterUtils.ACCESS_TOKEN_SECRET, "");
      pref.commit();
      assertFalse("アクセストークンが空文字にも関わらず保持状態と判定", TwitterUtils.hasAccessToken(context));
    }

    // Esspresso : http://qiita.com/shikato/items/e4fa620f0f616d6b790d
    // Robolectric : http://qiita.com/yuya_presto/items/d5cc27225a19e1971096
  }
}
