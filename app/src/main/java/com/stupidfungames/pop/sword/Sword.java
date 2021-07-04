package com.stupidfungames.pop.sword;

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
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

public class Sword extends BaseEntity implements SceneTouchListener, Listener<State> {

  private BubblesOnLineEntityMatcher bubblesOnLineEntityMatcher = null;
  private float prevX = -1;
  private float prevY = -1;

  public Sword(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
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
    if (get(SwordStateMachine.class).getCurrentState() == State.CHARGED
        //&& !get(Mutex.class).isLocked()
        && (action == ACTION_DOWN || action == ACTION_MOVE)) {
      // If the sword is charged and we aren't already dragging something then we can use the sword.
      for (IEntity bubbleToPop : scene.query(getBublesOnPointEntityMatcher(touchEvent.getX(), touchEvent.getY()))) {
        if (get(SwordStateMachine.class).getCurrentState() == State.CHARGED) {
          get(BubblePopperEntity.class).popBubble((Sprite) bubbleToPop);
          get(SwordChargeManager.class).decrementCharge();
        }
      }
    }
    prevX = touchEvent.getX();
    prevY = touchEvent.getX();
    return false;
  }

  private IEntityMatcher getBublesOnPointEntityMatcher(float pX, float pY) {
    if (bubblesOnLineEntityMatcher == null) {
      bubblesOnLineEntityMatcher = new BubblesOnLineEntityMatcher(pX, pY);
    } else {
      bubblesOnLineEntityMatcher.setPoint(pX, pY);
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
