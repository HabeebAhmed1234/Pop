package com.stupidfungames.pop.inapppurchase;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public class GameProduct {

  public final @DrawableRes int image;
  public final @StringRes int name;
  public final @StringRes int description;

  public GameProduct(
      @DrawableRes int image,
      @StringRes int title,
      @StringRes int description) {
    this.image = image;
    this.name = title;
    this.description = description;
  }
}
