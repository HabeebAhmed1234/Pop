package com.wack.pop2.savegame;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.wack.pop2.GameActivity;
import com.wack.pop2.gamesettings.GamePreferencesManager;

/**
 * Serializes and deserializes the {@link SaveGame}
 */
public class SaveGameManager {

    /**
     * Increment this when a change to {@link SaveGame} has been made. We cannot let older clients
     * attempt to load save game data saved by newer clients in case the game breaks as a result.
     */
    private static final int SAVE_GAME_VERSION_NUMER = 1;

    private static final String LOCAL_SAVE_GAME_DEBUG_KEY = "local_save_game";

    public static ListenableFuture<SaveGame> loadGame(Context context) {
        String saveGameJson = GamePreferencesManager.getString(context, LOCAL_SAVE_GAME_DEBUG_KEY);
        if (TextUtils.isEmpty(saveGameJson)) {
            return Futures.immediateFuture(null);
        }
        SaveGame loadedSaveGame = new Gson().fromJson(saveGameJson, SaveGame.class);
        return Futures.immediateFuture(loadedSaveGame);
    }

    public static void saveGame(Context context, SaveGame newSaveGame) {
        newSaveGame.saveGameVersionCode = SAVE_GAME_VERSION_NUMER;
        String json = new Gson().toJson(newSaveGame);
        GamePreferencesManager.set(context, LOCAL_SAVE_GAME_DEBUG_KEY, json);
    }

    /**
     * Returns true if the game was started
     */
    public static boolean startLoadedGame(SaveGame saveGame, Context context) {
        if (saveGame.saveGameVersionCode > SAVE_GAME_VERSION_NUMER) {
            return false;
        }

        context.startActivity(GameActivity.newIntent(saveGame, context));
        return true;
    }
}
