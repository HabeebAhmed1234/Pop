package com.stupidfungames.pop.wallsv2;

import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableEntityCreator;
import com.stupidfungames.pop.draggableinventory.BaseDraggableInventoryIcon;
import com.stupidfungames.pop.draggableinventory.BaseDraggableManagerEntity;

public class WallsV2Manager extends BaseDraggableManagerEntity {

  public WallsV2Manager(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(WallsV2CollisionManager.class, new WallsV2CollisionManager(this));
  }

  @Override
  protected BaseDraggableEntityCreator getDraggableEntityCreator() {
    return new WallDraggableEntityCreator(this);
  }

  @Override
  protected BaseDraggableInventoryIcon getDraggableInventoryIcon() {
    return new WallsV2InventoryIcon(this);
  }
}
