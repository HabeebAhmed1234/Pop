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
    map.put(ProductSKU.SKU_BG_1.skuString, R.drawable.ic_launcher);
    map.put(ProductSKU.SKU_BG_2.skuString, R.drawable.ic_launcher);
    map.put(ProductSKU.SKU_BG_3.skuString, R.drawable.ic_launcher);
    map.put(ProductSKU.SKU_BG_4.skuString, R.drawable.ic_launcher);
    map.put(ProductSKU.SKU_BG_5.skuString, R.drawable.ic_launcher);
    map.put(ProductSKU.SKU_BG_6.skuString, R.drawable.ic_launcher);
    map.put(ProductSKU.SKU_COINS_100.skuString, R.drawable.ic_launcher);
  }
}
