package com.stupidfungames.pop.inapppurchase;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.stupidfungames.pop.inapppurchase.ProductSKUManager.ProductSKU;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ProductsSorter {

  public static void sortProducts(final List<SkuDetails> products, final Set<String> purchasesSkus) {
    Collections.sort(products, new Comparator<SkuDetails>() {
      @Override
      public int compare(SkuDetails o1, SkuDetails o2) {
        if (purchasesSkus.contains(o1.getSku())) {
          return 1;
        }
        if (purchasesSkus.contains(o2.getSku())) {
          return -1;
        }
        if (o1.getSku().equals(ProductSKU.SKU_GAME_CONTINUE.skuString)) {
          return -1;
        }
        if (o2.getSku().equals(ProductSKU.SKU_GAME_CONTINUE.skuString)) {
          return 1;
        }
        return o1.getSku().compareTo(o2.getSku());
      }
    });
  }

  public static void sortPurchases(List<Purchase> purchases) {
    Collections.sort(purchases, new Comparator<Purchase>() {
      @Override
      public int compare(Purchase o1, Purchase o2) {
        if (o1.getSku().equals(ProductSKU.SKU_GAME_CONTINUE.skuString)) {
          return -1;
        }
        if (o2.getSku().equals(ProductSKU.SKU_GAME_CONTINUE.skuString)) {
          return 1;
        }
        return o1.getSku().compareTo(o2.getSku());
      }
    });
  }
}
