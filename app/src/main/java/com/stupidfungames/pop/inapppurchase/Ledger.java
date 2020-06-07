package com.stupidfungames.pop.inapppurchase;

import com.android.billingclient.api.Purchase;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Model used by the app to track what purchases the user has made and to reflect them in the UI
 * appropriately.
 */
public class Ledger implements Serializable {
  
  private final Set<Purchase> purchases;

  public Ledger(List<Purchase> purchases) {
    this.purchases = new HashSet<>(purchases);
  }

  /**
   * Adds a new or updates an existing purchase in the ledger to its latest state.
   * @param purchase
   */
  public void updatePurchase(Purchase purchase) {
    purchases.add(purchase);
  }
}
