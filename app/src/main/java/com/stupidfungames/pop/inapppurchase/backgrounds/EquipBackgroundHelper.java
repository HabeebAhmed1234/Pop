package com.stupidfungames.pop.inapppurchase.backgrounds;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;
import com.stupidfungames.pop.inapppurchase.GameProduct;
import com.stupidfungames.pop.inapppurchase.ProductSKUManager;

/**
 * Use this to equip a certain background for the game.
 */
public class EquipBackgroundHelper {

  private static final String EQUIPPED_BACKGROUND_PREF = "equipped_background_pref";

  /**
   * Returns true if equip succeeded.
   */
  public static boolean equipBackground(Context context, String sku) {
    ImmutableMap<String, GameProduct> skuToProductsMap = ProductSKUManager.get().skuToProductsMap;
    if (skuToProductsMap.containsKey(sku)) {
      GamePreferencesManager.set(context, EQUIPPED_BACKGROUND_PREF, sku);
      return true;
    }
    return false;
  }

  public static void unequip(Context context) {
    GamePreferencesManager.clear(context, EQUIPPED_BACKGROUND_PREF);
  }

  /**
   * Returns -1 if no background was set.
   */
  @Nullable public static String getBackgroundFileName(Context context) {
    String sku = GamePreferencesManager.getString(context, EQUIPPED_BACKGROUND_PREF);
    ImmutableMap<String, GameProduct> skuToProductsMap = ProductSKUManager.get().skuToProductsMap;
    if (TextUtils.isEmpty(sku) || !skuToProductsMap.containsKey(sku)) {
      return null;
    }
    return skuToProductsMap.get(sku).imageFileName;
  }

  public static boolean isBackgroundEquiped(Context context, String sku) {
    String equippedSku = GamePreferencesManager.getString(context, EQUIPPED_BACKGROUND_PREF);
    return !TextUtils.isEmpty(equippedSku) && equippedSku.equals(sku);
  }

}
