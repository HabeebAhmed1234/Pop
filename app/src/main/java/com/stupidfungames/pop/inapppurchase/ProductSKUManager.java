package com.stupidfungames.pop.inapppurchase;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;

public class ProductSKUManager {

  public enum ProductSKU {
    SKU_COINS_100("pop_coins_100"),
    SKU_COINS_500("pop_coins_500"),
    SKU_COINS_1000("pop_coins_1000"),
    SKU_TEST_PURCHASE("android.test.purchased"),
    SKU_NO_ADS("no_ads"),
    SKU_GAME_CONTINUE("game_continue"),
    SKU_BG_1("bg_1"),
    SKU_BG_2("bg_2"),
    SKU_BG_3("bg_3"),
    SKU_BG_4("bg_4"),
    SKU_BG_5("bg_5"),
    SKU_BG_6("bg_6");

    public String skuString;

    ProductSKU(final String skuString) {
      this.skuString = skuString;
    }

    public static List<String> toStringList() {
      ProductSKU[] skus = ProductSKU.values();
      List<String> skuStrings = new ArrayList<>();
      for (ProductSKU productSKU : skus) {
        skuStrings.add(productSKU.skuString);
      }
      return skuStrings;
    }
  }

  public final ImmutableMap<String, GameProduct> skuToProductsMap;
  private static ProductSKUManager sProductSKUManager;

  private ProductSKUManager() {
    skuToProductsMap = createMap();
  }

  public static final ProductSKUManager get() {
    if (sProductSKUManager == null) {
      sProductSKUManager = new ProductSKUManager();
    }
    return sProductSKUManager;
  }

  private ImmutableMap<String, GameProduct> createMap() {
    ImmutableMap.Builder<String, GameProduct> builder = ImmutableMap.builder();
    return builder
        .put(ProductSKU.SKU_COINS_100.skuString,
            new GameProduct("100 coins", "yer coins bruh 100 of em"))
        .put(ProductSKU.SKU_COINS_500.skuString,
            new GameProduct("500 coins", "yer coins bruh 500 of em"))
        .put(ProductSKU.SKU_COINS_1000.skuString,
            new GameProduct("1000 coins", "yer coins bruh 500 of em"))
        .put(ProductSKU.SKU_TEST_PURCHASE.skuString,
            new GameProduct("Test Purchase", "Test purchase you made"))
        .put(ProductSKU.SKU_NO_ADS.skuString, new GameProduct("Add Free Experience",
            "Ensures that you no longer have to watch ads in the app"))
        .put(ProductSKU.SKU_GAME_CONTINUE.skuString, new GameProduct("Game Continue",
            "Token that lets you resume your game after game over."))
        .put(ProductSKU.SKU_BG_1.skuString, new GameProduct("Back ground 1", "Back ground 1"))
        .put(ProductSKU.SKU_BG_2.skuString, new GameProduct("Back ground 2", "Back ground 2"))
        .build();
  }
}
