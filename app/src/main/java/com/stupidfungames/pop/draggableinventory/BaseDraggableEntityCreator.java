package com.stupidfungames.pop.draggableinventory;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;

public abstract class BaseDraggableEntityCreator extends BaseEntity {

  public BaseDraggableEntityCreator(BinderEnity parent) {
    super(parent);
  }

  public abstract BaseDraggableEntity createDraggableEntity(float centerX, float centerY);
}
