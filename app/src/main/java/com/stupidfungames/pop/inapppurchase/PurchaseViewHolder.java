package com.stupidfungames.pop.inapppurchase;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.list.BindableViewHolder;

public class PurchaseViewHolder extends BindableViewHolder<PurchasePreviewableModel> {

  private TextView name;
  private TextView description;

  public PurchaseViewHolder(@NonNull View v) {
    super(v);
    name = v.findViewById(R.id.name);
    description = v.findViewById(R.id.description);
  }

  @Override
  public void bind(PurchasePreviewableModel model) {
    @Nullable GameProduct product = ProductSKUManager.get().skuToProductsMap
        .get(model.purchase.getSku());
    name.setText(product != null ? product.name : "Product Name Not Found");
    description.setText(product != null ? product.description : "Product Description Not Found");
  }
}
