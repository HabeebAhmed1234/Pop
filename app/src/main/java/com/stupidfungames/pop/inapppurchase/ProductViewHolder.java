package com.stupidfungames.pop.inapppurchase;

import android.view.ViewGroup;
import android.widget.TextView;
import com.android.billingclient.api.SkuDetails;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.list.BindableViewHolder;

public class ProductViewHolder extends BindableViewHolder<SkuDetails> {
  // each data item is just a string in this case

  private TextView name;
  private TextView price;
  private TextView description;


  public ProductViewHolder(ViewGroup v) {
    super(v);
    name = v.findViewById(R.id.name);
    price = v.findViewById(R.id.price);
    description = v.findViewById(R.id.description);

  }

  @Override
  public void bind(SkuDetails skuDetails) {
    name.setText(skuDetails.getTitle());
    price.setText(skuDetails.getPrice());
    description.setText(skuDetails.getDescription());
  }
}