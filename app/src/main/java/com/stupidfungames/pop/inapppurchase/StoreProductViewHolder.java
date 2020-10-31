package com.stupidfungames.pop.inapppurchase;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.billingclient.api.SkuDetails;
import com.bumptech.glide.Glide;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.list.BindableViewHolder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoreProductViewHolder extends BindableViewHolder<SkuDetails> {

  private final GooglePlayServicesBillingManager billingManager;
  private final Activity activity;
  private final OnClickListener buyBtnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      billingManager.purchase(activity, skuDetails);
    }
  };

  private SkuDetails skuDetails;

  private ImageView imageView;
  private TextView name;
  private TextView price;
  private TextView description;
  private Button buyBtn;


  public StoreProductViewHolder(GooglePlayServicesBillingManager billingManager, Activity activity,
      ViewGroup v) {
    super(v);
    this.billingManager = billingManager;
    this.activity = activity;
    imageView = v.findViewById(R.id.product_image);
    name = v.findViewById(R.id.name);
    price = v.findViewById(R.id.price);
    price.setVisibility(View.VISIBLE);
    description = v.findViewById(R.id.description);
    buyBtn = v.findViewById(R.id.buy_btn);
    buyBtn.setOnClickListener(buyBtnClickListener);
  }

  @Override
  public void bind(SkuDetails skuDetails) {
    this.skuDetails = skuDetails;
    bindImage();
    name.setText(getTitle(skuDetails));
    price.setText(skuDetails.getPrice());
    description.setText(skuDetails.getDescription());
  }

  @Override
  public void unBind() {

  }

  private void bindImage() {
    String sku = skuDetails.getSku();
    if (ProductSKUManager.get().skuToProductsMap.containsKey(sku)) {
      GameProduct product = ProductSKUManager.get().skuToProductsMap.get(sku);
      if (product != null) {
        Glide.with(itemView).load(product.image).into(imageView);
      } else {
        Glide.with(itemView).load(R.drawable.ic_launcher).into(imageView);
      }
    }
  }

  private String getTitle(SkuDetails skuDetails) {
    String skuTitleAppNameRegex = "(?> \\(.+?\\))$";
    Pattern p = Pattern.compile(skuTitleAppNameRegex, Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(skuDetails.getTitle());
    return m.replaceAll("");
  }
}