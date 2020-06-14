package com.stupidfungames.pop.inapppurchase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.androidui.LoadingSpinner;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EquipActivity extends AppCompatActivity implements HostActivity {

  public static Intent getIntent(Context context) {
    return new Intent(context, EquipActivity.class);
  }

  private LoadingSpinner loadingSpinner;
  private TextView errorText;
  private RecyclerView productsRecyclerView;
  private LoadableListAdapter loadableListAdapter;

  private GooglePlayServicesAuthManager authManager;
  private GooglePlayServicesBillingManager billingManager;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.equip_activity_layout);

    productsRecyclerView = findViewById(R.id.equip_items_recyclerview);
    loadingSpinner = findViewById(R.id.loading_spinner);
    errorText = findViewById(R.id.error_text);

    authManager = new GooglePlayServicesAuthManager(this);
    billingManager = new GooglePlayServicesBillingManager(this);

    productsRecyclerView.setHasFixedSize(true);
    productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    loadableListAdapter = new LoadableListAdapter(this, billingManager);
    productsRecyclerView.setAdapter(loadableListAdapter);

    renderLoadingState();
    startLoadingEquippableItems();
  }

  private void renderLoadingState() {
    errorText.setVisibility(View.GONE);
    productsRecyclerView.setVisibility(View.GONE);

    loadingSpinner.startLoadingAnimation();
  }

  private void renderLoadedState(PurchasesResult purchasesResult) {
    loadingSpinner.stopLoadingAnimation();
    errorText.setVisibility(View.GONE);
    productsRecyclerView.setVisibility(View.VISIBLE);

    loadableListAdapter.setProducts(loadedProducts);
  }

  private void renderErrorState(@StringRes int errorResId) {
    renderErrorState(getString(errorResId));
  }

  private void renderErrorState(@Nullable String errorMessage) {
    loadingSpinner.stopLoadingAnimation();
    productsRecyclerView.setVisibility(View.GONE);

    errorText.setVisibility(View.VISIBLE);
    if (errorMessage != null) {
      errorText.setText(errorMessage);
    }
  }

  private void startLoadingEquippableItems() {
    Futures.addCallback(billingManager.queryPurchases(), new FutureCallback<PurchasesResult>() {

      @Override
      public void onSuccess(@NullableDecl PurchasesResult result) {
        if (result != null && result.getResponseCode() == BillingResponseCode.OK) {
          renderLoadedState(result);
        } else if (result != null && result.getPurchasesList().isEmpty()) {
          renderErrorState(R.string.no_purchases_result);
        } else {
          renderErrorState(R.string.error_fetching_items);
        }
      }

      @Override
      public void onFailure(Throwable t) {
        renderErrorState(R.string.error_fetching_items);
      }
    }, ContextCompat.getMainExecutor(this));
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
