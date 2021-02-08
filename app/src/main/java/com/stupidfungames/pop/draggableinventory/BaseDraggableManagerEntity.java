package com.stupidfungames.pop.draggableinventory;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.utils.Mutex;

public abstract class BaseDraggableManagerEntity extends BaseEntity {

  public BaseDraggableManagerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    binder.bind(Mutex.class, new Mutex())
        .bind(BaseDraggableEntityCreator.class, getDraggableEntityCreator())
        .bind(BaseDraggableInventoryIcon.class, getDraggableInventoryIcon());

  }

  protected abstract BaseDraggableEntityCreator getDraggableEntityCreator();

  protected abstract BaseDraggableInventoryIcon getDraggableInventoryIcon();
}
