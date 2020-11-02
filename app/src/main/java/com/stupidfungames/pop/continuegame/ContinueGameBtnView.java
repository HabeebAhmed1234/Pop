package com.stupidfungames.pop.continuegame;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.android.billingclient.api.Purchase;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.androidui.GameMenuButton;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
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
      billingManager.consume(continueGamePurchase);
      hostActivity.continueGame();
    }
  };
  private final OnClickListener watchAdOrBuyTokenOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      // Present the user with the option to watch an ad or buy a continue game token

    }
  };

  private ContinueGameBtnState currentState = ContinueGameBtnState.UNKNOWN;

  private GameMenuButton continueGameButton;
  @Nullable private Purchase continueGamePurchase;

  private GooglePlayServicesAuthManager authManager;
  private GooglePlayServicesBillingManager billingManager;

  private enum ContinueGameBtnState {
    UNKNOWN,
    TOKEN_PRESENT,
    NO_TOKEN_WATCH_AD_OR_BUY
  }

  public ContinueGameBtnView(
      GameMenuButton continueGameButton,
      ContinueGameBtnViewHostActivity hostActivity) {
    this.continueGameButton = continueGameButton;
    this.hostActivity = hostActivity;
    authManager = new GooglePlayServicesAuthManager(hostActivity.getContext());
    billingManager = new GooglePlayServicesBillingManager(hostActivity);

    updateButton();
    checkIfUserHasContinueToken();
  }

  private void checkIfUserHasContinueToken() {
    Futures.addCallback(billingManager.hasPurchase(ProductSKU.SKU_GAME_CONTINUE.skuString),
        new FutureCallback<Purchase>() {
          @Override
          public void onSuccess(@NullableDecl Purchase result) {
            if (result != null) {
              continueGamePurchase = result;
              setGameButtonState(ContinueGameBtnState.TOKEN_PRESENT);
            } else {
              setGameButtonState(ContinueGameBtnState.NO_TOKEN_WATCH_AD_OR_BUY);
            }
          }

          @Override
          public void onFailure(Throwable t) {
            Log.e(TAG, "Error checking continue token", t);
            Toast.makeText(getContext(), R.string.error_checking_continue_token, Toast.LENGTH_LONG).show();
            setGameButtonState(ContinueGameBtnState.NO_TOKEN_WATCH_AD_OR_BUY);
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
      case UNKNOWN:
        color = ContextCompat.getColor(getContext(), R.color.menu_button_color_disabled);
        continueGameButton.setOnClickListener(null);
        break;
      case TOKEN_PRESENT:
        color = ContextCompat.getColor(getContext(), R.color.menu_button_color);
        continueGameButton.setOnClickListener(continueGameAndConsumeTokenOnClickListener);
        break;
      case NO_TOKEN_WATCH_AD_OR_BUY:
        color = ContextCompat.getColor(getContext(), R.color.menu_button_color);
        continueGameButton.setOnClickListener(watchAdOrBuyTokenOnClickListener);
        break;
    }
    continueGameButton.setTextColor(color);
  }

  private Context getContext() {
    return continueGameButton.getContext();
  }
}
