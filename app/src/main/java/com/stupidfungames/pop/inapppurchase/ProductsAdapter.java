package com.stupidfungames.pop.inapppurchase;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.android.billingclient.api.SkuDetails;
import com.stupidfungames.pop.R;
import java.util.ArrayList;
import java.util.List;

public class ProductsAdapter extends Adapter {

  private List<SkuDetails> products = new ArrayList<>();
  private final Activity activity;
  private final GooglePlayServicesBillingManager billingManager;

  public ProductsAdapter(Activity activity, GooglePlayServicesBillingManager billingManager) {
    this.activity = activity;
    this.billingManager = billingManager;
  }

  public void setProducts(List<SkuDetails> products) {
    this.products = products;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ViewGroup productListItemView = (ViewGroup) LayoutInflater.from(
        parent.getContext()).inflate(R.layout.product_list_item, parent, false);

    return new ProductViewHolder(productListItemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ((ProductViewHolder) holder).bind(products.get(position));
  }

  @Override
  public int getItemCount() {
    return products.size();
  }

  public class ProductViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case

    private SkuDetails skuDetails;

    private TextView name;
    private TextView price;
    private TextView description;

    private OnClickListener clickListener = new OnClickListener() {
      @Override
      public void onClick(View v) {
        billingManager.purchase(activity, skuDetails);
      }
    };

    public ProductViewHolder(ViewGroup v) {
      super(v);
      name = v.findViewById(R.id.name);
      price = v.findViewById(R.id.price);
      description = v.findViewById(R.id.description);

      v.setOnClickListener(clickListener);
    }

    public void bind(SkuDetails skuDetails) {
      this.skuDetails = skuDetails;

      name.setText(skuDetails.getTitle());
      price.setText(skuDetails.getPrice());
      description.setText(skuDetails.getDescription());
    }
  }
}
