package com.stupidfungames.pop.purchasedbackground;

import com.stupidfungames.pop.R;
import com.stupidfungames.pop.inapppurchase.ProductSKUManager.ProductSKU;
import java.util.HashMap;
import java.util.Map;

public class PurchaseSkuToBackgroundResId {

  public final Map<String, Integer> map;

  private static PurchaseSkuToBackgroundResId sPurchaseSkuToBackgroundResId;
  public static PurchaseSkuToBackgroundResId get() {
   if (sPurchaseSkuToBackgroundResId == null) {
     sPurchaseSkuToBackgroundResId = new PurchaseSkuToBackgroundResId();
   }
    return sPurchaseSkuToBackgroundResId;
  }

  PurchaseSkuToBackgroundResId() {
    map = new HashMap<>();
    map.put(ProductSKU.SKU_BG_1.skuString, R.drawable.bg_1);
    map.put(ProductSKU.SKU_BG_2.skuString, R.drawable.bg_2);
    map.put(ProductSKU.SKU_BG_3.skuString, R.drawable.bg_3);
    map.put(ProductSKU.SKU_BG_4.skuString, R.drawable.bg_4);
    map.put(ProductSKU.SKU_BG_5.skuString, R.drawable.bg_5);
    map.put(ProductSKU.SKU_BG_6.skuString, R.drawable.bg_6);
    map.put(ProductSKU.SKU_BG_7.skuString, R.drawable.bg_7);
    map.put(ProductSKU.SKU_BG_8.skuString, R.drawable.bg_8);
    map.put(ProductSKU.SKU_BG_9.skuString, R.drawable.bg_9);
    map.put(ProductSKU.SKU_BG_10.skuString, R.drawable.bg_10);
    map.put(ProductSKU.SKU_BG_11.skuString, R.drawable.bg_11);
    map.put(ProductSKU.SKU_BG_12.skuString, R.drawable.bg_12);
    map.put(ProductSKU.SKU_BG_13.skuString, R.drawable.bg_13);
  }
}
