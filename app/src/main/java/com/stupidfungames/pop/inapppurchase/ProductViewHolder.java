package com.stupidfungames.pop.inapppurchase;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.billingclient.api.SkuDetails;
import com.stupidfungames.pop.R;

public class ProductViewHolder extends BindableViewHolder<SkuDetails>{
  // each data item is just a string in this case

  private SkuDetails skuDetails;
  private GooglePlayServicesBillingManager billingManager;
  private Activity activity;

  private TextView name;
  private TextView price;
  private TextView description;

  private OnClickListener clickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      billingManager.purchase(activity, skuDetails);
    }
  };

  public ProductViewHolder(GooglePlayServicesBillingManager billingManager, Activity activity,
      ViewGroup v) {
    super(v);
    this.billingManager = billingManager;
    this.activity = activity;
    name = v.findViewById(R.id.name);
    price = v.findViewById(R.id.price);
    description = v.findViewById(R.id.description);

    v.setOnClickListener(clickListener);
  }

  @Override
  public void bind(SkuDetails skuDetails) {
    this.skuDetails = skuDetails;

    name.setText(skuDetails.getTitle());
    price.setText(skuDetails.getPrice());
    description.setText(skuDetails.getDescription());
  }
}