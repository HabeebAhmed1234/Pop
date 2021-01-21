package com.stupidfungames.pop.bubbletimeout;

import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.ABOUT_TO_EXPLODE;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.EXPLODING;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.IDLE;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.STABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.BUBBLE_RECYCLED;
import static com.stupidfungames.pop.utils.ScreenUtils.PERCENT_SPRITE_IN_SCREEN;

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
        nextState = ABOUT_TO_EXPLODE;
        break;
      case ABOUT_TO_EXPLODE:
        nextState = EXPLODING;
        break;
      case IDLE:
      case EXPLODING:
        nextState = null;
        break;
    }
    if (nextState != null) {
      currentStateTransition = new TimerHandler(newState.duration,
          new NextStateDriver(nextState));
      engine.registerUpdateHandler(currentStateTransition);
    }
  }

  private void removeCurrentUpdateHandler() {
    if (currentStateTransition != null && engine.containsUpdateHandler(currentStateTransition)) {
      engine.unregisterUpdateHandler(currentStateTransition);
      currentStateTransition = null;
    }
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    if (event == BUBBLE_RECYCLED && bubbleSprite != null) {
      BubbleEntityUserData userData = (BubbleEntityUserData) bubbleSprite.getUserData();
      BubbleRecycledEventPayload recycledEventPayload = (BubbleRecycledEventPayload) payload;
      if (userData != null && recycledEventPayload.bubbleId == userData.getId()) {
        removeCurrentUpdateHandler();
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
      boolean isBubbleInScreen = ScreenUtils.isInScreen(bubbleSprite, PERCENT_SPRITE_IN_SCREEN);
      if (nextState == EXPLODING && !isBubbleInScreen) {
        // If the bubble has already passed the bottom of the screen it shouldn't explode
        // Should just disappear
        return;
      } else if ((nextState == ABOUT_TO_EXPLODE || nextState == EXPLODING) && !isBubbleInScreen) {
        // If bubble if not on the screen and we want to transition to some end bubble state then
        // make it stable again since its not fair to the player if its off screen and about to explode
        stateMachine.transitionState(STABLE);
      } else if (stateMachine.isValidTransition(nextState)) {
        stateMachine.transitionState(nextState);
      }
    }
  }
}
