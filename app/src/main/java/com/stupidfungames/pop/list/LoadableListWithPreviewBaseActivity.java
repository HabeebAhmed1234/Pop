package com.stupidfungames.pop.list;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.stupidfungames.pop.R;

public abstract class LoadableListWithPreviewBaseActivity<T> extends LoadableListBaseActivity<T> {

  private ImageView previewImageView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    previewImageView = findViewById(R.id.preview_image);
  }

  protected void showPreview(@DrawableRes int previewDrawableId) {
    Glide.with(this).load(previewDrawableId).into(previewImageView);
  }
}
