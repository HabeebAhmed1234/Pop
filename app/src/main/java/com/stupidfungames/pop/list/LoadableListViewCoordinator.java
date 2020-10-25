package com.stupidfungames.pop.list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.androidui.LoadingSpinner;
import com.stupidfungames.pop.list.LoadableListLoadingCoordinator.ViewCoordinator;
import java.util.List;

/**
 * Coordinates the view state when loading some data with {@link LoadableListLoadingCoordinator}. Requires that
 * loadable_list_view.xml is included in the passed in viewGroup or its children.
 */
public class LoadableListViewCoordinator<T> implements ViewCoordinator<List<T>> {

  private Context context;
  private LoadingSpinner loadingSpinner;
  private TextView errorText;
  private RecyclerView recyclerView;
  private ListAdapter adapter;

  public LoadableListViewCoordinator(ListAdapter<T, ?> adapter, RecyclerView recyclerView, ViewGroup viewGroup) {
    this.context = viewGroup.getContext();
    this.adapter = adapter;
    this.recyclerView = recyclerView;
    loadingSpinner = viewGroup.findViewById(R.id.loading_spinner);
    errorText = viewGroup.findViewById(R.id.error_text);

    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(viewGroup.getContext()));
    recyclerView.setAdapter(adapter);

    renderLoadingState();
  }

  @Override
  public void renderLoadedState(List<T> loadedResult) {
    loadingSpinner.stopLoadingAnimation();
    errorText.setVisibility(View.GONE);
    recyclerView.setVisibility(View.VISIBLE);

    adapter.submitList(loadedResult);
  }

  @Override
  public void renderEmptyState() {
    renderErrorState(context.getString(R.string.no_items_result));
  }

  @Override
  public void renderErrorState() {
    renderErrorState(context.getString(R.string.error_fetching_items));
  }

  @Override
  public void renderErrorState(Throwable throwable) {
    renderErrorState(throwable.getMessage());
  }

  private void renderLoadingState() {
    errorText.setVisibility(View.GONE);
    recyclerView.setVisibility(View.GONE);
    loadingSpinner.startLoadingAnimation();
  }

  private void renderErrorState(String errorMessage) {
    loadingSpinner.stopLoadingAnimation();
    recyclerView.setVisibility(View.GONE);

    errorText.setVisibility(View.VISIBLE);
    if (errorMessage != null) {
      errorText.setText(errorMessage);
    }
  }
}
