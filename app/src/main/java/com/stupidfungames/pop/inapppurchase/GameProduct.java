package com.stupidfungames.pop.inapppurchase;

import androidx.annotation.StringRes;

public class GameProduct {

  public final @StringRes int name;
  public final @StringRes int description;

  public GameProduct(
      @StringRes int title,
      @StringRes int description) {
    this.name = title;
    this.description = description;
  }
}
