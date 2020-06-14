package com.stupidfungames.pop.inapppurchase;

import android.view.ViewGroup;
import androidx.annotation.LayoutRes;

public interface BindableViewHolderFactory {

  @LayoutRes int getLayoutId();
  BindableViewHolder create(ViewGroup parentView);
}
