package com.stupidfungames.pop.inapppurchase;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class ProductsInfoMap {

  public static final String SKU_NO_ADS = "no_ads";
  public static final String SKU_GAME_CONTINUE = "game_continue";
  public static final String SKU_BG_1 = "bg_1";
  public static final String SKU_BG_2 = "bg_2";

  public static Map<String, GameProduct> skuToProductsMap = ImmutableMap.of(
      SKU_NO_ADS, new GameProduct("no_ads", "Add Free Experience", "Ensures that you no longer have to watch ads in the app"),
      SKU_GAME_CONTINUE, new GameProduct("game_continue", "Game Continue", "Token that lets you resume your game after game over."),
      SKU_BG_1, new GameProduct("bg_1", "Back ground 1", "Back ground 1"),
      SKU_BG_2, new GameProduct("bg_2", "Back ground 2", "Back ground 2")
  );

  public static class GameProduct {
    public final String sku;
    public final String name;
    public final String description;

    public GameProduct(
        String sku,
        String name,
        String description) {
      this.sku = sku;
      this.name = name;
      this.description = description;
    }
  }
}
