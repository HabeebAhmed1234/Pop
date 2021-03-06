package com.stupidfungames.pop.inapppurchase;

import static com.stupidfungames.pop.analytics.Events.PURCHASE_CANCELED;
import static com.stupidfungames.pop.analytics.Events.PURCHASE_FAILED;
import static com.stupidfungames.pop.analytics.Events.PURCHASE_START;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Manages all calls to the play services billing manager. If the user is not logged in then it will
 * either prompt them to login or it will throw an exception.
 */
public class GooglePlayServicesBillingManager implements PurchasesUpdatedListener {

  private static final String TAG = "GPSBillingManager";

  private HostActivity hostActivity;
  private GooglePlayServicesAuthManager authManager;
  private BillingClient billingClient;

  @Nullable
  private Pair<SkuDetails, SettableFuture<Purchase>> productPurchaseFuture;

  private interface BillingClientReadyCallback {

    void onBillingClientReady();

    void onBillingClientError(@Nullable Throwable e);
  }

  public GooglePlayServicesBillingManager(HostActivity hostActivity) {
    this.hostActivity = hostActivity;
    this.authManager = hostActivity.getAuthManager();
    billingClient = BillingClient.newBuilder(hostActivity.getContext())
        .enablePendingPurchases()
        .setListener(this)
        .build();
  }

  public ListenableFuture<List<SkuDetails>> getProducts(final Context context) {
    final SettableFuture<List<SkuDetails>> productsSettableFuture = SettableFuture.create();
    ensureConnection(new BillingClientReadyCallback() {
      @Override
      public void onBillingClientReady() {
        Futures.addCallback(getProductsInternal(), new FutureCallback<List<SkuDetails>>() {
          @Override
          public void onSuccess(@NullableDecl List<SkuDetails> result) {
            if (result != null) {
              productsSettableFuture.set(result);
            } else {
              productsSettableFuture
                  .setException(new IllegalStateException("null products returned"));
            }
          }

          @Override
          public void onFailure(Throwable t) {
            productsSettableFuture.setException(t);
          }
        }, ContextCompat.getMainExecutor(context));
      }

      @Override
      public void onBillingClientError(@Nullable Throwable e) {
        productsSettableFuture.setException(e);
      }
    }, true);

    return productsSettableFuture;
  }

  /**
   * Returns a listenable future which gets set to the Purchase that is successfully made. Else
   * fails.
   */
  public ListenableFuture<Purchase> purchase(final Activity activity, final SkuDetails skuDetails) {
    ListenableFuture<BillingResult> result = launchPurchaseFlow(activity, skuDetails);
    if (productPurchaseFuture != null) {
      onPurchaseFailed("Purchase already in progress");
    }
    productPurchaseFuture = new Pair<>(skuDetails, SettableFuture.<Purchase>create());
    Futures.addCallback(result, new FutureCallback<BillingResult>() {
      @Override
      public void onSuccess(@NullableDecl BillingResult result) {
        int toastResId = -1;
        if (result != null) {
          switch (result.getResponseCode()) {
            case BillingResponseCode.OK:
              Logger.logSelect(hostActivity.getContext(), Event.PURCHASE, skuDetails.getSku());
              break;
            case BillingResponseCode.USER_CANCELED:
              toastResId = R.string.purchase_canceled;
              Logger.logSelect(hostActivity.getContext(), PURCHASE_CANCELED, skuDetails.getSku());
              onPurchaseFailed(null);
              break;
            default:
              onPurchaseFailed("Billing flow error error: " + result.getResponseCode());
          }
        }
        if (toastResId != -1) {
          Toast.makeText(activity, toastResId, Toast.LENGTH_LONG).show();
        }
      }

      @Override
      public void onFailure(Throwable t) {
        Toast.makeText(activity, R.string.generic_error, Toast.LENGTH_LONG).show();
        onPurchaseFailed("launch purchase flow exception");
      }
    }, ContextCompat.getMainExecutor(activity));
    return productPurchaseFuture.second;
  }

