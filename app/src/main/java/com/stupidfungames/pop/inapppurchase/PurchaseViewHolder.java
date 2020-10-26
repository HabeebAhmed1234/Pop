package com.stupidfungames.pop.inapppurchase;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.billingclient.api.Purchase;
import com.bumptech.glide.Glide;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.list.BindableViewHolder;

public class PurchaseViewHolder extends BindableViewHolder<Purchase> {

  private ImageView image;
  private TextView name;
  private TextView description;
  private Button equipBtn;

  public PurchaseViewHolder(@NonNull View v) {
    super(v);
    image = v.findViewById(R.id.product_image);
    name = v.findViewById(R.id.name);
    description = v.findViewById(R.id.description);
    equipBtn = v.findViewById(R.id.equip_btn);
  }

  @Override
  public void bind(Purchase model) {
    @Nullable GameProduct product = ProductSKUManager.get().skuToProductsMap
        .get(model.getSku());
    if (product != null) {
      Glide.with(itemView).load(product.image).into(image);
    } else {
      Glide.with(itemView).load(R.drawable.ic_launcher).into(image);
    }
    name.setText(
        product != null ? itemView.getContext().getString(product.name) : "Product Name Not Found");
    description.setText(
        product != null ? itemView.getContext().getString(product.description)
            : "Product Description Not Found");
  }
}
