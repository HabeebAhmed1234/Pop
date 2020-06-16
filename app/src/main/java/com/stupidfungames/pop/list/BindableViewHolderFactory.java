package com.stupidfungames.pop.list;

import android.view.ViewGroup;
import androidx.annotation.LayoutRes;

public interface BindableViewHolderFactory {

  @LayoutRes int getLayoutId();
  BindableViewHolder create(ViewGroup parentView);
}
