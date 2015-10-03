package jp.co.tweetmap;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;

public class MainActivityPage {
  public Intent createMainLaunchIntent() {
    Intent intent = new Intent(InstrumentationRegistry.getContext(), MainActivity.class);
    intent.setAction(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);
    return intent;
  }
}
