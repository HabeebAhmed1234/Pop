package com.stupidfungames.pop.savegame;

import android.content.Context;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.stupidfungames.pop.GameActivity;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;
import java.util.HashSet;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Serializes and deserializes the {@link SaveGame}
 */
public class SaveGameManager implements LoginListener {

    public interface Listener {

        /**
         * Callled when there has been a loaded save game ready to play
         */
        void onSaveSameLoaded(SaveGame saveGame);

        /**
         * Called when we have loaded the save games and have determined that there are no save
         * games for the user.
         */
        void onNoSaveGames();
    }

    /**
     * Increment this when a change to {@link SaveGame} has been made. We cannot let older clients
     * attempt to load save game data saved by newer clients in case the game breaks as a result.
     */
    private static final int SAVE_GAME_VERSION_NUMER = 1;
    private static final String TAG = "SaveGameManager";

    private static SaveGameManager sSaveGameManager;

    private final Context context;
    private final Set<Listener> listeners = new HashSet<>();
    private final LocalSaveGameManager localSaveGameManager;
    private final GooglePlayServicesSaveGameManager playServicesSaveGameManager;

    public static SaveGameManager get(
        Context context,
        GooglePlayServicesSaveGameManager playServicesSaveGameManager,
        GooglePlayServicesAuthManager authManager) {
        if (sSaveGameManager == null) {
            sSaveGameManager = new SaveGameManager(context, playServicesSaveGameManager, authManager);
        }
        return sSaveGameManager;
    }

    private SaveGameManager(
        Context context,
        GooglePlayServicesSaveGameManager playServicesSaveGameManager,
        GooglePlayServicesAuthManager authManager){
        this.context = context;
        this.playServicesSaveGameManager = playServicesSaveGameManager;
        this.localSaveGameManager = new LocalSaveGameManager();
        authManager.addListener(this);
    }

    @Override
    public void onLoggedIn(GoogleSignInAccount account) {
        latestSaveGame = null;
        loadGame(account);
    }

    @Override
    public void onLoggedOut() {
        latestSaveGame = null;
    }

    @Override
    public void onLoginFailed(Exception e) {
        latestSaveGame = null;
    }

    public void addListener(Listener listener) {
        if (latestSaveGame != null) {
            listener.onSaveSameLoaded(latestSaveGame);
        }
        listeners.add(listener);
    }

    public void saveGame(Context context, SaveGame newSaveGame) {
        newSaveGame.saveGameVersionCode = SAVE_GAME_VERSION_NUMER;
        latestSaveGame = newSaveGame;

        // Save to local shared preferences
        localSaveGameManager.saveGame(context, latestSaveGame);

        // Save to google
    }

    public void loadGame(GoogleSignInAccount account) {
        // Try loading from Google
        Futures.addCallback(playServicesSaveGameManager.load(account),
            new FutureCallback<SaveGame>() {
                @Override
                public void onSuccess(@NullableDecl SaveGame saveGame) {
                    if (saveGame != null) {
                        notifySaveGameLoaded(saveGame);
                    } else {
                        loadLoaclGameFallback();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    loadLoaclGameFallback();
                }
            }, ContextCompat.getMainExecutor(context));
    }

    private void loadLoaclGameFallback() {
        Futures.addCallback(localSaveGameManager.loadGame(context), new FutureCallback<SaveGame>() {
            @Override
            public void onSuccess(@NullableDecl SaveGame saveGame) {
                if (saveGame != null) {
                    notifySaveGameLoaded(saveGame);
                } else if (latestSaveGame != null) {
                    notifySaveGameLoaded(latestSaveGame);
                } else {
                    notifyNoSaveGames();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "error loading fallback local game",t);
                if (latestSaveGame != null) {
                    notifySaveGameLoaded(latestSaveGame);
                } else {
                    notifyNoSaveGames();
                }
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void notifySaveGameLoaded(SaveGame saveGame) {
        for (Listener listener :  listeners) {
            listener.onSaveSameLoaded(saveGame);
        }
    }

    private void notifyNoSaveGames() {
        for (Listener listener :  listeners) {
            listener.onNoSaveGames();
        }
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
