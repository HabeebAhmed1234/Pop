package com.stupidfungames.pop.wallsv2;

import static com.stupidfungames.pop.eventbus.GameEvent.WALL_V2_POPPED_BUBBLE;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableEntity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventBus.Subscriber;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.WallV2PoppedBubbleEventPayload;
import com.stupidfungames.pop.fixturedefdata.BaseEntityUserData;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.statemachine.BaseStateMachine.Listener;
import com.stupidfungames.pop.wallsv2.WallV2StateMachine.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

public class WallDraggableEntity extends BaseDraggableEntity implements Subscriber,
    Listener<State> {

  private static final float WALL_DRAGGING_OFFSET = 30;
  public static final float WALL_DRAGGING_SCALE_MULTIPLIER = 1.0f;
  private final Sprite wallSprite;
  private final WallV2StateMachine stateMachine;

  // Walls start off with no charge since they don't pop bubbles until upgraded.
  private int wallCharge = -1;

  public WallDraggableEntity(Sprite wallSprite, BinderEnity parent) {
    super(wallSprite, parent);
    this.wallSprite = wallSprite;
    this.stateMachine = new WallV2StateMachine();

    stateMachine.addAllStateTransitionListener(this);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    EventBus.get().subscribe(WALL_V2_POPPED_BUBBLE, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get().unSubscribe(WALL_V2_POPPED_BUBBLE, this);
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
    stateMachine.transitionState(State.DRAGGING);
  }

  @Override
  public void onDropped() {
    stateMachine
        .transitionState(wallCharge <= 0 ? State.DROPPED_DISCHARGED : State.DROPPED_CHARGED);
  }

  @Override
  public void onDocked() {
    stateMachine.transitionState(State.DOCKED);
    removePhysics(wallSprite);
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
    if (event == WALL_V2_POPPED_BUBBLE) {
      WallV2PoppedBubbleEventPayload wallV2PoppedBubbleEventPayload = (WallV2PoppedBubbleEventPayload) payload;
      if (((BaseEntityUserData) draggableSprite.getUserData()).getId()
          == wallV2PoppedBubbleEventPayload.wallId) {
        onWallPoppedBubble();
      }
    }
  }

  private void onWallPoppedBubble() {
    wallCharge -= 1;
    onWallChargeChanged();
  }

  private void onWallChargeChanged() {
    if (wallCharge > 0 && stateMachine.getCurrentState() == State.DROPPED_DISCHARGED
        && stateMachine.getCurrentState() != State.DROPPED_CHARGED) {
      stateMachine.transitionState(State.DROPPED_CHARGED);
    } else if (stateMachine.getCurrentState() == State.DROPPED_CHARGED
        && stateMachine.getCurrentState() != State.DROPPED_DISCHARGED) {
      stateMachine.transitionState(State.DROPPED_DISCHARGED);
    }
  }

  @Override
  public void onEnterState(State newState) {
    updateColor(newState);
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
  }
}
