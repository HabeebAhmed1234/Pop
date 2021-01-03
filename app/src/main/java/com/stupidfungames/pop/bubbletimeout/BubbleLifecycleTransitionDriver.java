package com.stupidfungames.pop.bubbletimeout;

import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_FAST;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_IMMINENT;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_SLOWLY;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.EXPLODING;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.IDLE;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.STABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.BUBBLE_RECYCLED;

import com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State;
import com.stupidfungames.pop.eventbus.BubbleRecycledEventPayload;
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
  public void onLifeycleControllersDestroy() {
    removeListeners();
  }

  private void addListeners() {
    stateMachine.addAllStateTransitionListener(this);
    EventBus.get().subscribe(BUBBLE_RECYCLED, this);
  }

  private void removeListeners() {
    removeCurrentUpdateHandler();
    stateMachine.removeAllStateTransitionListener(this);
    EventBus.get().unSubscribe(BUBBLE_RECYCLED, this);
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
      boolean isBubbleInScreen = ScreenUtils.getScreenRect()
          .contains(bubbleSprite.getCenterX(), bubbleSprite.getCenterY());

      if (nextState == EXPLODING && bubbleSprite.getCenterY() > ScreenUtils
          .getSreenSize().heightPx) {
        // If the bubble has already passed the bottom of the screen it shouldn't explode
        // Should just disappear
        return;
      } else if ((nextState == BLINKING_SLOWLY
          || nextState == BLINKING_FAST
          || nextState == BLINKING_IMMINENT
          || nextState == EXPLODING) && !isBubbleInScreen) {
        // If bubble if not on the screen and we want to transition to some end bubble state then
        // make it stable again since its not fair to the player if its off screen and about to explode
        currentStateTransition = new TimerHandler(STABLE.duration, new NextStateDriver(STABLE));
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
    if (event == BUBBLE_RECYCLED && bubbleSprite != null) {
      BubbleEntityUserData userData = (BubbleEntityUserData) bubbleSprite.getUserData();
      BubbleRecycledEventPayload recycledEventPayload = (BubbleRecycledEventPayload) payload;
      if (userData != null && recycledEventPayload.bubbleId == userData.getId()) {
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
