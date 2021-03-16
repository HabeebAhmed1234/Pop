package com.stupidfungames.pop;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Handler;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.stupidfungames.pop.globalpoppedbubbles.GlobalPoppedBubbleManager;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Adds the incremental bubbles popped this game to the global total in textviews.
 */
public class GameOverBubblesPoppedCounterView {

  private static final long TOTAL_ANIMATION_DURATION_MILLIS = 3000;
  private static final long TOTAL_INCREMENTS = 20;

  private static final long ANIMATION_INCREMENT_MILLIS =
      TOTAL_ANIMATION_DURATION_MILLIS / TOTAL_INCREMENTS;

  private final TextView globalTotalTextView;
  private final TextView incrementalBubblesTextView;
  private final TextView slash;

  private final Handler handler = new Handler();
  private final Runnable incrementRunnable = new Runnable() {
    @Override
    public void run() {
      if (onIncrement()) {
        handler.postDelayed(incrementRunnable, ANIMATION_INCREMENT_MILLIS);
      }
    }
  };

  private long globalTotalBubblesPopped;
  private long incrementalBubblesPopped;
  private long incrementAmount;

  public GameOverBubblesPoppedCounterView(
      TextView globalTotalTextView,
      TextView incrementalBubblesTextView,
      TextView slash) {
    this.globalTotalTextView = globalTotalTextView;
    this.incrementalBubblesTextView = incrementalBubblesTextView;
    this.slash = slash;
  }

  public void startAnimation(
      final long globalTotalBubblesPopped,
      final long incrementalBubblesPopped) {
    if (globalTotalBubblesPopped <= 0) {
      Futures.addCallback(
          GlobalPoppedBubbleManager.getInstance()
              .getTotalNumberOfGlobalBubblesPopped(globalTotalTextView.getContext()),
          new FutureCallback<Long>() {
            @Override
            public void onSuccess(@NullableDecl Long result) {
              if (result == null) {
                renderFallback(incrementalBubblesPopped);
              } else {
                startAnimationInternal(globalTotalBubblesPopped, incrementalBubblesPopped);
              }
            }

            @Override
            public void onFailure(Throwable t) {
              renderFallback(incrementalBubblesPopped);
            }
          }, ContextCompat.getMainExecutor(globalTotalTextView.getContext()));
    } else {
      startAnimationInternal(globalTotalBubblesPopped, incrementalBubblesPopped);
    }
  }

  private void startAnimationInternal(
      long globalTotalBubblesPopped,
      long incrementalBubblesPopped) {
    this.globalTotalBubblesPopped = globalTotalBubblesPopped;
    this.incrementalBubblesPopped = incrementalBubblesPopped;
    this.incrementAmount = incrementalBubblesPopped / TOTAL_INCREMENTS;
    if (incrementAmount <= 0) {
      incrementAmount = 1;
    }

    updateText();

    handler.postDelayed(incrementRunnable, ANIMATION_INCREMENT_MILLIS);
  }

  private void renderFallback(final long incrementalBubblesPopped) {
    globalTotalBubblesPopped = 0;
    this.incrementalBubblesPopped = incrementalBubblesPopped;
    updateText();
  }

  /**
   * Return true if we want to continue incrementing
   */
  private boolean onIncrement() {
    if (incrementalBubblesPopped <= 0) {
      return false;
    }
    if ((incrementalBubblesPopped - incrementAmount) < 0) {
      globalTotalBubblesPopped += incrementalBubblesPopped;
      incrementalBubblesPopped = 0;
    } else {
      incrementalBubblesPopped -= incrementAmount;
      globalTotalBubblesPopped += incrementAmount;
    }
    updateText();
    return incrementalBubblesPopped > 0;
  }

  private void updateText() {
    globalTotalTextView.setVisibility(globalTotalBubblesPopped == 0 ? GONE : VISIBLE);
    incrementalBubblesTextView.setVisibility(incrementalBubblesPopped == 0 ? GONE : VISIBLE);
    slash.setVisibility(
        globalTotalTextView.getVisibility() == GONE
            || incrementalBubblesTextView.getVisibility() == GONE ? GONE : VISIBLE);

    globalTotalTextView.setText(Long.toString(globalTotalBubblesPopped));
    incrementalBubblesTextView.setText(Long.toString(incrementalBubblesPopped));
  }

  public void onDestroy() {
    if (incrementRunnable != null) {
      handler.removeCallbacks(incrementRunnable);
    }
  }
}
