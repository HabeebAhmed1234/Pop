package com.stupidfungames.pop.list;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.stupidfungames.pop.androidui.GlideUtils.loadWithImageAssetFileName;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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

  private ImageView expandedImageView;
  private View expandedImageViewFrame;

  @Nullable
  private String currentPreviewImageFileName = null;
  @DrawableRes
  private int currentPreviewImageDrawableRes = -1;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    background = findViewById(R.id.background);

    previewImageView = Preconditions.checkNotNull((ImageView) findViewById(R.id.preview_image),
        "Must use loadable_list_with_preview_view as layout");
    previewImageFrame = Preconditions.checkNotNull(findViewById(R.id.preview_image_frame),
        "Must use loadable_list_with_preview_view as layout");

    expandedImageView = Preconditions
        .checkNotNull((ImageView) findViewById(R.id.expanded_preview_image),
            "Must use loadable_list_with_preview_view as layout");
    expandedImageViewFrame = Preconditions
        .checkNotNull(findViewById(R.id.expanded_preview_image_frame),
            "Must use loadable_list_with_preview_view as layout");

    previewImageFrame.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        showExpandedPreview();
      }
    });
  }

  @Override
  protected void onClick(T item) {
    @Nullable String previewFileName = getPreviewImageFileName(item);
    @DrawableRes int drawableRes = getPreviewImageDrawableResId(item);
    if (!TextUtils.isEmpty(previewFileName)) {
      showPreview(previewFileName);
    } else if (drawableRes != -1) {
      showPreview(drawableRes);
    } else {
      hidePreview();
    }

    if (background != null) {
      String backgroundFileName = getPreviewBackgroundImageFileName(item);
      if (!TextUtils.isEmpty(backgroundFileName)) {
        // Set this as the current background of the activity if background view is there
        loadWithImageAssetFileName(background, previewFileName);
      } else {
        Glide.with(this).load(R.drawable.main_menu_background).into(background);
      }
    }
  }

  /**
   * Return the file name for the image to be used in the preview window of this activity.
   */
  protected abstract String getPreviewImageFileName(T item);

  /**
   * Return the drawable for the image to be used in the preview window of this activity. if no file
   * path was provided via getPreviewImageFileName.
   */
  protected @DrawableRes
  int getPreviewImageDrawableResId(T item) {
    return -1;
  }

  /**
   * Return the file name for the image to be used in the background of this activity.
   */
  protected abstract String getPreviewBackgroundImageFileName(T item);

  private void showPreview(String imageFileName) {
    currentPreviewImageFileName = imageFileName;
    currentPreviewImageDrawableRes = -1;
    previewImageFrame.setVisibility(VISIBLE);
    loadWithImageAssetFileName(previewImageView, currentPreviewImageFileName);
  }

  private void showPreview(@DrawableRes int imageDrawableRes) {
    currentPreviewImageDrawableRes = imageDrawableRes;
    currentPreviewImageFileName = null;
    previewImageFrame.setVisibility(VISIBLE);
    Glide.with(this).load(currentPreviewImageDrawableRes).into(previewImageView);
  }

  private void hidePreview() {
    currentPreviewImageFileName = null;
    currentPreviewImageDrawableRes = -1;
    Glide.with(this).clear(previewImageView);
    previewImageFrame.setVisibility(GONE);
  }

  private void showExpandedPreview() {
    if (!TextUtils.isEmpty(currentPreviewImageFileName)) {
      expandedImageViewFrame.setVisibility(VISIBLE);
      loadWithImageAssetFileName(expandedImageView, currentPreviewImageFileName);
    } else if (currentPreviewImageDrawableRes != -1) {
      expandedImageViewFrame.setVisibility(VISIBLE);
      Glide.with(this).load(currentPreviewImageDrawableRes).into(expandedImageView);
    }
  }


  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && expandedImageViewFrame.getVisibility() == VISIBLE) {
      Glide.with(this).clear(expandedImageView);
      expandedImageViewFrame.setVisibility(GONE);
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }
}
