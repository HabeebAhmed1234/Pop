package com.stupidfungames.pop.inapppurchase;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import com.android.billingclient.api.Purchase;

public class PurchasesDiffUtilCallback extends DiffUtil.ItemCallback<Purchase> {

  @Override
  public boolean areItemsTheSame(@NonNull Purchase oldItem, @NonNull Purchase newItem) {
    return oldItem.getSku().equals(newItem.getSku());
  }

  @Override
  public boolean areContentsTheSame(@NonNull Purchase oldItem, @NonNull Purchase newItem) {
    return oldItem.getSku().equals(newItem.getSku());
  }
}
