package com.stupidfungames.pop.bubbletimeout;

import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_FAST;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_IMMINENT;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_SLOWLY;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.EXPLODING;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.IDLE;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.STABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.BUBBLE_POPPED;

import com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State;
import com.stupidfungames.pop.eventbus.BubblePoppedEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventBus.Subscriber;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.statemachine.BaseStateMachine.Listener;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;

/**
 * Responsible for transitioning the bubble's state machine through its lifecycle
 */
class BubbleLifecycleTransitionDriver implements BubbleLifecycleController, Listener<State>,
    Subscriber {

  private final Engine engine;
  private final BubbleLifeCycleStateMachine stateMachine;
  private final Sprite bubbleSprite;

  private IUpdateHandler currentStateTransition;

  BubbleLifecycleTransitionDriver(Sprite bubbleSprite, Engine engine,
      BubbleLifeCycleStateMachine stateMachine) {
    this.bubbleSprite = bubbleSprite;
    this.engine = engine;
    this.stateMachine = stateMachine;
    addListeners();
    if (bubbleSprite.isAttached() && stateMachine.getCurrentState() != STABLE) {
      stateMachine.transitionState(STABLE);
    }
  }

  @Override
  public void onDestroy() {
    removeListeners();
  }

  private void addListeners() {
    stateMachine.addAllStateTransitionListener(this);
    EventBus.get().subscribe(BUBBLE_POPPED, this);
  }

  private void removeListeners() {
    removeCurrentUpdateHandler();
    stateMachine.removeAllStateTransitionListener(this);
    EventBus.get().unSubscribe(BUBBLE_POPPED, this);
  }

  @Override
  public void onEnterState(BubbleLifeCycleStateMachine.State newState) {
    removeCurrentUpdateHandler();

    BubbleLifeCycleStateMachine.State nextState = null;
    switch (newState) {
      case STABLE:
        nextState = BLINKING_SLOWLY;
        break;
      case BLINKING_SLOWLY:
        nextState = BLINKING_FAST;
        break;
      case BLINKING_FAST:
        nextState = BLINKING_IMMINENT;
        break;
      case BLINKING_IMMINENT:
        nextState = EXPLODING;
        break;
      case IDLE:
      case EXPLODING:
        nextState = null;
        break;
    }
    if (nextState != null) {
      float[] bubbleCenter = bubbleSprite.getCenter();
      if (nextState == BLINKING_SLOWLY && !ScreenUtils.getScreenRect()
          .contains(bubbleCenter[0], bubbleCenter[1])) {
        // Only transition to BLINKING_SLOWLY if the bubble is on the screen.
        currentStateTransition = new TimerHandler(STABLE.duration, new NextStateDriver(STABLE));
      } else if (nextState == EXPLODING && bubbleSprite.getY() > ScreenUtils
          .getSreenSize().heightPx) {
        // If the bubble has already passed the bottom of the screen it shouldn't explode
        // Should just disappear
        return;
      } else {
        currentStateTransition = new TimerHandler(newState.duration,
            new NextStateDriver(nextState));
      }
      engine.registerUpdateHandler(currentStateTransition);
    }
  }

  private void removeCurrentUpdateHandler() {
    if (currentStateTransition != null && engine.containsUpdateHandler(currentStateTransition)) {
      engine.unregisterUpdateHandler(currentStateTransition);
    }
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    if (event == BUBBLE_POPPED && bubbleSprite != null) {
      BubbleEntityUserData userData = (BubbleEntityUserData) bubbleSprite.getUserData();
      if (userData != null && ((BubblePoppedEventPayload) payload).bubbleId == userData.getId()
          && stateMachine.getCurrentState() != IDLE) {
        stateMachine.transitionState(IDLE);
      }
    }
  }

  private class NextStateDriver implements ITimerCallback {

    private BubbleLifeCycleStateMachine.State nextState;

    NextStateDriver(BubbleLifeCycleStateMachine.State nextState) {
      this.nextState = nextState;
    }

    @Override
    public void onTimePassed(TimerHandler pTimerHandler) {
      stateMachine.transitionState(nextState);
    }
  }
}
