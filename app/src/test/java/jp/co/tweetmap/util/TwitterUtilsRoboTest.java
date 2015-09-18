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

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import jp.co.tweetmap.CustomRobolectricTestRunner;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Config(sdk = 18) @RunWith(CustomRobolectricTestRunner.class)
public class TwitterUtilsRoboTest {

  private final Context context = RuntimeEnvironment.application;

  @Before
  public void setUp() {
    ShadowLog.stream = System.out;
  }

  @Test @SmallTest @SuppressLint("CommitPrefEdits")
  public void Twitterアクセストークンの保持確認() {
    SharedPreferences.Editor prefEditor =
        context.getSharedPreferences(TwitterUtils.PREF_NAME, Context.MODE_PRIVATE).edit();

    {
      // ダミーのアクセストークンをPreferenceに記録し、疑似的にトークン保持状態で確認する。
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN, "TEST_TOKEN");
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN_SECRET, "TEST_TOKEN_SECRET");
      prefEditor.commit();
      assertTrue("保存したアクセストークンを取得できていない", TwitterUtils.hasAccessToken(context));
    }
    {
      // アクセストークン(null)をPreferenceに記録し、トークン保持状態を確認する。
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN, null);
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN_SECRET, null);
      prefEditor.commit();
      assertFalse("アクセストークンがnullにも関わらず保持状態と判定", TwitterUtils.hasAccessToken(context));
    }
    {
      // アクセストークン(空文字)をPreferenceに記録し、トークン保持状態を確認する。
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN, "");
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN_SECRET, "");
      prefEditor.commit();
      assertFalse("アクセストークンが空文字にも関わらず保持状態と判定", TwitterUtils.hasAccessToken(context));
    }
  }

  @Test @SmallTest @SuppressLint("CommitPrefEdits")
  public void Twitterアクセストークンの読み込み確認() {
    SharedPreferences.Editor prefEditor =
        context.getSharedPreferences(TwitterUtils.PREF_NAME, Context.MODE_PRIVATE).edit();

    {
      // ダミーのアクセストークンをPreferenceに記録し、疑似的にトークン保持状態で確認する。
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN, "TEST_TOKEN");
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN_SECRET, "TEST_TOKEN_SECRET");
      prefEditor.commit();
      AccessToken token = TwitterUtils.loadAccessToken(context);
      assertNotNull("保存されたアクセストークンを読み込めていない", token);
      assertEquals("保存されたアクセストークンが正しく読み込めていない", "TEST_TOKEN", token.getToken());
      assertEquals("保存されたシークレットアクセストークンが正しく読み込めていない", "TEST_TOKEN_SECRET", token.getTokenSecret());
    }
    {
      // アクセストークン(null)をPreferenceに記録し、トークン未保持状態で確認する。
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN, null);
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN_SECRET, null);
      prefEditor.commit();
      AccessToken token = TwitterUtils.loadAccessToken(context);
      assertNull("null入力で非nullのアクセストークンオブジェクトが取得された", token);
    }
    {
      // アクセストークン(空文字)をPreferenceに記録した状態で確認する。
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN, "");
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN_SECRET, "");
      prefEditor.commit();
      AccessToken token = TwitterUtils.loadAccessToken(context);
      assertNotNull("保存されたアクセストークンを読み込めていない", token);
    }
  }

  @Test @SmallTest @SuppressLint("CommitPrefEdits")
  public void Twitterアクセストークンの書き込み確認() {
    SharedPreferences pref = context.getSharedPreferences(TwitterUtils.PREF_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor prefEditor = pref.edit();

    {
      // ダミーのアクセストークンをPreferenceに記録した状態で上書き確認する。
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN, "TEST_TOKEN");
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN_SECRET, "TEST_TOKEN_SECRET");
      prefEditor.commit();
      TwitterUtils.storeAccessToken(context, new AccessToken("OVERWRITE_TOKEN", "OVERWRITE_TOKEN_SECRET"));
      assertEquals("アクセストークンが正しく保存されていない",
          "OVERWRITE_TOKEN", pref.getString(TwitterUtils.ACCESS_TOKEN, null));
      assertEquals("シークレットアクセストークンが正しく保存されていない",
          "OVERWRITE_TOKEN_SECRET", pref.getString(TwitterUtils.ACCESS_TOKEN_SECRET, null));
    }
    {
      // ダミーのアクセストークンをPreferenceに記録した状態で上書き確認する。
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN, "TEST_TOKEN");
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN_SECRET, "TEST_TOKEN_SECRET");
      TwitterUtils.storeAccessToken(context, new AccessToken("", ""));
      assertEquals("空文字のアクセストークンが書き込めない",
          "", pref.getString(TwitterUtils.ACCESS_TOKEN, null));
      assertEquals("空文字のシークレットアクセストークンが書き込めない",
          "", pref.getString(TwitterUtils.ACCESS_TOKEN_SECRET, null));
    }
    {
      // ダミーのアクセストークンをPreferenceに記録した状態で上書き確認する。
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN, "TEST_TOKEN");
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN_SECRET, "TEST_TOKEN_SECRET");
      prefEditor.commit();
      // ブランク検査
