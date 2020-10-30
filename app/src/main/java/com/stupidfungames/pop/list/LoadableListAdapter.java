package com.stupidfungames.pop.list;

import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class LoadableListAdapter<T> extends ListAdapter<T, BindableViewHolder> {

  private final BindableViewHolderFactory bindableViewHolderFactory;
  private final OnClickListener onClickListener;

  public LoadableListAdapter(DiffUtil.ItemCallback<T> diffUtilCallback,
      BindableViewHolderFactory bindableViewHolderFactory,
      OnClickListener onClickListener) {
    super(diffUtilCallback);
    this.bindableViewHolderFactory = bindableViewHolderFactory;
    this.onClickListener = onClickListener;
  }

  @NonNull
  @Override
  public BindableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ViewGroup productListItemView = (ViewGroup) LayoutInflater.from(
        parent.getContext()).inflate(bindableViewHolderFactory.getLayoutId(), parent, false);
    productListItemView.setOnClickListener(onClickListener);
    return bindableViewHolderFactory.create(productListItemView);
  }

  @Override
  public void onBindViewHolder(@NonNull BindableViewHolder holder, int position) {
    holder.bind(getItem(position));
  }

  @Override
  public void onViewRecycled(@NonNull BindableViewHolder holder) {
    holder.unBind();
  }

  public T getItemAtPosition(int position) {
    return getItem(position);
  }
}
