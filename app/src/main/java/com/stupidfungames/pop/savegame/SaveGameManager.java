package com.stupidfungames.pop.savegame;

import static com.stupidfungames.pop.analytics.Events.RESUME_GAME;
import static com.stupidfungames.pop.analytics.Events.RESUME_GAME_INCORRECT_VERSION_NUMBER;

import android.content.Context;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.stupidfungames.pop.GameActivity;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;
import com.stupidfungames.pop.googleplaysave.GooglePlayServicesSaveManager;
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
    private static final String SAVE_GAME_NAME = "save_game";
    private static final String TAG = "SaveGameManager";

    private final Context context;
    private final Set<Listener> listeners = new HashSet<>();
    private final GooglePlayServicesSaveManager playServicesSaveGameManager;
    private final GooglePlayServicesAuthManager authManager;

    public SaveGameManager(
        Context context,
        HostActivity hostActivity){
        this.context = context;
        this.authManager = hostActivity.getAuthManager();
        this.playServicesSaveGameManager = new GooglePlayServicesSaveManager<SaveGame>(hostActivity);

        authManager.addListener(this);
    }

    @Override
    public void onLoginStart() {}

    @Override
    public void onLoggedIn(GoogleSignInAccount account) {
        loadGame(account);
    }

    @Override
    public void onLoggedOut() {
        LocalSaveGameManager.clear(context);
    }

    @Override
    public void onLoginFailed(Exception e) {
        LocalSaveGameManager.clear(context);
    }

    @Override
    public void onLoginCanceled() {
        LocalSaveGameManager.clear(context);
    }

    public void addListener(final Listener listener) {
        final ListenableFuture<SaveGame> latestSaveGame =  LocalSaveGameManager.loadGame(context);
        Futures.addCallback(latestSaveGame, new FutureCallback<SaveGame>() {
            @Override
            public void onSuccess(@NullableDecl SaveGame saveGame) {
                if (saveGame != null) {
                    listener.onSaveSameLoaded(saveGame);
                }
            }
            @Override
            public void onFailure(Throwable t) {}
        }, ContextCompat.getMainExecutor(context));
        listeners.add(listener);
    }

    public void saveGame(Context context, SaveGame newSaveGame) {
        if (!authManager.isLoggedIn()) {
            Log.e(TAG, "Cannot save game when logged out");
            return;
        }
        newSaveGame.saveGameVersionCode = SAVE_GAME_VERSION_NUMER;

        // Save to local shared preferences
        LocalSaveGameManager.saveGame(context, newSaveGame);

        // Save to google
        playServicesSaveGameManager.save(SAVE_GAME_NAME, newSaveGame);
    }

    public void deleteSaveGame() {
        LocalSaveGameManager.delete(context);

        if (!authManager.isLoggedIn()) {
            Log.e(TAG, "Delete remote copy of save game");
            return;
        }

        // Save to google
        playServicesSaveGameManager.delete(SAVE_GAME_NAME);

    }

    public void loadGame(GoogleSignInAccount account) {
        // Try loading from Google
        Futures.addCallback(playServicesSaveGameManager.load(SAVE_GAME_NAME, account),
            new FutureCallback<SaveGame>() {
                @Override
                public void onSuccess(@NullableDecl SaveGame saveGame) {
                    if (saveGame != null) {
                        notifySaveGameLoaded(saveGame);
                    } else {
                        loadLocalGameFallback();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    loadLocalGameFallback();
                }
            }, ContextCompat.getMainExecutor(context));
    }

    private void loadLocalGameFallback() {
        Futures.addCallback(LocalSaveGameManager.loadGame(context), new FutureCallback<SaveGame>() {
            @Override
            public void onSuccess(@NullableDecl SaveGame saveGame) {
                if (saveGame != null) {
                    notifySaveGameLoaded(saveGame);
                } else {
                    notifyNoSaveGames();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "error loading fallback local game",t);
                notifyNoSaveGames();
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
            Logger.logSelect(context, RESUME_GAME_INCORRECT_VERSION_NUMBER, saveGame.saveGameVersionCode, SAVE_GAME_VERSION_NUMER);
            return false;
        }

        Logger.logSelect(context, RESUME_GAME);
        context.startActivity(GameActivity.newIntent(saveGame, context));
        return true;
    }
}
