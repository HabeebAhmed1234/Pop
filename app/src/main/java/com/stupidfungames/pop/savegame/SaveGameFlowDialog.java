package com.stupidfungames.pop.savegame;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;
import com.stupidfungames.pop.dialog.GameNeonDialogActivity;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;
import java.util.Arrays;
import java.util.List;

public class SaveGameFlowDialog extends GameNeonDialogActivity implements HostActivity,
    LoginListener {

  public static final int RESULT_DECLINED = 1;
  public static final int RESULT_DECLINED_PERMANENT = 2;
  public static final int RESULT_SUCCESS = 3;

  private static final String EXTRA_SAVE_GAME = "save_game";
  private static final String DONT_SHOW_SAVE_GAME_FLOW_ON_BACK_PREF = "dont_show_save_game";

  /**
   * returns a non null intent if the flow can be started, null otherwise
   */
  @Nullable
  public static Intent getIntent(SaveGame saveGame, Context context) {
    if (GamePreferencesManager.getBoolean(context, DONT_SHOW_SAVE_GAME_FLOW_ON_BACK_PREF)) {
      return null;
    }
    Intent intent = new Intent(context, SaveGameFlowDialog.class);
    intent.putExtra(EXTRA_SAVE_GAME, saveGame);
    return intent;
  }

  private final OnClickListener signInClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      GooglePlayServicesAuthManager.get(
          SaveGameFlowDialog.this,
          SaveGameFlowDialog.this)
          .initiateLogin(SaveGameFlowDialog.this);
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

  @Override
  protected int getTitleResId() {
    return R.string.save_game_flow_title;
  }

  @Override
  protected List<ButtonModel> getButtonModels() {
    return Arrays.asList(
        new ButtonModel(R.string.sign_in, signInClickListener),
        new ButtonModel(R.string.no, declineClickListener),
        new ButtonModel(R.string.no_permanent, declinePermanentClickListener));
  }

  @Override
  public void onLoggedIn(GoogleSignInAccount account) {
    SaveGame saveGame = (SaveGame) getIntent().getSerializableExtra(EXTRA_SAVE_GAME);
    if (saveGame == null) {
      showGenericError();
      return;
    }
    SaveGameManager.get(this, this).saveGame(this, saveGame);
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
}
