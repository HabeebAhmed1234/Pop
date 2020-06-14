package com.stupidfungames.pop.inapppurchase;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import com.android.billingclient.api.SkuDetails;

public class SkuDetailsDiffUtilCallback extends DiffUtil.ItemCallback<SkuDetails> {

  @Override
  public boolean areItemsTheSame(@NonNull SkuDetails oldItem, @NonNull SkuDetails newItem) {
    return oldItem.getSku().equals(newItem.getSku());
  }

  @Override
  public boolean areContentsTheSame(@NonNull SkuDetails oldItem, @NonNull SkuDetails newItem) {
    return oldItem.getSku().equals(newItem.getSku());
  }
}
