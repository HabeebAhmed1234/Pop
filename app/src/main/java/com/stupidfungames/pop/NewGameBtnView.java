package com.stupidfungames.pop;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.stupidfungames.pop.analytics.Events;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.dialog.ConfirmationToastDialogActivity;

/**
 * Manages the launching of a new game. - If the user is logged in we must check if they already
 * have a save game - If they already have a save game then show a confirmation dialog ("are you
 * sure you want to override your save game?" - If they don't have a save game or are not logged in
 * then just launch a new game
 */
public class NewGameBtnView {

  private final Context context;
  private final View newGameBtn;
  private final HostActivity hostActivity;

  public NewGameBtnView(View newGameBtn, HostActivity hostActivity) {
    this.context = newGameBtn.getContext();
    this.newGameBtn = newGameBtn;
    this.hostActivity = hostActivity;
    initClickListeners();
  }

  private void initClickListeners() {
    newGameBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        onNewGameClicked();
      }
    });
  }

  private void onNewGameClicked() {
    GooglePlayServicesAuthManager authManager = hostActivity.getAuthManager();
    if (authManager.isLoggedIn()) {
      // Launch the confirmation toast
      launchConfirmationToast();
    } else {
      startNewGame();
    }
  }

  private void launchConfirmationToast() {
    ConfirmationToastDialogActivity.start(
        R.string.new_game_confirm_title,
        new ConfirmationToastDialogActivity.ConfirmationCallback() {

          @Override
          public void onYes() {
            startNewGame();
          }

          @Override
          public void onNo() {
          }
        },
        hostActivity,
        context);
  }

  private void startNewGame() {
    Logger.logSelect(hostActivity.getContext(), Events.NEW_GAME_STARTED_MAIN_MENU);
    hostActivity.startActivity(GameActivity.newIntent(context));
    hostActivity.finish();
  }
}
