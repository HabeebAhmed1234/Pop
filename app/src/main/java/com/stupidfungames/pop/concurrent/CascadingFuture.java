package com.stupidfungames.pop.concurrent;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class CascadingFuture<T> {

  private final Context context;
  private final ListenableFuture<T>[] futures;
  private final SettableFuture<T> finalResult = SettableFuture.create();

  public CascadingFuture(Context context, ListenableFuture<T>... futures) {
    this.context = context;
    this.futures = futures;

    if (futures.length > 0) {
      chainFutures(futures[0], 0);
    } else {
      finalResult.setException(new IllegalArgumentException("There are no futures to chain."));
    }
  }

  private void chainFutures(final ListenableFuture<T> currentFuture, final int currentFutureIndex) {
    final int nextFutureIndex = currentFutureIndex + 1;
    Futures.addCallback(currentFuture, new FutureCallback<T>() {
      @Override
      public void onSuccess(@NullableDecl T result) {
        if (result == null) {
          if (nextFutureIndex < futures.length) {
            chainFutures(futures[nextFutureIndex], nextFutureIndex);
          } else {
            finalResult.setException(
                new Exception("Null result from future number " + currentFutureIndex));
          }
        } else {
          finalResult.set(result);
        }
      }

      @Override
      public void onFailure(Throwable t) {
        if (nextFutureIndex < futures.length) {
          chainFutures(futures[nextFutureIndex], nextFutureIndex);
        } else {
          finalResult.setException(t);
        }
      }
    }, ContextCompat.getMainExecutor(context));
  }

  public ListenableFuture<T> getFinalResult() {
    return finalResult;
  }
}
