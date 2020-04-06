package com.wack.pop2.savegame;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.wack.pop2.gamesettings.GamePreferencesManager;

/**
 * Serializes and deserializes the {@link SaveGame}
 */
public class SaveGameManager {

    private static final String LOCAL_SAVE_GAME_DEBUG_KEY = "local_save_game";

    public static ListenableFuture<SaveGame> loadGame(Context context) {
        String saveGameJson = GamePreferencesManager.getString(context, LOCAL_SAVE_GAME_DEBUG_KEY);
        if (TextUtils.isEmpty(saveGameJson)) {
            return null;
        }
        return Futures.immediateFuture(new Gson().fromJson(saveGameJson, SaveGame.class));
    }

    public static void saveGame(Context context, SaveGame newSaveGame) {
        String json = new Gson().toJson(newSaveGame);
        GamePreferencesManager.set(context, LOCAL_SAVE_GAME_DEBUG_KEY, json);
    }
}
