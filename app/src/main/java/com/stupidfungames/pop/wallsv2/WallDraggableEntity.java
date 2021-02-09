package com.stupidfungames.pop.wallsv2;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableEntity;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.savegame.SaveGame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  @Override
  public void onSaveGame(SaveGame saveGame) {
    super.onSaveGame(saveGame);
    if (wallSprite.getColor().equals(AndengineColor.TRANSPARENT)) {
      return;
    }
    if (saveGame.wallV2Postitions == null) {
      saveGame.wallV2Postitions = new ArrayList<>();
    }
    List<Float> position = Arrays
        .asList(draggableSprite.getX() + draggableSprite.getWidthScaled() / 2,
            draggableSprite.getY() + draggableSprite.getHeightScaled() / 2);
    saveGame.wallV2Postitions.add(position);
  }
}
