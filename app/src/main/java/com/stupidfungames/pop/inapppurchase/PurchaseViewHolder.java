package com.stupidfungames.pop.inapppurchase;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.billingclient.api.Purchase;
import com.bumptech.glide.Glide;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.inapppurchase.backgrounds.EquipBackgroundHelper;
import com.stupidfungames.pop.list.BindableViewHolder;

public class PurchaseViewHolder extends BindableViewHolder<Purchase> {

  private final OnClickListener onClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      if (SkuClassificationHelper.isBackground(sku)) {
        if (EquipBackgroundHelper.isBackgroundEquiped(itemView.getContext(), sku)) {
          // we must unequip the background
          EquipBackgroundHelper.unequip(itemView.getContext());
          Toast.makeText(itemView.getContext(), R.string.un_equip_success, Toast.LENGTH_LONG).show();
        } else {
          // we must equip the background
          if (!EquipBackgroundHelper.equipBackground(itemView.getContext(), sku)) {
            Toast.makeText(itemView.getContext(), R.string.error_equipping, Toast.LENGTH_LONG).show();
          } else {
            Toast.makeText(itemView.getContext(), R.string.equip_success, Toast.LENGTH_SHORT).show();;
          }
        }
        updateEquipBtn();
      }
    }
  };

  private ImageView image;
  private TextView name;
  private TextView description;
  private Button equipBtn;

  private String sku;

  public PurchaseViewHolder(@NonNull View v) {
    super(v);
    image = v.findViewById(R.id.product_image);
    name = v.findViewById(R.id.name);
    description = v.findViewById(R.id.description);
    equipBtn = v.findViewById(R.id.equip_btn);
    equipBtn.setOnClickListener(onClickListener);
  }

  @Override
  public void bind(Purchase model) {
    sku = model.getSku();
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

    updateEquipBtn();
  }

  private void updateEquipBtn() {
    if (SkuClassificationHelper.isEquippablePurchase(sku)) {
      equipBtn.setVisibility(View.VISIBLE);
      equipBtn.setText(EquipBackgroundHelper.isBackgroundEquiped(itemView.getContext(), sku) ? R.string.un_equip : R.string.equip);
    } else {
      equipBtn.setVisibility(View.GONE);
    }
  }
}
