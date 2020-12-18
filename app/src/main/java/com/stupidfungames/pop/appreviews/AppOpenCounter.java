package com.stupidfungames.pop.appreviews;

import android.content.Context;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;

public class AppOpenCounter {

  public static String APP_OPEN_COUNT_PREF = "app_open_count";

  public static void onAppOpened(Context context) {
    int currentNumTimesAppOpened = GamePreferencesManager.getInt(context, APP_OPEN_COUNT_PREF);
    if (currentNumTimesAppOpened == -1) {
      currentNumTimesAppOpened = 0;
    }
    GamePreferencesManager.set(context, APP_OPEN_COUNT_PREF, currentNumTimesAppOpened + 1);
  }

  public static int getNumTimesAppOpened(Context context) {
    int currentNumTimesAppOpened = GamePreferencesManager.getInt(context, APP_OPEN_COUNT_PREF);
    return currentNumTimesAppOpened == -1 ? 0 : currentNumTimesAppOpened;
  }
}
