package com.stupidfungames.pop.savegame;

import android.content.Context;
import android.text.TextUtils;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;

class LocalSaveGameManager {

  private static final String LOCAL_SAVE_GAME_KEY = "local_save_game";

  public static void clear(Context context) {
    GamePreferencesManager.set(context, LOCAL_SAVE_GAME_KEY, null);
  }

  public static ListenableFuture<SaveGame> loadGame(Context context) {
    String saveGameJson = GamePreferencesManager.getString(context, LOCAL_SAVE_GAME_KEY);
    if (TextUtils.isEmpty(saveGameJson)) {
      return Futures.immediateFuture(null);
    }
    SaveGame loadedSaveGame = new Gson().fromJson(saveGameJson, SaveGame.class);
    return Futures.immediateFuture(loadedSaveGame);
  }

  public static void saveGame(Context context, SaveGame newSaveGame) {
    String json = new Gson().toJson(newSaveGame);
    GamePreferencesManager.set(context, LOCAL_SAVE_GAME_KEY, json);
  }
}
