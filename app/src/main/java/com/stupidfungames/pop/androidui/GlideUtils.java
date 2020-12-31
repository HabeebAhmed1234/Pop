package com.stupidfungames.pop.androidui;

import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class GlideUtils {

  public static void loadWithImageAssetFileName(
      ImageView imageView,
      String fileName) {
    Glide.with(imageView.getContext()).load(Uri.parse("file:///android_asset/gfx/" + fileName))
        .into(imageView);
  }
}
