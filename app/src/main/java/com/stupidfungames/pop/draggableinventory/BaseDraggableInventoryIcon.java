package com.stupidfungames.pop.draggableinventory;

import static org.andengine.input.touch.TouchEvent.ACTION_CANCEL;
import static org.andengine.input.touch.TouchEvent.ACTION_DOWN;
import static org.andengine.input.touch.TouchEvent.ACTION_MOVE;
import static org.andengine.input.touch.TouchEvent.ACTION_OUTSIDE;
import static org.andengine.input.touch.TouchEvent.ACTION_UP;

import com.stupidfungames.pop.GameSceneTouchListenerEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.icons.BaseInventoryIconEntity;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.utils.Mutex;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

/**
 * Used for an icon that has an inventory of items and those items can be dragged from the icon onto
 * the screen (and vice versa).
 *
 * To be used in conjunction with {@link } and {@link BaseDraggableEntity}
 *
 * Requires a {@link Mutex} and {@link BaseDraggableEntityCreator} in its parent path
 */
public abstract class BaseDraggableInventoryIcon extends BaseInventoryIconEntity implements
    GameSceneTouchListenerEntity.SceneTouchListener {

  // True if we are currently spawning
  private boolean isSpawning;

  public BaseDraggableInventoryIcon(BinderEnity parent) {
    super(parent);
  }

  @Override
  public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
    boolean handled = false;
    switch (touchEvent.getAction()) {
      case ACTION_DOWN:
        handled = maybeStartUndocking(touchEvent);
        break;
      case ACTION_UP:
        finishedSpawning();
        handled = true;
        break;
      case ACTION_CANCEL:
      case ACTION_OUTSIDE:
      case ACTION_MOVE:
        // NOOP
        break;

    }
    return handled;
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    super.onEvent(event, payload);
    if (event == getDockedEvent()) {
      onEntityDocked();
    }
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    EventBus.get().subscribe(getDockedEvent(), this);
    get(GameSceneTouchListenerEntity.class).addSceneTouchListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get().unSubscribe(getDockedEvent(), this);
    get(GameSceneTouchListenerEntity.class).removeSceneTouchListener(this);
  }

  /**
   * If the user has pressed down on the icon and they are not already dragging an entity then we
   * need to load a new entity. While this new entity is being undocked we cannot
   * undock another entity.
   *
   * @Return true if we started spawning
   */
  private boolean maybeStartUndocking(TouchEvent touchEvent) {
    if (canUndockEntity(touchEvent)) {
      // play the sound for undocking an entity
      get(GameSoundsManager.class).getSound(getDockUnDockSoundEffect()).play();
      // Create a entity and set it to be dragging
      BaseDraggableEntity draggableEntity = get(BaseDraggableEntityCreator.class)
          .createDraggableEntity(touchEvent.getX(), touchEvent.getY());
      draggableEntity.forceStartDragging(touchEvent.getX(), touchEvent.getY());

      decreaseInventory();
      isSpawning = true;
      return true;
    }
    return false;
  }

  private void onEntityDocked() {
    get(GameSoundsManager.class).getSound(getDockUnDockSoundEffect()).play();
    increaseInventory();
  }

  /**
   * Returns true if it is permissible to undock a new entity
   */
  private boolean canUndockEntity(TouchEvent touchEvent) {
    return !isSpawning && !get(Mutex.class).isLocked() && getIconSprite()
        .contains(touchEvent.getX(), touchEvent.getY()) && hasInventory();
  }

  private void finishedSpawning() {
    isSpawning = false;
  }

  /**
   * called to get the name of event used to denote docking of this entity
   */
  protected abstract GameEvent getDockedEvent();

  protected abstract SoundId getDockUnDockSoundEffect();

  @Override
  protected IOnAreaTouchListener getTouchListener() {
    /** NOOP **/
    return null;
  }

}
