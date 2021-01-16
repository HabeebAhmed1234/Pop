package com.stupidfungames.pop.turrets.turret;

import static com.stupidfungames.pop.eventbus.GameEvent.CANCEL_WALL_PLACEMENT;
import static com.stupidfungames.pop.eventbus.GameEvent.OPEN_GAME_ICONS_TRAY;
import static com.stupidfungames.pop.eventbus.GameEvent.TURRET_DOCKED;

import android.content.Context;
import android.util.Log;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameSceneTouchListenerEntity;
import com.stupidfungames.pop.LongPressGesture;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.statemachine.BaseStateMachine;
import com.stupidfungames.pop.turrets.TurretsMutex;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

/**
 * Responsible for dragging a turret around
 */
public class TurretDraggingManager extends BaseEntity implements
    GameSceneTouchListenerEntity.SceneTouchListener,
    BaseStateMachine.Listener<TurretStateMachine.State>, LongPressGesture.LongPressCallback {

  private static final float DRAGGING_OFFSET_DISTANCE_DP = 20;

  public TurretDraggingManager(BinderEnity parent) {
    super(parent);
    init();
    get(TurretStateMachine.class).addAllStateTransitionListener(this);
  }

  @Override
  protected void createBindings(Binder binder) {
    binder.bind(LongPressGesture.class, new LongPressGesture(this, this));
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
    LongPressGesture longPressGesture = get(LongPressGesture.class);
    if (!touchListenerEntity.hasSceneTouchListener(longPressGesture)) {
      touchListenerEntity.addSceneTouchListener(longPressGesture);
    }
  }

  @Override
  public void onDestroy() {
    GameSceneTouchListenerEntity touchListenerEntity = get(GameSceneTouchListenerEntity.class);
    touchListenerEntity.removeSceneTouchListener(this);
    touchListenerEntity.removeSceneTouchListener(get(LongPressGesture.class));
  }

  @Override
  public void onEnterState(TurretStateMachine.State newState) {
    TurretsMutex mutex = get(TurretsMutex.class);
    if (newState == TurretStateMachine.State.DRAGGING) {
      mutex.setIsDragging(true);
      EventBus.get().sendEvent(CANCEL_WALL_PLACEMENT);
    } else {
      mutex.setIsDragging(false);
    }
  }

  @Override
  public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
    if (get(TurretStateMachine.class).getCurrentState() == TurretStateMachine.State.DRAGGING) {
      // If we are dragging then set the position of the turret to the touch event
      trackTurretToPointerOnDrag(touchEvent.getX(), touchEvent.getY());

      if (!maybeDockTurret(touchEvent) && touchEvent.getAction() == TouchEvent.ACTION_UP) {
        dropTurret(touchEvent);
      }
      return true;
    }
    return false;
  }

  /**
   * Force the turret to load dragging without the prerequisite long press.
   */
  public void forceStartDragging(float pointerX, float pointerY) {
    get(TurretStateMachine.class).transitionState(TurretStateMachine.State.DRAGGING);
    trackTurretToPointerOnDrag(pointerX, pointerY);
  }

  @Override
  public void onLongPress(float touchX, float touchY) {
    if (get(TurretStateMachine.class).getCurrentState() != TurretStateMachine.State.DRAGGING
        && !get(TurretsMutex.class).isDragging()) {
      pickUpTurret(touchX, touchY);
    } else {
      Log.e("TurretDragginManager", "Long press registered while we are dragging the turret!");
    }
  }

  private void pickUpTurret(float touchX, float touchY) {
    get(GameSoundsManager.class).getSound(SoundId.CLICK_UP).play();
    get(TurretStateMachine.class).transitionState(TurretStateMachine.State.DRAGGING);
    trackTurretToPointerOnDrag(touchX, touchY);
    EventBus.get().sendEvent(OPEN_GAME_ICONS_TRAY);
  }

  private void dropTurret(TouchEvent touchEvent) {
    get(GameSoundsManager.class).getSound(SoundId.CLICK_DOWN).play();
    get(TurretStateMachine.class).transitionState(TurretStateMachine.State.TARGETING);
    get(HostTurretCallback.class).setTurretPositionCenter(touchEvent.getX(), touchEvent.getY());
  }

  private void trackTurretToPointerOnDrag(float x, float y) {
    int offsetPx = ScreenUtils.dpToPx(DRAGGING_OFFSET_DISTANCE_DP);
    get(HostTurretCallback.class).setTurretPositionCenter(x - offsetPx, y - offsetPx);
  }

  /**
   * Possibly docks the turret.
   *
   * @return true if docked
   */
  private boolean maybeDockTurret(TouchEvent touchEvent) {
    // If the user is dragging over the dock and releases the turret then we must dock it back
    // Into the icon
    if (touchEvent.isActionUp() && isTouchOverTurretsIcon(touchEvent)) {
      get(TurretStateMachine.class).transitionState(TurretStateMachine.State.DOCKED);
      EventBus.get().sendEvent(TURRET_DOCKED);
      return true;
    }
    return false;
  }

  private boolean isTouchOverTurretsIcon(TouchEvent touchEvent) {
    return get(GameIconsHostTrayEntity.class).getIcon(GameIconsHostTrayEntity.IconId.TURRETS_ICON)
        .contains(touchEvent.getX(), touchEvent.getY());
  }
}
