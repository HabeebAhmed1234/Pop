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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.list.BindableViewHolder;
import com.stupidfungames.pop.list.BindableViewHolderFactory;
import com.stupidfungames.pop.list.LoadableListLoadingCoordinator.LoaderCallback;
import com.stupidfungames.pop.list.LoadableListWithPreviewBaseActivity;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EquipActivity extends LoadableListWithPreviewBaseActivity<Purchase> {

  public static Intent getIntent(Context context) {
    return new Intent(context, EquipActivity.class);
  }

  private GooglePlayServicesBillingManager billingManager;

  private BindableViewHolderFactory bindableViewHolderFactory = new BindableViewHolderFactory() {
    @Override
    public int getLayoutId() {
      return R.layout.equip_list_item;
    }

    @Override
    public BindableViewHolder create(ViewGroup parentView) {
      return new PurchaseViewHolder(parentView);
    }
  };

  private LoaderCallback<List<Purchase>> loaderCallback = new LoaderCallback<List<Purchase>>() {
    @Override
    public ListenableFuture<List<Purchase>> loadData() {
      final SettableFuture<List<Purchase>> futureResult = SettableFuture.create();
      Futures.addCallback(billingManager.queryPurchases(), new FutureCallback<PurchasesResult>() {
        @Override
        public void onSuccess(@NullableDecl PurchasesResult result) {
          if (result != null
              && result.getBillingResult().getResponseCode() == BillingResponseCode.OK) {
            futureResult.set(result.getPurchasesList());
          } else {
            futureResult
                .setException(new IllegalStateException("Error when loading purchases list"));
          }
        }

        @Override
        public void onFailure(Throwable t) {
          futureResult.setException(t);
        }
      }, ContextCompat.getMainExecutor(EquipActivity.this));
      return futureResult;
    }

    @Override
    public boolean isEmptyResult(List<Purchase> result) {
      return result.isEmpty();
    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    billingManager = new GooglePlayServicesBillingManager(this);
    super.onCreate(savedInstanceState);
    ((AdView) findViewById(R.id.adView)).loadAd(((new AdRequest.Builder()).build()));
  }

  @Override
  protected String getPreviewImageFileName(Purchase purchase) {
    String purchaseSku = purchase.getSku();
    ImmutableMap<String, GameProduct> skuToProductsMap = ProductSKUManager.get().skuToProductsMap;
    if (skuToProductsMap.containsKey(purchaseSku)) {
      return skuToProductsMap.get(purchaseSku).imageFileName;
    }
    return null;
  }

  @Override
  protected int getPreviewImageDrawableResId(Purchase purchase) {
    String purchaseSku = purchase.getSku();
    ImmutableMap<String, GameProduct> skuToProductsMap = ProductSKUManager.get().skuToProductsMap;
    if (skuToProductsMap.containsKey(purchaseSku)) {
      return skuToProductsMap.get(purchaseSku).imageDrawableRes;
    }
    return -1;
  }

  @Override
  protected String getPreviewBackgroundImageFileName(Purchase purchase) {
    String purchaseSku = purchase.getSku();
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
  protected int getLayoutId() {
    return R.layout.equip_activity_layout;
  }

  @Override
  protected BindableViewHolderFactory getViewHolderFactory() {
    return bindableViewHolderFactory;
  }

  @Override
  protected ItemCallback getDiffutilCallback() {
    return new PurchasesDiffUtilCallback();
  }

  @Override
  protected LoaderCallback<List<Purchase>> getLoaderCallback() {
    return loaderCallback;
  }

  @Override
  protected String getLogListName() {
    return "EquipActivity";
  }
}
