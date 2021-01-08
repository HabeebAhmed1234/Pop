package com.stupidfungames.pop.continuegame;

import static com.stupidfungames.pop.analytics.Events.CONTINUE_GAME_WITH_SHARE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.ads.AdRoomActivity;
import com.stupidfungames.pop.analytics.Events;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.dialog.GameNeonDialogActivity;
import com.stupidfungames.pop.inapppurchase.GooglePlayServicesBillingManager;
import com.stupidfungames.pop.inapppurchase.ProductSKUManager.ProductSKU;
import com.stupidfungames.pop.share.ShareHostActivity;
import com.stupidfungames.pop.share.SharingManager;
import java.util.Arrays;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Presents the user with the option to watch an ad or buy a game continue token to continue the
 * game.
 */
public class ContinueGameChoiceDialogActivity extends GameNeonDialogActivity implements
    ShareHostActivity {

  // Set if the user earned a game continue token
  public static final int RESULT_TOKEN_ACQUIRED = 1;
  // Set if the user didn't earn a game continue token but completed another action to be able to
  // Continue the game (such as ad watch or share).
  public static final int RESULT_USER_CAN_CONTINUE = 2;

  private GooglePlayServicesAuthManager authManager;
  private GooglePlayServicesBillingManager googlePlayServicesBillingManager;
  private SharingManager sharingManager;

  public static Intent createIntent(Context context) {
    return new Intent(context, ContinueGameChoiceDialogActivity.class);
  }

  @Override
  protected int getTitleResId() {
    return R.string.continue_game_choice_dialog_title;
  }

  @Override
  protected List<ButtonModel> getButtonModels() {
    return Arrays.asList(
        new ButtonModel(R.string.watch_ad_btn, new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            watchAd();
          }
        }), new ButtonModel(R.string.buy_game_continue_token, new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            buyGameContinueToken();
          }
        }), new ButtonModel(R.string.share_the_game_fb, new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            shareGameFb();
          }
        }), new ButtonModel(R.string.share_the_game_other, new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            shareGameOther();
          }
        }));
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    authManager = new GooglePlayServicesAuthManager(this);
    googlePlayServicesBillingManager = new GooglePlayServicesBillingManager(this);
    sharingManager = new SharingManager(this);
  }

  private void watchAd() {
    prepareCall(new StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
      @Override
      public void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == AdRoomActivity.RESULT_AD_WATCHED) {
          onUserCanContinue();
        } else {
          showMustChooseOptionError();
        }
      }
    }).launch(AdRoomActivity.createIntent(this));
  }

  private void buyGameContinueToken() {
    ListenableFuture<List<SkuDetails>> products = googlePlayServicesBillingManager
        .getProducts(this);
    Futures.addCallback(products, new FutureCallback<List<SkuDetails>>() {
      @Override
      public void onSuccess(@NullableDecl List<SkuDetails> result) {
        if (result != null) {
          purchaseGameContinueToken(findGameContinueTokenToPurchase(result));
        } else {
          showGenericError();
        }
      }

      @Override
      public void onFailure(Throwable t) {
        showGenericError();
      }
    }, ContextCompat.getMainExecutor(this));
  }

  private void shareGameFb() {
    Futures.addCallback(sharingManager.shareToFacebook(), new FutureCallback<Boolean>() {
      @Override
      public void onSuccess(@NullableDecl Boolean result) {
        onShareCompleted(result);
      }

      @Override
      public void onFailure(Throwable t) {
        showGenericError();
      }
    }, ContextCompat.getMainExecutor(this));
  }

  private void shareGameOther() {
    Futures.addCallback(sharingManager.shareToAndroid(), new FutureCallback<Boolean>() {
      @Override
      public void onSuccess(@NullableDecl Boolean result) {
        onShareCompleted(result);
      }

      @Override
      public void onFailure(Throwable t) {
        showGenericError();
      }
    }, ContextCompat.getMainExecutor(this));
  }

  private void onShareCompleted(@Nullable Boolean shareSuccess) {
    if (shareSuccess != null && shareSuccess) {
      onUserCanContinue();
      Logger.logSelect(this, CONTINUE_GAME_WITH_SHARE);
    } else {
      showMustChooseOptionError();
    }
  }

  private void purchaseGameContinueToken(SkuDetails gameContinueTokenSkuDetails) {
    Futures.addCallback(
        googlePlayServicesBillingManager.purchase(this, gameContinueTokenSkuDetails),
        new FutureCallback<Purchase>() {
          @Override
          public void onSuccess(@NullableDecl Purchase result) {
            if (result != null) {
              onTokenAquired();
            } else {
              showMustChooseOptionError();
            }
          }

          @Override
          public void onFailure(Throwable t) {
            showMustChooseOptionError();
          }
        }, ContextCompat.getMainExecutor(this));
  }

  private SkuDetails findGameContinueTokenToPurchase(List<SkuDetails> skuDetailsList) {
    for (SkuDetails skuDetails : skuDetailsList) {
      if (skuDetails.getSku().equals(ProductSKU.SKU_GAME_CONTINUE.skuString)) {
        return skuDetails;
      }
    }
    return null;
  }

  private void showGenericError() {
    Toast.makeText(ContinueGameChoiceDialogActivity.this, R.string.generic_error,
        Toast.LENGTH_LONG).show();
  }

  private void showMustChooseOptionError() {
    Toast.makeText(ContinueGameChoiceDialogActivity.this, R.string.must_choose_option_to_continue,
        Toast.LENGTH_LONG).show();
  }

  private void onTokenAquired() {
    setResult(RESULT_TOKEN_ACQUIRED);
    finish();
  }

  private void onUserCanContinue() {
    setResult(RESULT_USER_CAN_CONTINUE);
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

  @Override
  public Activity getActivity() {
    return this;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    sharingManager.onActivityResult(requestCode, resultCode, data);
  }
}
