package com.stupidfungames.pop.inapppurchase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.billingclient.api.SkuDetails;
import com.google.common.util.concurrent.ListenableFuture;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.inapppurchase.LoadableListLoadingCoordinator.LoaderCallback;
import java.util.List;

/**
 * This activity displays a list of all products available in the app. If a product is purchased it
 * saves that product item to the "economy" save game in the save game manager.
 */
public class StoreActivity extends AppCompatActivity implements HostActivity {

  public static Intent getIntent(Context context) {
    return new Intent(context, StoreActivity.class);
  }

  private LoaderCallback<List<SkuDetails>> loaderCallback = new LoaderCallback<List<SkuDetails>>() {
    @Override
    public ListenableFuture<List<SkuDetails>> loadData() {
      return billingManager.getProducts(StoreActivity.this);
    }

    @Override
    public boolean isEmptyResult(List<SkuDetails> result) {
      return result.isEmpty();
    }
  };

  private BindableViewHolderFactory bindableViewHolderFactory = new BindableViewHolderFactory() {
    @Override
    public int getLayoutId() {
      return R.layout.product_list_item;
    }

    @Override
    public BindableViewHolder create(ViewGroup parentView) {
      return new ProductViewHolder(billingManager, StoreActivity.this, parentView);
    }
  };

  private LoadableListViewCoordinator<SkuDetails> loadableListViewCoordinator;
  private LoadableListLoadingCoordinator<List<SkuDetails>> loadingCoordinator;
  private LoadableListAdapter loadableListAdapter;

  private GooglePlayServicesAuthManager authManager;
  private GooglePlayServicesBillingManager billingManager;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.store_activity_layout);

    loadableListAdapter = new LoadableListAdapter(new SkuDetailsDiffUtilCallback(),
        bindableViewHolderFactory);
    loadableListViewCoordinator = new LoadableListViewCoordinator<>(loadableListAdapter,
        (ViewGroup) findViewById(R.id.loadable_list_view));
    loadingCoordinator = new LoadableListLoadingCoordinator<>(loaderCallback,
        loadableListViewCoordinator);

    authManager = new GooglePlayServicesAuthManager(this);
    billingManager = new GooglePlayServicesBillingManager(this);

    loadingCoordinator.start(this);
  }


  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public GooglePlayServicesAuthManager getAuthManager() {
    return authManager;
  }
}
