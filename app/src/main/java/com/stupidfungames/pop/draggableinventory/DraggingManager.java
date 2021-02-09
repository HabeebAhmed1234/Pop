package com.stupidfungames.pop.draggableinventory;

import android.util.Log;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameSceneTouchListenerEntity;
import com.stupidfungames.pop.LongPressGesture;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.utils.GeometryUtils;
import com.stupidfungames.pop.utils.Mutex;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

/**
 * Responsible for dragging a {@link BaseDraggableEntity} around
 */
class DraggingManager extends BaseEntity implements
    GameSceneTouchListenerEntity.SceneTouchListener, LongPressGesture.LongPressCallback {

  /**
   * Callbacks for the entity that is being dragged
   */
  public interface DraggableEntityCallback {

    boolean canDrag();

    /**
     * Called when the entity has started tracking the users finger
     */
    void onDraggingStarted();

    /**
     * Called when the entity has been placed on the screen
     */
    void onDropped();

    /**
     * Called when the entity has been docked on its home icon
     */
    void onDocked();

    /**
     * Event to fire when an entity is docked
     */
    GameEvent getDockedEvent();
  }

  private final Sprite draggableSprite;
  private final float draggingOffsetPx;
  private final float draggingScaleMultiplier;
  private final IconId homeIconId;
  private final DraggableEntityCallback draggableEntityCallback;
  private final LongPressGesture longPressGesture;

  private boolean isDragging = false;

  DraggingManager(
      BinderEnity parent,
      Sprite draggableSprite,
      float draggingOffsetPx,
      float draggingScaleMultiplier,
      IconId homeIconId,
      DraggableEntityCallback draggableEntityCallback) {
    super(parent);
    this.draggableSprite = draggableSprite;
    this.draggingOffsetPx = draggingOffsetPx;
    this.draggingScaleMultiplier = draggingScaleMultiplier;
    this.homeIconId = homeIconId;
    this.draggableEntityCallback = draggableEntityCallback;
    this.longPressGesture = new LongPressGesture(draggableSprite, this, this);
    init();
  }

  @Override
  public void onCreateScene() {
    init();
  }

  private void init() {
    GameSceneTouchListenerEntity touchListenerEntity = get(GameSceneTouchListenerEntity.class);
    if (!touchListenerEntity.hasSceneTouchListener(this)) {
      touchListenerEntity.addSceneTouchListener(this);
    }
    if (!touchListenerEntity.hasSceneTouchListener(longPressGesture)) {
      touchListenerEntity.addSceneTouchListener(longPressGesture);
    }
  }

  @Override
  public void onDestroy() {
    GameSceneTouchListenerEntity touchListenerEntity = get(GameSceneTouchListenerEntity.class);
    touchListenerEntity.removeSceneTouchListener(this);
    touchListenerEntity.removeSceneTouchListener(longPressGesture);
  }

  private void setDragging(boolean isDragging) {
    this.isDragging = isDragging;
    Mutex mutex = get(Mutex.class);
    if (isDragging) {
      mutex.setIsLocked(true);
      draggableEntityCallback.onDraggingStarted();
    } else {
      mutex.setIsLocked(false);
    }
    draggableSprite.setScale(isDragging ? draggingScaleMultiplier : 1);
  }

  @Override
  public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
    if (isDragging) {
      // If we are dragging then set the position of the entity to the touch event
      trackEntityToPointerOnDrag(touchEvent.getX(), touchEvent.getY());

      if (!maybeDockEntity(touchEvent) && touchEvent.getAction() == TouchEvent.ACTION_UP) {
        dropEntity(touchEvent);
      }
      return true;
    }
    return false;
  }

  /**
   * Force the entity to load without the prerequisite long press.
   */
  public void forceStartDragging(float pointerX, float pointerY) {
    setDragging(true);
    trackEntityToPointerOnDrag(pointerX, pointerY);
  }

  @Override
  public void onLongPress(float touchX, float touchY) {
    if (draggableEntityCallback.canDrag() && !get(Mutex.class).isLocked()) {
      pickUpDraggableEntity(touchX, touchY);
    } else {
      Log.e("DraggingManager", "Long press registered while we are dragging the entity!");
    }
  }

  private void pickUpDraggableEntity(float touchX, float touchY) {
    get(GameSoundsManager.class).getSound(SoundId.CLICK_UP).play();
    setDragging(true);
    trackEntityToPointerOnDrag(touchX, touchY);
  }

  private void dropEntity(TouchEvent touchEvent) {
    get(GameSoundsManager.class).getSound(SoundId.CLICK_DOWN).play();
    setDragging(false);
    setDraggableSpritePositionCenter(touchEvent.getX(), touchEvent.getY());
    draggableEntityCallback.onDropped();
  }

  private void trackEntityToPointerOnDrag(float x, float y) {
    setDraggableSpritePositionCenter(x - draggingOffsetPx, y - draggingOffsetPx);
  }

  private void setDraggableSpritePositionCenter(float x, float y) {
    if (draggableSprite == null) {
      return;
    }
    float newX = x - draggableSprite.getWidthScaled() / 2;
    float newY = y - draggableSprite.getHeightScaled() / 2;
    draggableSprite.setX(GeometryUtils.constrainXToScreenWidth(newX, draggableSprite.getWidth()));
    draggableSprite
        .setY(GeometryUtils.constrainYToScreenHeight(newY, draggableSprite.getHeight()));
  }

  /**
   * Possibly docks the entity to its home icon.
   *
   * @return true if docked
   */
  private boolean maybeDockEntity(TouchEvent touchEvent) {
    // If the user is dragging over the dock icon and releases the entity then we must dock it back
    // Into the icon
    if (touchEvent.isActionUp() && isTouchOverHomeIcon(touchEvent)) {
      setDragging(false);
      draggableEntityCallback.onDocked();
      EventBus.get().sendEvent(draggableEntityCallback.getDockedEvent());
      return true;
    }
    return false;
  }

  private boolean isTouchOverHomeIcon(TouchEvent touchEvent) {
    return get(GameIconsHostTrayEntity.class).getIcon(homeIconId)
        .contains(touchEvent.getX(), touchEvent.getY());
  }
}
