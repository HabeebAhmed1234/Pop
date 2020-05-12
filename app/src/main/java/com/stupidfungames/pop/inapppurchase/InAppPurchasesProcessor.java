package com.stupidfungames.pop.inapppurchase;

import android.app.Activity;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.SkuDetails;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.stupidfungames.pop.R;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Takes in the result of successful in app purchases and reflects them in the user's persistent
 * storage and save data.
 */
public class InAppPurchasesProcessor {

  private Activity activity;
  private GooglePlayServicesBillingManager billingManager;

  public InAppPurchasesProcessor(Activity activity, GooglePlayServicesBillingManager billingManager) {
    this.activity = activity;
    this.billingManager = billingManager;
  }

  public void purchase(SkuDetails skuDetails) {
    ListenableFuture<BillingResult> result = billingManager.launchPurchaseFlow(activity, skuDetails);
    Futures.addCallback(result, new FutureCallback<BillingResult>() {
      @Override
      public void onSuccess(@NullableDecl BillingResult result) {
        int toastResId = R.string.generic_error;
        if (result != null) {
          switch (result.getResponseCode()) {
            case BillingResponseCode.OK:
              toastResId = R.string.purchase_succesful;
            case BillingResponseCode.USER_CANCELED:
              toastResId = R.string.purchase_canceled;
          }
        }
        Toast.makeText(activity, toastResId, Toast.LENGTH_LONG).show();
      }

      @Override
      public void onFailure(Throwable t) {
        Toast.makeText(activity, R.string.generic_error, Toast.LENGTH_LONG).show();
      }
    }, ContextCompat.getMainExecutor(activity));
  }
}
