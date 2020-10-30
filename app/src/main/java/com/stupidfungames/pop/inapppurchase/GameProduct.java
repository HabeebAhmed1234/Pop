package com.stupidfungames.pop.inapppurchase;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import javax.annotation.Nullable;

public class GameProduct {

  public final @DrawableRes int image;
  public final @Nullable String imageFileName;
  public final @StringRes int name;
  public final @StringRes int description;

  public GameProduct(
      @DrawableRes int image,
      @Nullable String imageFileName,
      @StringRes int title,
      @StringRes int description) {
    this.image = image;
    this.imageFileName = imageFileName;
    this.name = title;
    this.description = description;
  }
}
