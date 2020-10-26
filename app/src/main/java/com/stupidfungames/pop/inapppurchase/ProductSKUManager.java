package com.stupidfungames.pop.inapppurchase;

import com.google.common.collect.ImmutableMap;
import com.stupidfungames.pop.R;
import java.util.ArrayList;
import java.util.List;

public class ProductSKUManager {

  public enum ProductSKU {
    SKU_NO_ADS("no_ads"),
    SKU_GAME_CONTINUE("game_continue_token"),
    SKU_BG_1("bg_1"),
    SKU_BG_2("bg_2"),
    SKU_BG_3("bg_3"),
    SKU_BG_4("bg_4"),
    SKU_BG_5("bg_5"),
    SKU_BG_6("bg_6"),
    SKU_BG_7("bg_7"),
    SKU_BG_8("bg_8"),
    SKU_BG_9("bg_9"),
    SKU_BG_10("bg_10"),
    SKU_BG_11("bg_11"),
    SKU_BG_12("bg_12"),
    SKU_BG_13("bg_13"),
    ;

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
        .put(ProductSKU.SKU_NO_ADS.skuString,
            new GameProduct(R.drawable.ic_launcher, R.string.title_no_ads,
                R.string.description_no_ads))
        .put(ProductSKU.SKU_GAME_CONTINUE.skuString,
            new GameProduct(R.drawable.ic_launcher, R.string.title_game_continue_token,
                R.string.description_game_continue_token))
        .put(ProductSKU.SKU_BG_1.skuString,
            new GameProduct(R.drawable.bg_1, R.string.title_bg_1, R.string.description_bg_1))
        .put(ProductSKU.SKU_BG_2.skuString,
            new GameProduct(R.drawable.bg_2, R.string.title_bg_2, R.string.description_bg_2))
        .put(ProductSKU.SKU_BG_3.skuString,
            new GameProduct(R.drawable.bg_3, R.string.title_bg_3, R.string.description_bg_3))
        .put(ProductSKU.SKU_BG_4.skuString,
            new GameProduct(R.drawable.bg_4, R.string.title_bg_4, R.string.description_bg_4))
        .put(ProductSKU.SKU_BG_5.skuString,
            new GameProduct(R.drawable.bg_5, R.string.title_bg_5, R.string.description_bg_5))
        .put(ProductSKU.SKU_BG_6.skuString,
            new GameProduct(R.drawable.bg_6, R.string.title_bg_6, R.string.description_bg_6))
        .put(ProductSKU.SKU_BG_7.skuString,
            new GameProduct(R.drawable.bg_7, R.string.title_bg_7, R.string.description_bg_7))
        .put(ProductSKU.SKU_BG_8.skuString,
            new GameProduct(R.drawable.bg_8, R.string.title_bg_8, R.string.description_bg_8))
        .put(ProductSKU.SKU_BG_9.skuString,
            new GameProduct(R.drawable.bg_9, R.string.title_bg_9, R.string.description_bg_9))
        .put(ProductSKU.SKU_BG_10.skuString,
            new GameProduct(R.drawable.bg_10, R.string.title_bg_10, R.string.description_bg_10))
        .put(ProductSKU.SKU_BG_11.skuString,
            new GameProduct(R.drawable.bg_11, R.string.title_bg_11, R.string.description_bg_11))
        .put(ProductSKU.SKU_BG_12.skuString,
            new GameProduct(R.drawable.bg_12, R.string.title_bg_12, R.string.description_bg_12))
        .put(ProductSKU.SKU_BG_13.skuString,
            new GameProduct(R.drawable.bg_13, R.string.title_bg_13, R.string.description_bg_13))
        .build();
  }
}
