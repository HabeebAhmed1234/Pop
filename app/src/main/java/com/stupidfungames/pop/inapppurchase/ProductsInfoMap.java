package com.stupidfungames.pop.inapppurchase;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class ProductsInfoMap {

  public static Map<String, GameProduct> skuToProductsMap = ImmutableMap.of(
      "no_ads", new GameProduct("no_ads", "Add Free Experience", "Ensures that you no longer have to watch ads in the app"),
      "game_continue", new GameProduct("game_continue", "Game Continue", "Token that lets you resume your game after game over."),
      "bg_1", new GameProduct("bg_1", "Back ground 1", "Back ground 1"),
      "bg_2", new GameProduct("bg_2", "Back ground 2", "Back ground 2")
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
