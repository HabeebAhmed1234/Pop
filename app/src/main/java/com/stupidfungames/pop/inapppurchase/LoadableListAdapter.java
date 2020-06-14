package com.stupidfungames.pop.inapppurchase;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class LoadableListAdapter<T> extends ListAdapter<T, BindableViewHolder> {

  private final BindableViewHolderFactory bindableViewHolderFactory;

  public LoadableListAdapter(DiffUtil.ItemCallback<T> diffUtilCallback,
      BindableViewHolderFactory bindableViewHolderFactory) {
    super(diffUtilCallback);
    this.bindableViewHolderFactory = bindableViewHolderFactory;
  }

  @NonNull
  @Override
  public BindableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ViewGroup productListItemView = (ViewGroup) LayoutInflater.from(
        parent.getContext()).inflate(bindableViewHolderFactory.getLayoutId(), parent, false);
    return bindableViewHolderFactory.create(productListItemView);
  }

  @Override
  public void onBindViewHolder(@NonNull BindableViewHolder holder, int position) {
    holder.bind(getItem(position));
  }
}
