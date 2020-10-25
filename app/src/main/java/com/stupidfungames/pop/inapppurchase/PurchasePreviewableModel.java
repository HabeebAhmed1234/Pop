package com.stupidfungames.pop.inapppurchase;


import androidx.annotation.DrawableRes;
import com.android.billingclient.api.Purchase;
import com.stupidfungames.pop.list.LoadableListWithPreviewBaseActivity.PreviewableModel;

public class PurchasePreviewableModel implements PreviewableModel {

  public final @DrawableRes
  int previewResID;
  public final Purchase purchase;

  PurchasePreviewableModel(@DrawableRes int previewResID, Purchase purchase) {
    this.previewResID = previewResID;
    this.purchase = purchase;
  }

  @Override
  public int getPreviewResId() {
    return previewResID;
  }
}

