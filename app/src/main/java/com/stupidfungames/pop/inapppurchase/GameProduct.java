package com.stupidfungames.pop.inapppurchase;

import androidx.annotation.StringRes;
import javax.annotation.Nullable;

public class GameProduct {

  public final @Nullable
  String imageFileName;
  public final @StringRes
  int name;
  public final @StringRes
  int description;

  public GameProduct(
      @Nullable String imageFileName,
      @StringRes int title,
      @StringRes int description) {
    this.imageFileName = imageFileName;
    this.name = title;
    this.description = description;
  }
}
