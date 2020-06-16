package com.stupidfungames.pop.inapppurchase;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.android.billingclient.api.Purchase;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.inapppurchase.ProductsInfoMap.GameProduct;
import com.stupidfungames.pop.list.BindableViewHolder;

public class PurchaseViewHolder extends BindableViewHolder<Purchase> {

  private TextView name;
  private TextView description;

  public PurchaseViewHolder(@NonNull View v) {
    super(v);
    name = v.findViewById(R.id.name);
    description = v.findViewById(R.id.description);
  }

  @Override
  public void bind(Purchase model) {
    GameProduct product =  ProductsInfoMap.skuToProductsMap.get(model.getSku());
    name.setText(product.name);
    description.setText(product.description);
  }
}
