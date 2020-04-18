package com.stupidfungames.pop;

import static android.view.animation.Animation.INFINITE;
import static android.view.animation.Animation.RESTART;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.stupidfungames.pop.androidui.GameMenuButton;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;
import com.stupidfungames.pop.dialog.ToastDialogActivity;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.savegame.SaveGameManager;
import com.stupidfungames.pop.savegame.SaveGameManager.Listener;
import com.stupidfungames.pop.savegame.UpdateGameDialogActivity;

public class LoadGameBtnView implements Listener, LoginListener {

  private final Context context;
  private final SaveGameManager saveGameManager;
  private final View loadingSpinner;
  private final GameMenuButton loadGameBtn;
  private final HostActivity hostActivity;

  public LoadGameBtnView(
      final SaveGameManager saveGameManager,
      final  View loadingSpinner,
      final GameMenuButton loadGameBtn,
      final HostActivity hostActivity) {
    this.context = loadGameBtn.getContext();
    this.loadingSpinner = loadingSpinner;
    this.loadGameBtn = loadGameBtn;
    this.hostActivity = hostActivity;
    this.saveGameManager = saveGameManager;

    updateButtonColor(false);
    saveGameManager.addListener(this);

    GooglePlayServicesAuthManager authManager = hostActivity.getAuthManager();
    authManager.addListener(this);

    if (!authManager.isLoggedIn()) {
      updateButtonColor(false);
      setupButtonToPromptLoad();
    }
  }

  @Override
  public void onSaveSameLoaded(SaveGame saveGame) {
    setupButtonForLoadedGame(saveGame);
  }

  @Override
  public void onNoSaveGames() {
    setUpButtonForNoSaveGame();
  }

  @Override
  public void onLoginStart() {
    startLoadingAnimation();
  }

  @Override
  public void onLoggedIn(GoogleSignInAccount account) {
    /** Do nothing, wait for save game to load **/
  }

  @Override
  public void onLoggedOut() {
    // Change the button to prompt login
    setupButtonToPromptLoad();
  }

  @Override
  public void onLoginFailed(Exception e) {
    // Change the button to prompt login
    setupButtonToPromptLoad();
  }

  @Override
  public void onLoginCanceled() {
    // Change the button to prompt login
    setupButtonToPromptLoad();
  }

  /**
   * If the user is not yet logged in and there is no save game data loaded then we must login the
   * user if they tap on the resume button. The user can then tap again to load the game.
   */
  private void setupButtonToPromptLoad() {
    updateButtonColor(false);
    stopLoadingAnimation();
    loadGameBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        hostActivity.getAuthManager().initiateLogin(hostActivity);
      }
    });
  }

  /**
   * Sets an onclick listener to the resume button such that when it is pressed the new save game
   * launches
   */
  private void setupButtonForLoadedGame(final SaveGame saveGame) {
    updateButtonColor(true);
    stopLoadingAnimation();
    loadGameBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        startLoadedGame(saveGame);
      }
    });
  }

  /**
   * Sets an onclick listener to let the user know there are no save games if they click on the load
   * btn
   */
  private void setUpButtonForNoSaveGame() {
    updateButtonColor(false);
    stopLoadingAnimation();
    loadGameBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        showNoSaveGameToast();
      }
    });
  }

  private void startLoadedGame(SaveGame saveGame) {
    boolean started = SaveGameManager.startLoadedGame(saveGame, context);
    if (started) {
      hostActivity.finish();
    } else {
      showUpdateDialog();
    }
  }

  private void showNoSaveGameToast() {
    ToastDialogActivity.start(R.string.no_save_game_available, context);
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

  private void startLoadingAnimation() {
    loadingSpinner.setVisibility(View.VISIBLE);

    RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    rotate.setDuration(700);
    rotate.setInterpolator(new LinearInterpolator());
    rotate.setRepeatCount(INFINITE);
    rotate.setRepeatMode(RESTART);

    loadingSpinner.startAnimation(rotate);
  }

  private void stopLoadingAnimation() {
    loadingSpinner.setVisibility(View.GONE);
    loadingSpinner.clearAnimation();
  }
}