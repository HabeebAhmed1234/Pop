package com.stupidfungames.pop.list;

import static com.stupidfungames.pop.analytics.Events.LOAD_LIST_EMPTY_RESULT;
import static com.stupidfungames.pop.analytics.Events.LOAD_LIST_ERROR;
import static com.stupidfungames.pop.analytics.Events.LOAD_LIST_START;
import static com.stupidfungames.pop.analytics.Events.LOAD_LIST_SUCCESS;

import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;
import com.stupidfungames.pop.auth.LoginListenerImpl;
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
  }

  /**
   * Callback for loading the data.
   */
  public interface LoaderCallback<T> {

    ListenableFuture<T> loadData();

    boolean isEmptyResult(T result);
  }

  private final HostActivity hostActivity;

  private final LoaderCallback<T> loaderCallback;
  private final ViewCoordinator<T> viewCoordinator;
  private final String listLogName;

  private final LoginListener loginListener = new LoginListenerImpl() {

    @Override
    public void onLoggedIn(GoogleSignInAccount account) {
      // Now that we are logged in retry loading.
      load();
    }
  };

  public LoadableListLoadingCoordinator(
      HostActivity hostActivity,
      LoaderCallback<T> loaderCallback,
      ViewCoordinator<T> viewCoordinator,
      String listLogName) {
    this.hostActivity = hostActivity;
    this.loaderCallback = loaderCallback;
    this.viewCoordinator = viewCoordinator;
    this.listLogName = listLogName;
  }

  public void load() {
    Logger.logSelect(hostActivity.getContext(), LOAD_LIST_START, listLogName);
    Futures.addCallback(loaderCallback.loadData(), new FutureCallback<T>() {

      @Override
      public void onSuccess(@NullableDecl T result) {
        if (result != null && !loaderCallback.isEmptyResult(result)) {
          viewCoordinator.renderLoadedState(result);
          Logger.logSelect(hostActivity.getContext(), LOAD_LIST_SUCCESS, listLogName);
          onLoadSuccess();
        } else if (loaderCallback.isEmptyResult(result)) {
          viewCoordinator.renderEmptyState();
          Logger.logSelect(hostActivity.getContext(), LOAD_LIST_EMPTY_RESULT, listLogName);
          onLoadSuccess();
        } else {
          viewCoordinator.renderErrorState();
          onLoadFailed();
        }
      }

      @Override
      public void onFailure(Throwable t) {
        viewCoordinator.renderErrorState();
        onLoadFailed();
      }
    }, ContextCompat.getMainExecutor(hostActivity.getContext()));
  }

  private void onLoadSuccess() {
    hostActivity.getAuthManager().removeListener(loginListener);
  }

  private void onLoadFailed() {
    Logger.logSelect(hostActivity.getContext(), LOAD_LIST_ERROR, listLogName);
    hostActivity.getAuthManager().addListener(loginListener);
  }
}
