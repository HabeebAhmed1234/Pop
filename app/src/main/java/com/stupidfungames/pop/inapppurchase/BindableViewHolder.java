package com.stupidfungames.pop.inapppurchase;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

/**
 * ViewHolder that is bindable.
 * @param <T> model to be passed in to bind
 */
public abstract class BindableViewHolder<T> extends ViewHolder {

  public BindableViewHolder(@NonNull View itemView) {
    super(itemView);
  }

  public abstract void bind(T model);
}
