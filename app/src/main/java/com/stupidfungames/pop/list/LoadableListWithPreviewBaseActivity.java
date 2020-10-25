package com.stupidfungames.pop.list;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.util.Preconditions;
import com.bumptech.glide.Glide;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.list.LoadableListWithPreviewBaseActivity.PreviewableModel;

public abstract class LoadableListWithPreviewBaseActivity<T extends PreviewableModel> extends
    LoadableListBaseActivity<T> {

  public interface PreviewableModel {

    @DrawableRes
    int getPreviewResId();
  }

  private ImageView previewImageView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    previewImageView = Preconditions.checkNotNull((ImageView) findViewById(R.id.preview_image),
        "Must use loadable_list_with_preview_view as layout");
  }

  @Override
  protected void onClick(T item) {
    int resId = item.getPreviewResId();
    if (resId > 0) {
      showPreview(resId);
    } else {
      hidePreview();
    }
  }

  private void showPreview(@DrawableRes int previewDrawableId) {
    previewImageView.setVisibility(VISIBLE);
    Glide.with(this).load(previewDrawableId).into(previewImageView);
  }

  private void hidePreview() {
    Glide.with(this).clear(previewImageView);
    previewImageView.setVisibility(GONE);
  }
}
