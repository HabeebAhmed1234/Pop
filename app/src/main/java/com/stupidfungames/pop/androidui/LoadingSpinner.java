package com.stupidfungames.pop.androidui;

import static android.view.animation.Animation.INFINITE;
import static android.view.animation.Animation.RESTART;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.stupidfungames.pop.R;

public class LoadingSpinner extends androidx.appcompat.widget.AppCompatImageView {

  public LoadingSpinner(Context context) {
    super(context);
    init();
  }

  public LoadingSpinner(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public LoadingSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.circular_spinner));
  }

  public void startLoadingAnimation() {
    setVisibility(View.VISIBLE);

    RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    rotate.setDuration(700);
    rotate.setInterpolator(new LinearInterpolator());
    rotate.setRepeatCount(INFINITE);
    rotate.setRepeatMode(RESTART);

    startAnimation(rotate);
  }


  public void stopLoadingAnimation() {
    setVisibility(View.GONE);
    clearAnimation();
  }
}
