package com.stupidfungames.pop.list;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Triggers the loading of some data and provides callbacks for setting the corresponding view
 * state
 *
 * @param <T> The type of the return value from loading
 */
public class LoadableListLoadingCoordinator<T> {

  public interface ViewCoordinator<T> {

    void renderLoadedState(T loadedResult);

    void renderEmptyState();

    void renderErrorState();

    void renderErrorState(Throwable throwable);
  }

  /**
   * Callback for loading the data.
   */
  public interface LoaderCallback<T> {

    ListenableFuture<T> loadData();

    boolean isEmptyResult(T result);
  }

  private final LoaderCallback<T> loaderCallback;
  private final ViewCoordinator<T> viewCoordinator;

  public LoadableListLoadingCoordinator(LoaderCallback<T> loaderCallback, ViewCoordinator<T> viewCoordinator) {
    this.loaderCallback = loaderCallback;
    this.viewCoordinator = viewCoordinator;
  }

  public void start(Context context) {
    Futures.addCallback(loaderCallback.loadData(), new FutureCallback<T>() {

      @Override
      public void onSuccess(@NullableDecl T result) {
        if (result != null && !loaderCallback.isEmptyResult(result)) {
          viewCoordinator.renderLoadedState(result);
        } else if (loaderCallback.isEmptyResult(result)) {
          viewCoordinator.renderEmptyState();
        } else {
          viewCoordinator.renderErrorState();
        }
      }

      @Override
      public void onFailure(Throwable t) {
        viewCoordinator.renderErrorState(t);
      }
    }, ContextCompat.getMainExecutor(context));
  }
}
