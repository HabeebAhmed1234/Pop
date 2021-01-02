package com.stupidfungames.pop.tooltips;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.androidui.GlideUtils;

public class FullPageTooltipActivity extends AppCompatActivity {

  public static final String EXTRA_TOOLTIP_ID = "tooltip_id";

  private final TooltipTexts tooltipTexts = new TooltipTexts();
  private final TooltipImages tooltipImages = new TooltipImages();
  private final TooltipImageTints tooltipImageTints = new TooltipImageTints();

  public static Intent newIntent(TooltipId tooltipId, Context context) {
    Intent intent = new Intent(context, FullPageTooltipActivity.class);
    intent.putExtra(EXTRA_TOOLTIP_ID, tooltipId);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tooltip_with_image_layout);
    TooltipId tooltipId = (TooltipId) getIntent().getExtras().get(EXTRA_TOOLTIP_ID);
    ((TextView) findViewById(R.id.tooltip_text))
        .setText(tooltipTexts.getTooltipText(tooltipId));
    @Nullable String imageFilePath = tooltipImages.getTooltipImageAssetFilePath(tooltipId);
    if (!TextUtils.isEmpty(imageFilePath)) {
      ImageView imageView = findViewById(R.id.tooltip_image);
      GlideUtils
          .loadWithImageAssetFileName(imageView, imageFilePath);
      imageView.setVisibility(View.VISIBLE);
      @ColorRes int colorRes = tooltipImageTints.getTooltipImageTint(tooltipId);
      if (colorRes != -1) {
        imageView.setColorFilter(ContextCompat.getColor(this, colorRes),
            android.graphics.PorterDuff.Mode.MULTIPLY);
      }
    }
    findViewById(R.id.ok_btn).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }
}