  private void onPurchaseFailed(@Nullable String reason) {
    if (productPurchaseFuture != null && productPurchaseFuture.second != null) {
      productPurchaseFuture.second.setException(new IllegalStateException("Purchase failed"));
    }
    productPurchaseFuture = null;

    if (!TextUtils.isEmpty(reason)) {
      Logger.logSelect(hostActivity.getContext(), PURCHASE_FAILED, reason);
    }
  }

  private void onPurchaseSuccess(Purchase purchase) {
    if (productPurchaseFuture != null && productPurchaseFuture.second != null
        && productPurchaseFuture.first != null && productPurchaseFuture.first.getSku()
        .equals(purchase.getSku())) {
      productPurchaseFuture.second.set(purchase);
    }
  }

  /**
   * Checks if the user has the given sku purchased and active. Returns the purchase object.
   */
  public ListenableFuture<Purchase> hasPurchase(final String sku) {
    final SettableFuture<Purchase> hasPurchase = SettableFuture.create();
    ListenableFuture<PurchasesResult> purchases = queryPurchases();
    Futures.addCallback(
        purchases,
        new FutureCallback<PurchasesResult>() {

          @Override
          public void onSuccess(@NullableDecl PurchasesResult result) {
            if (result == null
                || result.getBillingResult().getResponseCode() != BillingResponseCode.OK) {
              hasPurchase.set(null);
            }
            List<Purchase> purchases = result.getPurchasesList();
            for (Purchase purchase : purchases) {
              if (purchase.getSku().equals(sku)) {
                hasPurchase.set(purchase);
                return;
              }
            }
            hasPurchase.set(null);
          }

          @Override
          public void onFailure(Throwable t) {
            hasPurchase.setException(t);
          }
        },
        ContextCompat.getMainExecutor(hostActivity.getContext()));
    return hasPurchase;
  }

  /**
   * Consumes the given purchased item.
   */
  public void consume(Purchase purchase) {
    ConsumeParams consumeParams =
        ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.getPurchaseToken())
            .build();

    billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
      @Override
      public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
      }
    });
  }

  public ListenableFuture<PurchasesResult> queryPurchases() {
    final SettableFuture<PurchasesResult> resultSettableFuture = SettableFuture.create();
    ensureConnection(new BillingClientReadyCallback() {
      @Override
      public void onBillingClientReady() {
        resultSettableFuture.set(billingClient.queryPurchases(SkuType.INAPP));
      }

      @Override
      public void onBillingClientError(@Nullable Throwable e) {
        resultSettableFuture.setException(e);
      }
    }, false);
    return resultSettableFuture;
  }

  private void checkLoginThenStartConnection(final BillingClientReadyCallback readyCallback) {
    if (!authManager.isLoggedIn()) {
      authManager.initiateLogin(hostActivity, new LoginListener() {
        @Override
        public void onLoginStart() {
        }

        @Override
        public void onLoggedIn(GoogleSignInAccount account) {
          startConnection(readyCallback);
        }

        @Override
        public void onLoggedOut() {
          readyCallback.onBillingClientError(new IllegalStateException("User logged out"));
        }

        @Override
        public void onLoginCanceled() {
          readyCallback.onBillingClientError(new IllegalStateException("User cancelled login"));
        }

        @Override
        public void onLoginFailed(Exception e) {
          readyCallback.onBillingClientError(e);
        }
      });
    } else {
      startConnection(readyCallback);
    }
  }

  private boolean isConnecting = false;
  private List<BillingClientReadyCallback> readyCallbacks = new ArrayList<>();

  private void startConnection(final BillingClientReadyCallback readyCallback) {
    if (!authManager.isLoggedIn()) {
      readyCallback.onBillingClientError(
          new IllegalStateException(
              "Cannot load a billing connection if user is not logged in."));
      return;
    }
    if (billingClient.isReady()) {
      readyCallback.onBillingClientReady();
      return;
    }

    readyCallbacks.add(readyCallback);

    if (!isConnecting) {
      isConnecting = true;
      billingClient.startConnection(new BillingClientStateListener() {
        @Override
        public void onBillingSetupFinished(BillingResult billingResult) {
          isConnecting = false;
          if (billingResult.getResponseCode() == BillingResponseCode.OK) {
            // The BillingClient is ready. You can query purchases here.
            Iterator<BillingClientReadyCallback> it = readyCallbacks.iterator();
            while (it.hasNext()) {
              it.next().onBillingClientReady();
              it.remove();
            }
          } else {
            readyCallback
                .onBillingClientError(new IllegalStateException(billingResult.getDebugMessage()));
          }

        }

        @Override
        public void onBillingServiceDisconnected() {
          isConnecting = false;
        }
      });
    }
  }

  /**
   * Ensures that the user is logged in and billing client is connected. then runs the
   * billingClientReadyCallback after.
   *
   * @param showLogin true if we want to show login flow if the user is not logged in false if we
   * want to error if user is not logged in
   */
  private void ensureConnection(
      final BillingClientReadyCallback billingClientReadyCallback,
      final boolean showLogin) {
    if (authManager.isLoggedIn() && billingClient.isReady()) {
      billingClientReadyCallback.onBillingClientReady();
    } else if (showLogin) {
      checkLoginThenStartConnection(billingClientReadyCallback);
    } else {
      startConnection(billingClientReadyCallback);
    }
  }

  private ListenableFuture<BillingResult> launchPurchaseFlow(
      final Activity activity, final SkuDetails skuDetails) {

    // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
    final SettableFuture<BillingResult> result = SettableFuture.create();
    ensureConnection(new BillingClientReadyCallback() {
      @Override
      public void onBillingClientReady() {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build();
        result.set(billingClient.launchBillingFlow(activity, flowParams));

        Logger.logSelect(hostActivity.getContext(), PURCHASE_START, skuDetails.getSku());
      }

      @Override
      public void onBillingClientError(@Nullable Throwable e) {
        result.setException(e);
      }
    }, true);
    return result;
  }

  private ListenableFuture<List<SkuDetails>> getProductsInternal() {
    final SettableFuture<List<SkuDetails>> productsSettableFuture = SettableFuture.create();
    billingClient.querySkuDetailsAsync(
        SkuDetailsParams.newBuilder().setSkusList(ProductSKUManager.ProductSKU.toStringList())
            .setType(SkuType.INAPP).build(),
        new SkuDetailsResponseListener() {
          @Override
          public void onSkuDetailsResponse(BillingResult result, List<SkuDetails> skuDetailsList) {
            if (result.getResponseCode() == BillingResponseCode.OK && skuDetailsList != null) {
              productsSettableFuture.set(skuDetailsList);
              //productsSettableFuture.set(generateTestListOfSampleProducts(skuDetailsList));
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
    if (billingResult.getResponseCode() == BillingResponseCode.USER_CANCELED || list == null) {
      onPurchaseFailed(
          "onPurchasesUpdated BillingResponseCode.USER_CANCELED = "
              + (billingResult.getResponseCode() == BillingResponseCode.USER_CANCELED)
              + " or null list = " + (list == null));
      return;
    }
    for (final Purchase purchase : list) {
      if (!purchase.isAcknowledged()) {
        billingClient.acknowledgePurchase(
            AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken())
                .build(), new AcknowledgePurchaseResponseListener() {
              @Override
              public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
              }
            });
      }
      if (productPurchaseFuture != null && productPurchaseFuture.first != null
          && productPurchaseFuture.first.getSku().equals(purchase.getSku())) {
        onPurchaseSuccess(purchase);
      }
    }
  }

  private static final int NUM_SAMPLES = 20;

  private List<SkuDetails> generateTestListOfSampleProducts(List<SkuDetails> seed) {
    List<SkuDetails> samples = new ArrayList<>();
    if (seed == null || seed.isEmpty()) {
      return samples;
    }
    for (int i = 0; i < NUM_SAMPLES; i++) {
      samples.add(seed.get(0));
    }
    return samples;
  }
}
