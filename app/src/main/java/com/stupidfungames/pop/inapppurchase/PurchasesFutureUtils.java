package com.stupidfungames.pop.inapppurchase;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PurchasesFutureUtils {

  public interface PurchasesCallback {

    void onResult(List<Purchase> purchases);
  }

  /**
   * Given the input future returns
   */
  public static void unwrap(Context context, ListenableFuture<PurchasesResult> input,
      final PurchasesCallback purchasesCallback) {
    Futures.addCallback(input, new FutureCallback<PurchasesResult>() {
      @Override
      public void onSuccess(@NullableDecl PurchasesResult result) {
        if (result != null
            && result.getBillingResult().getResponseCode() == BillingResponseCode.OK
            && result.getPurchasesList() != null) {
          purchasesCallback.onResult(result.getPurchasesList());
        } else {
          purchasesCallback.onResult(new ArrayList<Purchase>());
        }
      }

      @Override
      public void onFailure(Throwable t) {
        purchasesCallback.onResult(new ArrayList<Purchase>());
      }
    }, ContextCompat.getMainExecutor(context));
  }
}
