package com.stupidfungames.pop.continuegame;

import static com.stupidfungames.pop.analytics.Events.CONTINUE_BTN_PRESSED;
import static com.stupidfungames.pop.analytics.Events.CONTINUE_GAME_WITH_TOKEN;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.android.billingclient.api.Purchase;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.androidui.GameMenuButton;
import com.stupidfungames.pop.androidui.LoadingSpinner;
import com.stupidfungames.pop.inapppurchase.GooglePlayServicesBillingManager;
import com.stupidfungames.pop.inapppurchase.ProductSKUManager.ProductSKU;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ContinueGameBtnView {

  public interface ContinueGameBtnViewHostActivity extends HostActivity {

    void continueGame();
  }

  private final String TAG = "ContinueGameBtnView";
  private final ContinueGameBtnViewHostActivity hostActivity;
  private final OnClickListener continueGameAndConsumeTokenOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      if (continueGamePurchase != null) {
        Logger.logSelect(hostActivity.getContext(), CONTINUE_GAME_WITH_TOKEN);
        billingManager.consume(continueGamePurchase);
      }
      hostActivity.continueGame();
      Logger.logSelect(hostActivity.getContext(), CONTINUE_BTN_PRESSED);
    }
  };
  private final OnClickListener watchAdOrBuyTokenOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      // Present the user with the option to watch an ad or buy a continue game token
      launchContinueGameChoice();
    }
  };

  private ContinueGameBtnState currentState = ContinueGameBtnState.LOADING;

  private LoadingSpinner loadingSpinner;
  private GameMenuButton continueGameButton;
  @Nullable
  private Purchase continueGamePurchase;

  private GooglePlayServicesBillingManager billingManager;

  private enum ContinueGameBtnState {
    LOADING, // Checking if the user has a continue game token
    ALLOW_CONTINUE_GAME, // User is allowed to continue game (either has token on watched ad)
    WATCH_AD_OR_BUY // User must watch an ad or buy a token to continue
  }

  public ContinueGameBtnView(
      LoadingSpinner loadingSpinner,
      GameMenuButton continueGameButton,
      ContinueGameBtnViewHostActivity hostActivity) {
    this.loadingSpinner = loadingSpinner;
    this.continueGameButton = continueGameButton;
    this.hostActivity = hostActivity;
    billingManager = new GooglePlayServicesBillingManager(hostActivity);

    updateButton();
    checkIfUserHasContinueToken();
  }

  private void checkIfUserHasContinueToken() {
    setGameButtonState(ContinueGameBtnState.LOADING);
    Futures.addCallback(billingManager.hasPurchase(ProductSKU.SKU_GAME_CONTINUE.skuString),
        new FutureCallback<Purchase>() {
          @Override
          public void onSuccess(@NullableDecl Purchase result) {
            if (result != null) {
              continueGamePurchase = result;
              setGameButtonState(ContinueGameBtnState.ALLOW_CONTINUE_GAME);
            } else {
              setGameButtonState(ContinueGameBtnState.WATCH_AD_OR_BUY);
            }
          }

          @Override
          public void onFailure(Throwable t) {
            setGameButtonState(ContinueGameBtnState.WATCH_AD_OR_BUY);
          }
        }, ContextCompat.getMainExecutor(getContext()));
  }

  private void setGameButtonState(ContinueGameBtnState newState) {
    if (currentState == newState) {
      return;
    }
    currentState = newState;
    updateButton();
  }

  private void updateButton() {
    @ColorInt int color = ContextCompat.getColor(getContext(), R.color.menu_button_color_disabled);
    switch (currentState) {
      case LOADING:
        loadingSpinner.startLoadingAnimation();
        color = ContextCompat.getColor(getContext(), R.color.menu_button_color_disabled);
        continueGameButton.setOnClickListener(null);
        break;
      case ALLOW_CONTINUE_GAME:
        promptUserToContinueGame();
        loadingSpinner.stopLoadingAnimation();
        color = ContextCompat.getColor(getContext(), R.color.green);
        continueGameButton.setOnClickListener(continueGameAndConsumeTokenOnClickListener);
        break;
      case WATCH_AD_OR_BUY:
        loadingSpinner.stopLoadingAnimation();
        color = ContextCompat.getColor(getContext(), R.color.menu_button_color);
        continueGameButton.setOnClickListener(watchAdOrBuyTokenOnClickListener);
        break;
    }
    continueGameButton.setTextColor(color);
  }

  private void launchContinueGameChoice() {
    hostActivity
        .prepareCall(new StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
          @Override
          public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == ContinueGameChoiceDialogActivity.RESULT_TOKEN_ACQUIRED) {
              checkIfUserHasContinueToken();
            } else if (result.getResultCode()
                == ContinueGameChoiceDialogActivity.RESULT_USER_CAN_CONTINUE) {
              promptUserToContinueGame();
              setGameButtonState(ContinueGameBtnState.ALLOW_CONTINUE_GAME);
            } else {
              Toast.makeText(getContext(), R.string.continue_game_token_not_aquired,
                  Toast.LENGTH_SHORT).show();
            }
          }
        }).launch(ContinueGameChoiceDialogActivity.createIntent(getContext()));
  }

  private void promptUserToContinueGame() {
    Toast.makeText(getContext(), R.string.prompt_user_to_continue, Toast.LENGTH_SHORT).show();
  }

  private Context getContext() {
    return continueGameButton.getContext();
  }
}
