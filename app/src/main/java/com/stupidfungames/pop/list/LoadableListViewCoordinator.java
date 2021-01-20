package com.stupidfungames.pop.list;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.androidui.LoadingSpinner;
import com.stupidfungames.pop.list.LoadableListLoadingCoordinator.ViewCoordinator;
import java.util.List;

/**
 * Coordinates the view state when loading some data with {@link LoadableListLoadingCoordinator}.
 * Requires that loadable_list_view.xml is included in the passed in viewGroup or its children.
 */
public class LoadableListViewCoordinator<T> implements ViewCoordinator<List<T>> {

  private Context context;
  private LoadingSpinner loadingSpinner;
  private RecyclerView recyclerView;
  private ListAdapter adapter;

  // Error state
  private TextView errorText;
  private View signInBtn;

  public LoadableListViewCoordinator(
      final HostActivity hostActivity,
      ListAdapter<T, ?> adapter,
      RecyclerView recyclerView,
      ViewGroup viewGroup) {
    this.context = viewGroup.getContext();
    this.adapter = adapter;
    this.recyclerView = recyclerView;
    loadingSpinner = viewGroup.findViewById(R.id.loading_spinner);
    errorText = viewGroup.findViewById(R.id.error_text);
    signInBtn = viewGroup.findViewById(R.id.sign_in_btn);

    signInBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        hostActivity.getAuthManager().initiateLogin(hostActivity);
      }
    });

    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(viewGroup.getContext()));
    recyclerView.setAdapter(adapter);

    renderLoadingState();
  }

  @Override
  public void renderLoadedState(List<T> loadedResult) {
    loadingSpinner.stopLoadingAnimation();
    errorText.setVisibility(GONE);
    signInBtn.setVisibility(GONE);
    recyclerView.setVisibility(VISIBLE);

    adapter.submitList(loadedResult);
  }

  @Override
  public void renderEmptyState() {
    renderErrorState(context.getString(R.string.no_items_result), false);
  }

  @Override
  public void renderErrorState() {
    renderErrorState(context.getString(R.string.error_fetching_items), true);
  }

  private void renderLoadingState() {
    errorText.setVisibility(GONE);
    signInBtn.setVisibility(GONE);
    recyclerView.setVisibility(GONE);
    loadingSpinner.startLoadingAnimation();
  }

  private void renderErrorState(String errorMessage, boolean showSignInBtn) {
    loadingSpinner.stopLoadingAnimation();
    recyclerView.setVisibility(GONE);

    errorText.setVisibility(VISIBLE);
    if (errorMessage != null) {
      errorText.setText(errorMessage);
    }

    signInBtn.setVisibility(showSignInBtn ? VISIBLE : GONE);
  }
}
