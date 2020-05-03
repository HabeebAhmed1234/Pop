package com.stupidfungames.pop.inapppurchase;

import android.content.Context;
import androidx.annotation.Nullable;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GooglePlayServicesBillingManager implements PurchasesUpdatedListener {

  private static final List<String> productIdsList = new ArrayList<String>(
      Arrays.asList(
          "pop_coins_100",
          "pop_coins_500",
          "pop_coins_1000"));

  private BillingClient billingClient;

  public void startConnection(Context context) {
    billingClient = BillingClient.newBuilder(context).setListener(this).build();
    billingClient.startConnection(new BillingClientStateListener() {
      @Override
      public void onBillingSetupFinished(BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingResponseCode.OK) {
          // The BillingClient is ready. You can query purchases here.
        }
      }

      @Override
      public void onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.

      }
    });
  }

  public ListenableFuture<List<SkuDetails>> getProducts() {
    final SettableFuture<List<SkuDetails>> productsSettableFuture = SettableFuture.create();
    billingClient.querySkuDetailsAsync(
        SkuDetailsParams.newBuilder().setSkusList(productIdsList).build(),
        new SkuDetailsResponseListener() {
          @Override
          public void onSkuDetailsResponse(BillingResult result, List<SkuDetails> skuDetailsList) {
            if (result.getResponseCode() == BillingResponseCode.OK && skuDetailsList != null) {
              productsSettableFuture.set(skuDetailsList);
            } else {
              productsSettableFuture.setException(
                  new IllegalStateException(
                      "Something went wrong when fetching the list of products"));
            }
          }
        });
    return productsSettableFuture;
  }
  @Override
  public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {

  }
}
