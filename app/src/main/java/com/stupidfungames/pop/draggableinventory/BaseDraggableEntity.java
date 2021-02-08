package com.stupidfungames.pop.draggableinventory;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.DraggingManager.DraggableEntityCallback;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;

public abstract class BaseDraggableEntity extends BaseEntity implements DraggableEntityCallback {


  public BaseDraggableEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(DraggingManager.class,
        new DraggingManager(this, getDraggingOffsetPx(), getHomeIconId(), this));
  }

  public void forceStartDragging(float pointerX, float pointerY) {
    get(DraggingManager.class).forceStartDragging(pointerX, pointerY);
  }

  public void onForceDropped() {
    onDropped();
  }


  protected abstract float getDraggingOffsetPx();

  protected abstract IconId getHomeIconId();
}
