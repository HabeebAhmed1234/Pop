package com.stupidfungames.pop;

import android.content.Context;
import android.util.Log;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.stupidfungames.pop.androidui.GameMenuButton;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.savegame.SaveGameManager;
import com.stupidfungames.pop.savegame.UpdateGameDialogActivity;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LoadGameBtnView implements LoginListener {

  private final Context context;
  private final GooglePlayServicesAuthManager authManager;
  private final GameMenuButton loadGameBtn;
  private final HostActivity hostActivity;

  public LoadGameBtnView(
      final GooglePlayServicesAuthManager authManager,
      final GameMenuButton loadGameBtn,
      final HostActivity hostActivity) {
    this.context = loadGameBtn.getContext();
    this.authManager = authManager;
    this.loadGameBtn = loadGameBtn;
    this.hostActivity = hostActivity;

    SaveGameManager.get();
    updateButtonColor(false);
    authManager.addListener(this);
  }

  @Override
  public void onLoggedIn(GoogleSignInAccount account) {
    getSaveGame();
  }

  @Override
  public void onLoggedOut() {
    updateButtonColor(false);
  }

  @Override
  public void onLoginFailed(Exception e) {
    updateButtonColor(false);
  }

  private void getSaveGame() {
    ListenableFuture<SaveGame> saveGameListenableFuture = SaveGameManager.loadGame(context);
    Futures.addCallback(saveGameListenableFuture, new FutureCallback<SaveGame>() {
      @Override
      public void onSuccess(@NullableDecl final SaveGame saveGame) {
        if (saveGame != null) {
          loadGameBtn.setVisibility(View.VISIBLE);
          loadGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startLoadGame(saveGame);
            }
          });
        }
      }

      @Override
      public void onFailure(Throwable t) {
        Log.e("MainMenuActivity", "Error loading save game", t);
      }
    }, ContextCompat.getMainExecutor(context));
  }

  private void startLoadGame(SaveGame saveGame) {
    boolean started = SaveGameManager.startLoadedGame(saveGame, context);
    if (started) {
      hostActivity.finish();
    } else {
      showUpdateDialog();
    }
  }

  private void showUpdateDialog() {
    hostActivity.startActivity(UpdateGameDialogActivity.newIntent(context));
  }

  private void updateButtonColor(boolean hasSaveGame) {
    int color = ContextCompat.getColor(context, R.color.menu_button_color_disabled);
    if (hasSaveGame) {
      color = ContextCompat.getColor(context, android.R.color.white);
    }
    loadGameBtn.setTextColor(color);
  }
}
