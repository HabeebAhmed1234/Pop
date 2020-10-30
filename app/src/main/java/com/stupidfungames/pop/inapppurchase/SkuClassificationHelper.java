package com.stupidfungames.pop.inapppurchase;

/**
 * Given the Sku string tells you what kind of product it is.
 */
public class SkuClassificationHelper {

  public static boolean isEquippablePurchase(String sku) {
    return isBackground(sku);
  }

  public static boolean isBackground(String sku) {
    return sku.startsWith("bg_");
  }
}
