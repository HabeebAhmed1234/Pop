package com.stupidfungames.pop.savegame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;
import com.stupidfungames.pop.dialog.GameNeonDialogActivity;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaveGameFlowDialog extends GameNeonDialogActivity implements HostActivity,
    LoginListener {

  public static final int RESULT_DECLINED = 1;
  public static final int RESULT_DECLINED_PERMANENT = 2;
  public static final int RESULT_SUCCESS = 3;
  public static final int RESULT_DISMISSED = 4;

  private static final String EXTRA_ALLOW_PERMANENT_DISMISS = "allow_permanent_dismiss";
  private static final String EXTRA_SAVE_GAME = "save_game";
  private static final String DONT_SHOW_SAVE_GAME_FLOW_ON_BACK_PREF = "dont_show_save_game";

  /**
   * returns a non null intent if the flow can be started, null otherwise
   */
  @Nullable
  public static Intent getIntent(SaveGame saveGame, boolean allowForPermanentDismiss,
      boolean forceShow, Context context) {
    if (!forceShow && GamePreferencesManager
        .getBoolean(context, DONT_SHOW_SAVE_GAME_FLOW_ON_BACK_PREF)) {
      return null;
    }
    Intent intent = new Intent(context, SaveGameFlowDialog.class);
    intent.putExtra(EXTRA_ALLOW_PERMANENT_DISMISS, allowForPermanentDismiss);
    intent.putExtra(EXTRA_SAVE_GAME, saveGame);
    return intent;
  }

  private GooglePlayServicesAuthManager authManager;
  private SaveGameManager saveGameManager;

  private final OnClickListener signInClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      authManager.initiateLogin(SaveGameFlowDialog.this);
    }
  };

  private final OnClickListener declineClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      onDeclined();
    }
  };

  private final OnClickListener declinePermanentClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      onDeclinedPermanent();
    }
  };

  private final OnClickListener dismissedClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      setResult(RESULT_DISMISSED);
      finish();
    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    authManager = new GooglePlayServicesAuthManager(this);
    saveGameManager = new SaveGameManager(this, this);

    authManager.addListener(this);
  }

  @Override
  protected int getTitleResId() {
    return R.string.save_game_flow_title;
  }

  @Override
  protected List<ButtonModel> getButtonModels() {
    List<ButtonModel> buttonModels = new ArrayList<>(
        Arrays.asList(
            new ButtonModel(R.string.sign_in, signInClickListener),
            new ButtonModel(R.string.no, declineClickListener)));
    if (getIntent().getBooleanExtra(EXTRA_ALLOW_PERMANENT_DISMISS, true)) {
      buttonModels.add(new ButtonModel(R.string.no_permanent, declinePermanentClickListener));
    }
    buttonModels.add(new ButtonModel(R.string.dismiss, dismissedClickListener));
    return buttonModels;
  }

  @Override
  public void onLoginStart() {
  }

  @Override
  public void onLoggedIn(GoogleSignInAccount account) {
    SaveGame saveGame = (SaveGame) getIntent().getSerializableExtra(EXTRA_SAVE_GAME);
    if (saveGame == null) {
      showGenericError();
      return;
    }
    saveGameManager.saveGame(this, saveGame);
    onSuccess();
  }

  @Override
  public void onLoggedOut() {
    showGenericError();
  }

  @Override
  public void onLoginFailed(Exception e) {
    showGenericError();
  }

  @Override
  public void onLoginCanceled() {
    Toast.makeText(this, R.string.login_to_save, Toast.LENGTH_LONG);
  }

  private void showGenericError() {
    Toast.makeText(this, R.string.generic_error, Toast.LENGTH_LONG);
  }

  private void onDeclined() {
    setResult(RESULT_DECLINED);
    finish();
  }

  private void onDeclinedPermanent() {
    GamePreferencesManager.set(this, DONT_SHOW_SAVE_GAME_FLOW_ON_BACK_PREF, true);
    setResult(RESULT_DECLINED_PERMANENT);
    finish();
  }

  private void onSuccess() {
    setResult(RESULT_SUCCESS);
    finish();
  }

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public GooglePlayServicesAuthManager getAuthManager() {
    return authManager;
  }
}
