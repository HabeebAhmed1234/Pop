package com.stupidfungames.pop.sword;

import static com.stupidfungames.pop.sword.SwordAnimationManager.MIN_LINE_SIZE_PX;
import static org.andengine.input.touch.TouchEvent.ACTION_DOWN;
import static org.andengine.input.touch.TouchEvent.ACTION_MOVE;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameSceneTouchListenerEntity;
import com.stupidfungames.pop.GameSceneTouchListenerEntity.SceneTouchListener;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.entitymatchers.BubblesOnLineEntityMatcher;
import com.stupidfungames.pop.statemachine.BaseStateMachine.Listener;
import com.stupidfungames.pop.sword.SwordStateMachine.State;
import com.stupidfungames.pop.utils.GeometryUtils;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

public class Sword extends BaseEntity implements SceneTouchListener, Listener<State> {

  private BubblesOnLineEntityMatcher bubblesOnLineEntityMatcher = null;
  private float prevX = -1;
  private float prevY = -1;

  private SwordAnimationManager swordAnimationManager;

  public Sword(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    this.swordAnimationManager = get(SwordAnimationManager.class);
    get(SwordStateMachine.class).addAllStateTransitionListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    get(GameSceneTouchListenerEntity.class).removeSceneTouchListener(this);
    get(SwordStateMachine.class).removeAllStateTransitionListener(this);
  }

  private void init() {
    GameSceneTouchListenerEntity touchListenerEntity = get(GameSceneTouchListenerEntity.class);
    if (!touchListenerEntity.hasSceneTouchListener(this)) {
      touchListenerEntity.addSceneTouchListener(this);
    }
  }

  @Override
  public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
    int action = touchEvent.getAction();
    if (action == ACTION_DOWN || action == ACTION_MOVE) {
      swordAnimationManager.onTouchEvent(touchEvent);
      if (prevX == -1 || prevY == -1) {
        prevX = touchEvent.getX();
        prevY = touchEvent.getY();
      }
      float touchX = touchEvent.getX();
      float touchY = touchEvent.getY();
      if (get(SwordStateMachine.class).getCurrentState() == State.CHARGED) {
        if (GeometryUtils.distanceBetween(prevX, prevY, touchX, touchY) > MIN_LINE_SIZE_PX) {
          // If the sword is charged and we are swiping then we can use the sword.
          for (IEntity bubbleToPop : scene.query(
              getBublesOnLineEntityMatcher(prevX, prevY, touchEvent.getX(), touchEvent.getY()))) {
            if (get(SwordStateMachine.class).getCurrentState() == State.CHARGED) {
              get(BubblePopperEntity.class).popBubble((Sprite) bubbleToPop);
              get(SwordChargeManager.class).decrementCharge();
            }
          }
        }
      }
      prevX = touchEvent.getX();
      prevY = touchEvent.getY();
      return true;
    } else {
      swordAnimationManager.clear();
      prevX = -1;
      prevY = -1;
      return false;
    }
  }

  private IEntityMatcher getBublesOnLineEntityMatcher(float x1, float y1, float x2, float y2) {
    if (bubblesOnLineEntityMatcher == null) {
      bubblesOnLineEntityMatcher = new BubblesOnLineEntityMatcher(x1, y1, x2, y2);
    } else {
      bubblesOnLineEntityMatcher.setLine(x1, y1, x2, y2);
    }
    return bubblesOnLineEntityMatcher;
  }

  @Override
  public void onEnterState(State newState) {
    if (newState != State.LOCKED) {
      init();
    }
  }
}
