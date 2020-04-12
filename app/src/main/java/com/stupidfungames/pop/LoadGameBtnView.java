package com.stupidfungames.pop;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.stupidfungames.pop.savegame.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.savegame.SaveGameManager;
import com.stupidfungames.pop.savegame.UpdateGameDialogActivity;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LoadGameBtnView {

  private final Context context;
  private final GooglePlayServicesAuthManager authManager;
  private final View loadGameBtn;

  public LoadGameBtnView(GooglePlayServicesAuthManager authManager, final View loadGameBtn) {
    this.context = loadGameBtn.getContext();
    this.authManager = authManager;
    this.loadGameBtn = loadGameBtn;
  }

  private void init() {
    ListenableFuture<SaveGame> saveGameListenableFuture = SaveGameManager.loadGame(context);
    Futures.addCallback(saveGameListenableFuture, new FutureCallback<SaveGame>() {
      @Override
      public void onSuccess(@NullableDecl final SaveGame saveGame) {
        if (saveGame != null) {
          View btn = findViewById(R.id.load_game_btn);
          btn.setVisibility(View.VISIBLE);
          btn.setOnClickListener(new View.OnClickListener() {
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
    }, ContextCompat.getMainExecutor(this));
  }

  private void startLoadGame(SaveGame saveGame) {
    boolean started = SaveGameManager.startLoadedGame(saveGame, this);
    if (started) {
      this.finish();
    } else {
      showUpdateDialog();
    }
  }

  private void showUpdateDialog() {
    startActivity(UpdateGameDialogActivity.newIntent(this));
  }
}
