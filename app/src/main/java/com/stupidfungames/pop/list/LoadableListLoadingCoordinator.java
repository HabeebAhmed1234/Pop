package com.stupidfungames.pop.list;

import static com.stupidfungames.pop.analytics.Events.LOAD_LIST_EMPTY_RESULT;
import static com.stupidfungames.pop.analytics.Events.LOAD_LIST_ERROR;
import static com.stupidfungames.pop.analytics.Events.LOAD_LIST_START;
import static com.stupidfungames.pop.analytics.Events.LOAD_LIST_SUCCESS;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.stupidfungames.pop.analytics.Logger;
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
  private final String listLogName;

  public LoadableListLoadingCoordinator(LoaderCallback<T> loaderCallback, ViewCoordinator<T> viewCoordinator, String listLogName) {
    this.loaderCallback = loaderCallback;
    this.viewCoordinator = viewCoordinator;
    this.listLogName = listLogName;
  }

  public void start(final Context context) {
    Logger.logSelect(context, LOAD_LIST_START, listLogName);
    Futures.addCallback(loaderCallback.loadData(), new FutureCallback<T>() {

      @Override
      public void onSuccess(@NullableDecl T result) {
        if (result != null && !loaderCallback.isEmptyResult(result)) {
          viewCoordinator.renderLoadedState(result);
          Logger.logSelect(context, LOAD_LIST_SUCCESS, listLogName);
        } else if (loaderCallback.isEmptyResult(result)) {
          viewCoordinator.renderEmptyState();
          Logger.logSelect(context, LOAD_LIST_EMPTY_RESULT, listLogName);
        } else {
          viewCoordinator.renderErrorState();
          Logger.logSelect(context, LOAD_LIST_ERROR, listLogName);
        }
      }

      @Override
      public void onFailure(Throwable t) {
        viewCoordinator.renderErrorState(t);
        Logger.logSelect(context, LOAD_LIST_ERROR, listLogName);
      }
    }, ContextCompat.getMainExecutor(context));
  }
}
