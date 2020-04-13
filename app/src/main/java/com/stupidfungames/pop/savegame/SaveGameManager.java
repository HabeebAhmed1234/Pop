package com.stupidfungames.pop.savegame;

import android.content.Context;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.stupidfungames.pop.GameActivityGame;
import com.stupidfungames.pop.HostActivity;
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
    private final GooglePlayServicesSaveGameManager playServicesSaveGameManager;

    public static SaveGameManager get(
        Context context,
        GooglePlayServicesAuthManager authManager,
        HostActivity hostActivity) {
        if (sSaveGameManager == null) {
            sSaveGameManager = new SaveGameManager(context, authManager, hostActivity);
        }
        return sSaveGameManager;
    }

    private SaveGameManager(
        Context context,
        GooglePlayServicesAuthManager authManager,
        HostActivity hostActivity){
        this.context = context;
        this.playServicesSaveGameManager = new GooglePlayServicesSaveGameManager(context, hostActivity);;
        authManager.addListener(this);

        if (!authManager.isLoggedIn()) {
            // We should clear the cache if this class is initialized while we are logged out.
            onLoggedOut();
        }
    }

    @Override
    public void onLoggedIn(GoogleSignInAccount account) {
        LocalSaveGameManager.clear(context);
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
        newSaveGame.saveGameVersionCode = SAVE_GAME_VERSION_NUMER;

        // Save to local shared preferences
        LocalSaveGameManager.saveGame(context, newSaveGame);

        // Save to google
        playServicesSaveGameManager.saveGame();
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
            return false;
        }

        context.startActivity(GameActivityGame.newIntent(saveGame, context));
        return true;
    }
}