//      TwitterUtils.storeAccessToken(null, null);
//      assertEquals("不正な入力でアクセストークンが破棄された",
//          "TEST_TOKEN", pref.getString(TwitterUtils.ACCESS_TOKEN, null));
//      assertEquals("不正な入力でシークレットアクセストークンが破棄された",
//          "TEST_TOKEN_SECRET", pref.getString(TwitterUtils.ACCESS_TOKEN_SECRET, null));
//
//      TwitterUtils.storeAccessToken(context, null);
//      assertEquals("不正な入力でアクセストークンが破棄された",
//          "TEST_TOKEN", pref.getString(TwitterUtils.ACCESS_TOKEN, null));
//      assertEquals("不正な入力でシークレットアクセストークンが破棄された",
//          "TEST_TOKEN_SECRET", pref.getString(TwitterUtils.ACCESS_TOKEN_SECRET, null));
//
//      TwitterUtils.storeAccessToken(null, new AccessToken("TEST_TOKEN", "TEST_TOKEN"));
//      assertEquals("不正な入力でアクセストークンが破棄された",
//          "TEST_TOKEN", pref.getString(TwitterUtils.ACCESS_TOKEN, null));
//      assertEquals("不正な入力でシークレットアクセストークンが破棄された",
//          "TEST_TOKEN_SECRET", pref.getString(TwitterUtils.ACCESS_TOKEN_SECRET, null));
    }
  }

  @Test @SmallTest @SuppressLint("CommitPrefEdits")
  public void Twitterインスタンスの取得確認() {
    SharedPreferences.Editor prefEditor =
        context.getSharedPreferences(TwitterUtils.PREF_NAME, Context.MODE_PRIVATE).edit();

    {
      // ダミーのアクセストークンをPreferenceに記録し、疑似的にトークン保持状態で確認する。
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN, "TEST_TOKEN");
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN_SECRET, "TEST_TOKEN_SECRET");
      prefEditor.commit();

      Twitter twitter = TwitterUtils.getTwitterInstance(context);
      assertNotNull("トークンがあってもTwitterインスタンスを取得できない", twitter);

      try {
        AccessToken token = twitter.getOAuthAccessToken();
        assertEquals("Twitterインスタンスにアクセストークンが正しく設定されていない", "TEST_TOKEN", token.getToken());
        assertEquals("Twitterインスタンスにシークレットアクセストークンが正しく設定されていない", "TEST_TOKEN_SECRET", token.getTokenSecret());
      } catch (TwitterException e) {
        throw new AssertionFailedError("Twitterインスタンスからのアクセストークンの取得に失敗");
      }
    }
    {
      // アクセストークン(null)をPreferenceに記録し、擬似的にトークン無し状態で確認する。
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN, null);
      prefEditor.putString(TwitterUtils.ACCESS_TOKEN_SECRET, null);
      prefEditor.commit();

      Twitter twitter = TwitterUtils.getTwitterInstance(context);
      assertNotNull("トークンがnullの場合にTwitterインスタンスが取得できない", twitter);
    }
  }
}
