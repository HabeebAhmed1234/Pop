package com.stupidfungames.pop.androidui;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class GlideUtils {

  public static void loadWithImageAssetFileName(
      View view,
      ImageView imageView,
      String fileName) {
    loadWithImageAssetFileName(view.getContext(), imageView, fileName);
  }

  public static void loadWithImageAssetFileName(
      Context context,
      ImageView imageView,
      String fileName) {
    Glide.with(context).load(Uri.parse("file:///android_asset/gfx/" + fileName)).into(imageView);
  }
}
