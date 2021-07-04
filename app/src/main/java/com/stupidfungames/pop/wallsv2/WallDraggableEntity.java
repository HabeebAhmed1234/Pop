package com.stupidfungames.pop.wallsv2;

import static com.stupidfungames.pop.eventbus.GameEvent.CHARGE_WALLS_LVL_1;
import static com.stupidfungames.pop.eventbus.GameEvent.CHARGE_WALLS_LVL_2;
import static com.stupidfungames.pop.eventbus.GameEvent.WALL_V2_COLLIDED_WITH_BUBBLE;
import static com.stupidfungames.pop.wallsv2.WallV2StateMachine.State.DROPPED_CHARGED;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableEntity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableInventoryIcon;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventBus.Subscriber;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.WallV2CollidedWithBubbleEventPayload;
import com.stupidfungames.pop.fixturedefdata.BaseEntityUserData;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.statemachine.BaseStateMachine.Listener;
import com.stupidfungames.pop.wallsv2.WallV2StateMachine.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

public class WallDraggableEntity extends BaseDraggableEntity implements Subscriber,
    Listener<State> {

  private static final float WALL_DRAGGING_OFFSET = 30;
  private static final float WALL_DRAGGING_SCALE_MULTIPLIER = 1.0f;
  private static final int WALL_CHARGE_PER_LVL = 7;
  private static final int WALL_RECHARGE_DELAY_SECONDS = 10;

  private final WallV2StateMachine stateMachine;
  private final Sprite wallHealthSprite;
  // Walls start off with no charge since they don't pop bubbles until upgraded.
  private int wallCharge = -1;
  private TimerHandler wallChargeTimerHandler = null;

  public WallDraggableEntity(Sprite wallSprite, BinderEnity parent) {
    super(wallSprite, parent);
    wallHealthSprite = (Sprite) wallSprite.getChildByIndex(0);
    this.stateMachine = new WallV2StateMachine();

    EventBus.get()
        .subscribe(WALL_V2_COLLIDED_WITH_BUBBLE, this)
        .subscribe(CHARGE_WALLS_LVL_1, this)
        .subscribe(CHARGE_WALLS_LVL_2, this);

    stateMachine.addAllStateTransitionListener(this);

    fullyChargeWalls();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get()
        .unSubscribe(WALL_V2_COLLIDED_WITH_BUBBLE, this)
        .unSubscribe(CHARGE_WALLS_LVL_1, this)
        .unSubscribe(CHARGE_WALLS_LVL_2, this);
  }

  @Override
  protected float getDraggingOffsetPx() {
    return WALL_DRAGGING_OFFSET;
  }

  @Override
  protected float getDraggingScaleMultiplier() {
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
    stateMachine.transitionState(State.DRAGGING);
  }

  @Override
  public void onDropped() {
    stateMachine.transitionState(wallCharge <= 0 ? State.DROPPED_DISCHARGED : DROPPED_CHARGED);
  }

  @Override
  public void onDocked() {
    stateMachine.transitionState(State.DOCKED);
    removePhysics(draggableSprite);
  }

  @Override
  public GameEvent getDockedEvent() {
    return GameEvent.WALL_DOCKED;
  }

  @Override
  public void onSaveGame(SaveGame saveGame) {
    super.onSaveGame(saveGame);
    if (stateMachine.getCurrentState() == State.DOCKED) {
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

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    switch (event) {
      case WALL_V2_COLLIDED_WITH_BUBBLE:
        onBubbleHitWall((WallV2CollidedWithBubbleEventPayload) payload);
        break;
      case CHARGE_WALLS_LVL_1:
        fullyChargeWalls();
        break;
      case CHARGE_WALLS_LVL_2:
        fullyChargeWalls();
        break;
    }
    if (event == WALL_V2_COLLIDED_WITH_BUBBLE) {
    }
  }

  private int getWallChargeLevel() {
    return ((WallsV2InventoryIcon) get(BaseDraggableInventoryIcon.class)).getWallsLevel();
  }

  private void fullyChargeWalls() {
    if (isRechargePending()) {
      wallChargeTimerHandler.pause();
    }
    wallCharge = getMaxWallCharge();
    onWallChargeChanged();
  }

  private int getMaxWallCharge() {
    return getWallChargeLevel() * WALL_CHARGE_PER_LVL;
  }

  private void onBubbleHitWall(WallV2CollidedWithBubbleEventPayload payload) {
    if (stateMachine.getCurrentState() == DROPPED_CHARGED
        && ((BaseEntityUserData) draggableSprite.getUserData()).getId() == payload.wallId) {
      get(BubblePopperEntity.class).popBubble(payload.bubbleSprite);
      wallCharge -= 1;
      onWallChargeChanged();
      payload.bubbleSprite = null;
    }
  }

  private void onWallChargeChanged() {
    if (wallCharge > 0 && stateMachine.getCurrentState() == State.DROPPED_DISCHARGED
        && stateMachine.getCurrentState() != DROPPED_CHARGED) {
      stateMachine.transitionState(DROPPED_CHARGED);
    } else if (wallCharge <= 0 && stateMachine.getCurrentState() == DROPPED_CHARGED
        && stateMachine.getCurrentState() != State.DROPPED_DISCHARGED) {
      stateMachine.transitionState(State.DROPPED_DISCHARGED);
    }
    updateWallHealthSprite();
  }

  private void updateWallHealthSprite() {
    int maxWallCharge = getMaxWallCharge();
    if (maxWallCharge == 0 && wallHealthSprite.isVisible()) {
      wallHealthSprite.setVisible(false);
    } else if (maxWallCharge > 0) {
      wallHealthSprite.setVisible(true);
      float percentWallCharged = (float) wallCharge / (float) getMaxWallCharge();
      wallHealthSprite.setScaleX(percentWallCharged);
    }
  }

  @Override
  public void onEnterState(State newState) {
    updateColor(newState);
    maybeChargeWall(newState);
  }

  private void updateColor(State newState) {
    AndengineColor newColor = AndengineColor.WHITE;
    switch (newState) {
      case DOCKED:
        newColor = AndengineColor.TRANSPARENT;
        break;
      case DRAGGING:
        newColor = AndengineColor.YELLOW;
        break;
      case DROPPED_CHARGED:
        newColor = AndengineColor.GREEN;
        break;
      case DROPPED_DISCHARGED:
        newColor = AndengineColor.RED;
        break;
    }
    draggableSprite.setColor(newColor);
    wallHealthSprite.setColor(newColor);
  }

  private boolean isRechargePending() {
    return wallChargeTimerHandler != null && !wallChargeTimerHandler.isPaused();
  }

  private void maybeChargeWall(State newState) {
    if (newState == State.DROPPED_DISCHARGED && (wallChargeTimerHandler == null
        || !isRechargePending())) {
      if (wallChargeTimerHandler == null) {
        wallChargeTimerHandler = new TimerHandler(WALL_RECHARGE_DELAY_SECONDS,
            new ITimerCallback() {
              @Override
              public void onTimePassed(TimerHandler pTimerHandler) {
                fullyChargeWalls();
              }
            });
        engine.registerUpdateHandler(wallChargeTimerHandler);
      } else {
        wallChargeTimerHandler.reset();
      }
    }
  }
}
