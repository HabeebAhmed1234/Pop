package com.stupidfungames.pop.inapppurchase;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.stupidfungames.pop.R;

/**
 * This activity displays a list of all products available in the app. If a product is purchased
 * it saves that product item to the "economy" save game in the save game manager.
 */
public class StoreActivity extends AppCompatActivity {

  private RecyclerView productsRecyclerView;
  private Adapter productsAdapter;

  private GooglePlayServicesBillingManager billingManager = new GooglePlayServicesBillingManager();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.store_activity_layout);

    productsRecyclerView = findViewById(R.id.products_recyclerview);
    productsRecyclerView.setHasFixedSize(true);
    productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    productsAdapter = new ProductsAdapter();
    productsRecyclerView.setAdapter(productsAdapter);

    startLoadingProducts();
  }

  private void startLoadingProducts() {
    //billingManager.
  }
}
