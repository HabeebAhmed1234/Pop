package com.stupidfungames.pop.androidui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import java.util.HashSet;
import java.util.Set;

public class BlinkAnimator {

  private static final float[] BLINK_ANIMATION_1 =
      new float[]{1, 0.5f, 1, 1, 1, 1, 1, 1, 1, 1, 0.7f, 1};

  Set<ValueAnimator> animators = new HashSet<>();


  public void animate(final View view) {
    ValueAnimator animator = ObjectAnimator.ofFloat((BLINK_ANIMATION_1));
    animator.setDuration(2000);
    animator.start();
    animator.setRepeatMode(ValueAnimator.RESTART);

    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        view.setAlpha((float) animation.getAnimatedValue());
      }
    });
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        animation.start();
      }
    });
    animators.add(animator);
  }

  public void onDestroy() {
    for (ValueAnimator animator : animators) {
      animator.end();
    }
    animators.clear();
  }
}
