package com.stupidfungames.pop.draggableinventory;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.DraggingManager.DraggableEntityCallback;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import org.andengine.entity.sprite.Sprite;

public abstract class BaseDraggableEntity extends BaseEntity implements DraggableEntityCallback {

  protected final Sprite draggableSprite;
  private final DraggingManager draggingManager;

  public BaseDraggableEntity(Sprite draggableSprite, BinderEnity parent) {
    super(parent);
    this.draggableSprite = draggableSprite;
    this.draggingManager = new DraggingManager(
        this,
        draggableSprite,
        getDraggingOffsetPx(),
        getDragginScaleMultiplier(),
        getHomeIconId(),
        this);
  }

  public void forceStartDragging(float pointerX, float pointerY) {
    draggingManager.forceStartDragging(pointerX, pointerY);
  }

  public void onForceDropped() {
    onDropped();
  }


  protected abstract float getDraggingOffsetPx();

  protected abstract float getDragginScaleMultiplier();

  protected abstract IconId getHomeIconId();
}
