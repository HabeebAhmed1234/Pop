package com.stupidfungames.pop.list;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.SkuDetails;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.list.LoadableListLoadingCoordinator.LoaderCallback;
import java.util.List;

/**
 * Activity that can load a list of type {@link T} models into a {@link BindableViewHolder} and
 * render them on the screen. Requires the layout to contain a R.layout.loadable_list_view.
 */
public abstract class LoadableListBaseActivity<T> extends AppCompatActivity implements
    HostActivity {

  private final OnClickListener onClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      LoadableListBaseActivity.this
          .onClick((T) loadableListAdapter.getItemAtPosition(recyclerView.getChildLayoutPosition(v)));
    }
  };

  private GooglePlayServicesAuthManager authManager;

  private RecyclerView recyclerView;
  private LoadableListAdapter loadableListAdapter;
  private LoadableListViewCoordinator<SkuDetails> loadableListViewCoordinator;
  private LoadableListLoadingCoordinator<List<SkuDetails>> loadingCoordinator;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    recyclerView = findViewById(R.id.recyclerview);
    recyclerView.setHasFixedSize(true);
    loadableListAdapter = new LoadableListAdapter(getDiffutilCallback(),
        getViewHolderFactory(), onClickListener);
    loadableListViewCoordinator = new LoadableListViewCoordinator(
        loadableListAdapter,
        recyclerView,
        (ViewGroup) findViewById(R.id.loadable_list_view));
    loadingCoordinator = new LoadableListLoadingCoordinator(getLoaderCallback(),
        loadableListViewCoordinator);

    loadingCoordinator.start(this);
  }

  @LayoutRes
  protected abstract int getLayoutId();

  protected abstract BindableViewHolderFactory getViewHolderFactory();

  protected abstract ItemCallback getDiffutilCallback();

  protected abstract LoaderCallback<List<T>> getLoaderCallback();

  protected abstract void onClick(T item);

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public GooglePlayServicesAuthManager getAuthManager() {
    if (authManager == null) {
      authManager = new GooglePlayServicesAuthManager(this);
    }
    return authManager;
  }
}
