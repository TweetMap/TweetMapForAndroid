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

import android.content.Context;
import android.location.LocationManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class) @LargeTest
public class ApplicationTest {
  @Rule
  public ActivityTestRule<MainActivity> activityRule
      = new ActivityTestRule<>(MainActivity.class, true, false);

  private Context context = InstrumentationRegistry.getContext();
  private MockLocationProvider locationProvider = new MockLocationProvider(LocationManager.GPS_PROVIDER, context);

  @Before
  public void onTestStart() {
    locationProvider.prepare();
    locationProvider.enable();
  }
  
  @After
  public void onTestFinished() {
    locationProvider.shutdown();
  }
  
  @Test
  public void GPS有効状態でActivity起動() {
    locationProvider.setLocation(10, 10);
    
    MainActivityPage activity = new MainActivityPage();
    activityRule.launchActivity(activity.createMainLaunchIntent());

    // アプリのトップページタイトルが表示されている
    onView(withText(R.string.top_title_bar_name)).check(matches(isDisplayed()));
  }

  @Test
  public void GPS無効状態でActivity起動() {
    locationProvider.disable();  // GPS無効化

    MainActivityPage activity = new MainActivityPage();
    activityRule.launchActivity(activity.createMainLaunchIntent());

    // 位置情報サービスが無効である旨のダイアログが表示されている
    onView(withText(R.string.gps_alert_dlg_title)).check(matches(isDisplayed()));
  }
}