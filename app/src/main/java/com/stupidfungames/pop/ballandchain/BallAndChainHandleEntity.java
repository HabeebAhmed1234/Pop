package com.stupidfungames.pop.ballandchain;

import static com.stupidfungames.pop.eventbus.GameEvent.CANCEL_WALL_PLACEMENT;

import androidx.annotation.Nullable;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameSceneTouchListenerEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import com.stupidfungames.pop.utils.CoordinateConversionUtil;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.MouseJoint;

/**
 * Manages the handle of the ball and chain based off of the current state of the ball and chain
 * system
 */
class BallAndChainHandleEntity extends BaseEntity implements
    BallAndChainStateMachine.Listener<BallAndChainStateMachine.State>,
    GameSceneTouchListenerEntity.SceneTouchListener {

  public static final Vec2 OFF_SCREEN_HANDLE_POSITION =
      CoordinateConversionUtil
          .sceneToPhysicsWorld(Vec2Pool.obtain(ScreenUtils.getSreenSize().widthPx * 1.5f,
              ScreenUtils.getSreenSize().heightPx / 2));

  @Nullable
  private MouseJoint handleJoint;

  public BallAndChainHandleEntity(BinderEnity parent) {
    super(parent);
  }

  public void setHandleJoint(MouseJoint handleJoint) {
    if (this.handleJoint != null) {
      throw new IllegalStateException("Ball and chain handle has already been set");
    }

    this.handleJoint = handleJoint;
    // start listening to state when the handle is set
    get(BallAndChainStateMachine.class).addAllStateTransitionListener(this);
  }

  @Override
  public void onDestroy() {
    get(BallAndChainStateMachine.class).removeAllStateTransitionListener(this);
  }

  @Override
  public void onEnterState(BallAndChainStateMachine.State newState) {
    switch (newState) {
      case LOCKED:
      case UNLOCKED_CHARGED:
      case UNLOCKED_DISCHARGED:
        stopDragging();
        break;
      case IN_USE_CHARGED:
      case IN_USE_DISCHARGED:
        startDragging();
        break;
    }
  }

  private void startDragging() {
    get(GameSceneTouchListenerEntity.class).addSceneTouchListener(this);
    EventBus.get().sendEvent(CANCEL_WALL_PLACEMENT);
  }

  private void stopDragging() {
    // Snap the handle out of the screen and stop tracking touch dragging the handle
    setHandlePositionTarget(OFF_SCREEN_HANDLE_POSITION);
    get(GameSceneTouchListenerEntity.class).removeSceneTouchListener(this);
  }

  @Override
  public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
    switch (touchEvent.getAction()) {
      case TouchEvent.ACTION_UP:
        releaseHandle();
        return false;
      case TouchEvent.ACTION_DOWN:
      case TouchEvent.ACTION_MOVE:
        setHandlePositionTarget(touchEvent);
        return true;
    }
    return false;
  }

  private void releaseHandle() {
    BallAndChainStateMachine stateMachine = get(BallAndChainStateMachine.class);
    if (BallAndChainStateClassifier.isBallAndChainInUse(stateMachine.getCurrentState())) {
      stateMachine.transitionState(
          stateMachine.getCurrentState() == BallAndChainStateMachine.State.IN_USE_CHARGED
              ? BallAndChainStateMachine.State.UNLOCKED_CHARGED
              : BallAndChainStateMachine.State.UNLOCKED_DISCHARGED);
      stopDragging();
    }
  }

  private void setHandlePositionTarget(TouchEvent touchEvent) {
    if (BallAndChainStateClassifier
        .isChainHandleDraggingPermitted(get(BallAndChainStateMachine.class).getCurrentState())) {
      Vec2 position = CoordinateConversionUtil
          .sceneToPhysicsWorld(Vec2Pool.obtain(touchEvent.getX(), touchEvent.getY()));
      setHandlePositionTarget(position);
      Vec2Pool.recycle(position);
    }
  }

  private void setHandlePositionTarget(Vec2 position) {
    if (handleJoint == null) {
      return;
    }
    handleJoint.setTarget(position);
  }
}
