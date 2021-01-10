package com.stupidfungames.pop.inapppurchase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.android.billingclient.api.SkuDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.list.BindableViewHolder;
import com.stupidfungames.pop.list.BindableViewHolderFactory;
import com.stupidfungames.pop.list.LoadableListLoadingCoordinator.LoaderCallback;
import com.stupidfungames.pop.list.LoadableListWithPreviewBaseActivity;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * This activity displays a list of all products available in the app. If a product is purchased it
 * saves that product item to the "economy" save game in the save game manager.
 */
public class StoreActivity extends LoadableListWithPreviewBaseActivity<SkuDetails> {

  public static Intent getIntent(Context context) {
    return new Intent(context, StoreActivity.class);
  }

  private GooglePlayServicesBillingManager billingManager;
  private Set<String> purchasesSkus = new HashSet<>();

  private LoaderCallback<List<SkuDetails>> loaderCallback = new LoaderCallback<List<SkuDetails>>() {
    @Override
    public ListenableFuture<List<SkuDetails>> loadData() {

      final ListenableFuture<List<SkuDetails>> products = billingManager
          .getProducts(StoreActivity.this);

      final SettableFuture<List<SkuDetails>> productsPropagateFuture = SettableFuture.create();
      Futures.addCallback(products, new FutureCallback<List<SkuDetails>>() {
        @Override
        public void onSuccess(@NullableDecl final List<SkuDetails> products) {
          if (products != null) {
            final ListenableFuture<PurchasesResult> purchases = billingManager.queryPurchases();
            Futures.addCallback(purchases, new FutureCallback<PurchasesResult>() {
              @Override
              public void onSuccess(@NullableDecl PurchasesResult purchasesResult) {
                if (purchasesResult != null
                    && purchasesResult.getBillingResult().getResponseCode()
                    == BillingResponseCode.OK
                    && !purchasesResult.getPurchasesList().isEmpty()) {
                  for (Purchase purchase : purchasesResult.getPurchasesList()) {
                    purchasesSkus.add(purchase.getSku());
                  }
                }

                ProductsSorter.sortProducts(products, purchasesSkus);
                productsPropagateFuture.set(products);
              }

              @Override
              public void onFailure(Throwable t) {
                ProductsSorter.sortProducts(products, purchasesSkus);
                productsPropagateFuture.set(products);
              }
            }, ContextCompat.getMainExecutor(StoreActivity.this));
          } else {
            productsPropagateFuture.setException(new IllegalStateException("Null SkuDetails"));
          }
        }

        @Override
        public void onFailure(Throwable t) {
          productsPropagateFuture.setException(t);
        }
      }, ContextCompat.getMainExecutor(StoreActivity.this));

      return productsPropagateFuture;
    }

    @Override
    public boolean isEmptyResult(List<SkuDetails> result) {
      return result.isEmpty();
    }
  };

  private BindableViewHolderFactory bindableViewHolderFactory = new BindableViewHolderFactory() {
    @Override
    public int getLayoutId() {
      return R.layout.store_list_item;
    }

    @Override
    public BindableViewHolder create(ViewGroup parentView) {
      return new StoreProductViewHolder(billingManager, StoreActivity.this, parentView,
          purchasesSkus);
    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    billingManager = new GooglePlayServicesBillingManager(this);
    super.onCreate(savedInstanceState);

    ((AdView) findViewById(R.id.adView)).loadAd(((new AdRequest.Builder()).build()));
  }

  @Override
  protected int getLayoutId() {
    return R.layout.store_activity_layout;
  }

  @Override
  protected BindableViewHolderFactory getViewHolderFactory() {
    return bindableViewHolderFactory;
  }

  @Override
  protected ItemCallback getDiffutilCallback() {
    return new SkuDetailsDiffUtilCallback();
  }

  @Override
  protected LoaderCallback<List<SkuDetails>> getLoaderCallback() {
    return loaderCallback;
  }

  @Override
  protected String getLogListName() {
    return "StoreActivity";
  }

  @Override
  protected String getPreviewImageFileName(SkuDetails item) {
    String purchaseSku = item.getSku();
    ImmutableMap<String, GameProduct> skuToProductsMap = ProductSKUManager.get().skuToProductsMap;
    if (skuToProductsMap.containsKey(purchaseSku)) {
      return skuToProductsMap.get(purchaseSku).imageFileName;
    }
    return null;
  }

  @Override
  protected int getPreviewImageDrawableResId(SkuDetails item) {
    String purchaseSku = item.getSku();
    ImmutableMap<String, GameProduct> skuToProductsMap = ProductSKUManager.get().skuToProductsMap;
    if (skuToProductsMap.containsKey(purchaseSku)) {
      return skuToProductsMap.get(purchaseSku).imageDrawableRes;
    }
    return -1;
  }

  @Override
  protected String getPreviewBackgroundImageFileName(SkuDetails item) {
    String purchaseSku = item.getSku();
    // only show a background preview for bg images
    if (purchaseSku.contains("bg_")) {
      ImmutableMap<String, GameProduct> skuToProductsMap = ProductSKUManager.get().skuToProductsMap;
      if (skuToProductsMap.containsKey(purchaseSku)) {
        return skuToProductsMap.get(purchaseSku).imageFileName;
      }
    }
    return null;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    EventBus.get().onDestroy(false);
  }
}
