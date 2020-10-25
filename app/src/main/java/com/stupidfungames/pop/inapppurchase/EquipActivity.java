package com.stupidfungames.pop.inapppurchase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.list.BindableViewHolder;
import com.stupidfungames.pop.list.BindableViewHolderFactory;
import com.stupidfungames.pop.list.LoadableListLoadingCoordinator.LoaderCallback;
import com.stupidfungames.pop.list.LoadableListWithPreviewBaseActivity;
import com.stupidfungames.pop.purchasedbackground.PurchaseSkuToBackgroundResId;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EquipActivity extends LoadableListWithPreviewBaseActivity<PurchasePreviewableModel> {

  public static Intent getIntent(Context context) {
    return new Intent(context, EquipActivity.class);
  }

  GooglePlayServicesBillingManager billingManager;

  private BindableViewHolderFactory bindableViewHolderFactory = new BindableViewHolderFactory() {
    @Override
    public int getLayoutId() {
      return R.layout.purchase_list_item;
    }

    @Override
    public BindableViewHolder create(ViewGroup parentView) {
      return new PurchaseViewHolder(parentView);
    }
  };

  private LoaderCallback<List<PurchasePreviewableModel>> loaderCallback = new LoaderCallback<List<PurchasePreviewableModel>>() {
    @Override
    public ListenableFuture<List<PurchasePreviewableModel>> loadData() {
      final SettableFuture<List<PurchasePreviewableModel>> futureResult = SettableFuture.create();
      Futures.addCallback(billingManager.queryPurchases(), new FutureCallback<PurchasesResult>() {
        @Override
        public void onSuccess(@NullableDecl PurchasesResult result) {
          if (result != null
              && result.getBillingResult().getResponseCode() == BillingResponseCode.OK) {
            futureResult.set(parsePurchases(result.getPurchasesList()));
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
    public boolean isEmptyResult(List<PurchasePreviewableModel> result) {
      return result.isEmpty();
    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    billingManager = new GooglePlayServicesBillingManager(this);
    super.onCreate(savedInstanceState);
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
  protected LoaderCallback<List<PurchasePreviewableModel>> getLoaderCallback() {
    return loaderCallback;
  }

  private static List<PurchasePreviewableModel> parsePurchases(List<Purchase> purchases) {
    final List<PurchasePreviewableModel> parsedPurchases = new ArrayList<>();
    for (Purchase purchase : purchases) {
      @DrawableRes int drawableResId = 0;
      String purchaseSku = purchase.getSku();
      if (PurchaseSkuToBackgroundResId.get().map.containsKey(purchaseSku)) {
        drawableResId = PurchaseSkuToBackgroundResId.get().map.get(purchaseSku);
      }
      parsedPurchases.add(new PurchasePreviewableModel(drawableResId, purchase));
    }
    return parsedPurchases;
  }
}
