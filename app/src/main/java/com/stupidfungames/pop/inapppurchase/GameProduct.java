package com.stupidfungames.pop.inapppurchase;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import javax.annotation.Nullable;

public class GameProduct {

  public @Nullable String imageFileName;
  public @DrawableRes int imageDrawableRes = -1;

  public final @StringRes int name;
  public final @StringRes int description;

  public GameProduct(
      @Nullable String imageFileName,
      @StringRes int title,
      @StringRes int description) {
    this.imageFileName = imageFileName;
    this.name = title;
    this.description = description;
  }

  public GameProduct(
      @DrawableRes int imageDrawableRes,
      @StringRes int title,
      @StringRes int description) {
    this.imageDrawableRes = imageDrawableRes;
    this.name = title;
    this.description = description;
  }
}
