package com.stupidfungames.pop.wallsv2;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableEntity;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

public class WallDraggableEntity extends BaseDraggableEntity {

  private static final float WALL_DRAGGING_OFFSET = 30;
  public static final float WALL_DRAGGING_SCALE_MULTIPLIER = 1.0f;
  private final Sprite wallSprite;

  public WallDraggableEntity(Sprite wallSprite, BinderEnity parent) {
    super(wallSprite, parent);
    this.wallSprite = wallSprite;
  }

  @Override
  protected float getDraggingOffsetPx() {
    return WALL_DRAGGING_OFFSET;
  }

  @Override
  protected float getDragginScaleMultiplier() {
    return WALL_DRAGGING_SCALE_MULTIPLIER;
  }

  @Override
  protected IconId getHomeIconId() {
    return IconId.WALLS_V2_ICON;
  }

  @Override
  public boolean canDrag() {
    return true;
  }

  @Override
  public void onDraggingStarted() {

  }

  @Override
  public void onDropped() {

  }

  @Override
  public void onDocked() {
    wallSprite.setColor(AndengineColor.TRANSPARENT);
    removePhysics(wallSprite);
  }

  @Override
  public GameEvent getDockedEvent() {
    return GameEvent.WALL_DOCKED;
  }
}
