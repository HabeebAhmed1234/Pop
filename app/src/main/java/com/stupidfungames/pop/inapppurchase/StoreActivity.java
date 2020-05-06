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
import com.android.billingclient.api.SkuDetails;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.androidui.LoadingSpinner;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * This activity displays a list of all products available in the app. If a product is purchased
 * it saves that product item to the "economy" save game in the save game manager.
 */
public class StoreActivity extends AppCompatActivity implements HostActivity, LoginListener {

  public static Intent getIntent(Context context) {
    return new Intent(context, StoreActivity.class);
  }

  private LoadingSpinner loadingSpinner;
  private View errorView;
  private TextView errorText;
  private RecyclerView productsRecyclerView;
  private ProductsAdapter productsAdapter;

  private GooglePlayServicesAuthManager authManager;
  private GooglePlayServicesBillingManager billingManager;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.store_activity_layout);

    productsRecyclerView = findViewById(R.id.products_recyclerview);
    loadingSpinner = findViewById(R.id.loading_spinner);
    errorView = findViewById(R.id.error_view);
    errorText = findViewById(R.id.error_text);

    productsRecyclerView.setHasFixedSize(true);
    productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    productsAdapter = new ProductsAdapter();
    productsRecyclerView.setAdapter(productsAdapter);

    authManager = new GooglePlayServicesAuthManager(this);
    billingManager = new GooglePlayServicesBillingManager(this);

    if (!authManager.isLoggedIn()) {
      renderLoadingState();
      authManager.initiateLogin(this, this);
    } else {
      startLoadingProducts();
    }
  }

  private void renderLoadingState() {
    errorView.setVisibility(View.GONE);
    productsRecyclerView.setVisibility(View.GONE);

    loadingSpinner.startLoadingAnimation();
  }

  private void renderLoadedState(List<SkuDetails> loadedProducts) {
    loadingSpinner.stopLoadingAnimation();
    errorView.setVisibility(View.GONE);

    productsAdapter.setProducts(loadedProducts);
  }

  private void renderErrorState(@StringRes int errorResId) {
    renderErrorState(getString(errorResId));
  }

  private void renderErrorState(@Nullable String errorMessage) {
    loadingSpinner.stopLoadingAnimation();
    productsRecyclerView.setVisibility(View.GONE);

    errorView.setVisibility(View.VISIBLE);
    if (errorMessage != null) {
      errorText.setText(errorMessage);
    }
  }

  private void startLoadingProducts() {
    Futures.addCallback(billingManager.getProducts(this), new FutureCallback<List<SkuDetails>>() {

      @Override
      public void onSuccess(@NullableDecl List<SkuDetails> result) {
        if (result != null && !result.isEmpty()) {
          renderLoadedState(result);
        } else if (result.isEmpty()) {
          renderErrorState(R.string.no_products_result);
        } else {
          renderErrorState(R.string.error_fetching_products);
        }
      }

      @Override
      public void onFailure(Throwable t) {
        renderErrorState(R.string.error_fetching_products);
      }
    }, ContextCompat.getMainExecutor(this));
  }

  @Override
  public GooglePlayServicesAuthManager getAuthManager() {
    return authManager;
  }

  @Override
  public void onLoginStart() {
    renderLoadingState();
  }

  @Override
  public void onLoggedIn(GoogleSignInAccount account) {
    startLoadingProducts();
  }

  @Override
  public void onLoggedOut() {
    renderErrorState(R.string.must_be_logged_in);
  }

  @Override
  public void onLoginCanceled() {
    renderErrorState(R.string.must_be_logged_in);
  }

  @Override
  public void onLoginFailed(Exception e) {
    renderErrorState(null);
  }
}
