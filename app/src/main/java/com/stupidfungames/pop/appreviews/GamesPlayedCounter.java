package com.stupidfungames.pop.appreviews;

import android.content.Context;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;

public class GamesPlayedCounter {

  public static String GAME_PLAYED_COUNT_PREF = "game_played_count_pref";

  public static void onGamePlayed(Context context) {
    int currentNumTimesAppOpened = GamePreferencesManager.getInt(context, GAME_PLAYED_COUNT_PREF);
    if (currentNumTimesAppOpened == -1) {
      currentNumTimesAppOpened = 0;
    }
    GamePreferencesManager.set(context, GAME_PLAYED_COUNT_PREF, currentNumTimesAppOpened + 1);
  }

  public static int getNumTimesGamePlayed(Context context) {
    int currentNumTimesAppOpened = GamePreferencesManager.getInt(context, GAME_PLAYED_COUNT_PREF);
    return currentNumTimesAppOpened == -1 ? 0 : currentNumTimesAppOpened;
  }
}
