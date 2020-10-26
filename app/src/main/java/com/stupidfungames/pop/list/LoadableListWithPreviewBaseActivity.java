package com.stupidfungames.pop.list;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.util.Preconditions;
import com.bumptech.glide.Glide;
import com.stupidfungames.pop.R;

public abstract class LoadableListWithPreviewBaseActivity<T> extends
    LoadableListBaseActivity<T> {

  @Nullable
  private ImageView background;
  private ImageView previewImageView;
  private View previewImageFrame;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    background = findViewById(R.id.background);
    previewImageView = Preconditions.checkNotNull((ImageView) findViewById(R.id.preview_image),
        "Must use loadable_list_with_preview_view as layout");
    previewImageFrame = Preconditions.checkNotNull(findViewById(R.id.preview_image_frame),
        "Must use loadable_list_with_preview_view as layout");
  }

  @Override
  protected void onClick(T item) {
    int resId = getPreviewResId(item);
    if (resId > 0) {
      showPreview(resId);
      if (background != null) {
        // Set this as the current background of the activity if background view is there
        Glide.with(this).load(resId).into(background);
      }
    } else {
      hidePreview();
      if (background != null) {
        Glide.with(this).load(R.drawable.main_menu_background).into(background);
      }
    }
  }

  protected abstract @DrawableRes
  int getPreviewResId(T item);

  private void showPreview(@DrawableRes int previewDrawableId) {
    previewImageFrame.setVisibility(VISIBLE);
    Glide.with(this).load(previewDrawableId).into(previewImageView);
  }

  private void hidePreview() {
    Glide.with(this).clear(previewImageView);
    previewImageFrame.setVisibility(GONE);
  }
}
