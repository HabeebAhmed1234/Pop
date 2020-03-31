package com.wack.pop2.bubbletimeout;

import com.wack.pop2.statemachine.BaseStateMachine;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;

import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_FAST;
import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_IMMINENT;
import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_SLOWLY;
import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.EXPLODING;
import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.STABLE;

/**
 * Responsible for transitioning the bubble's state machine through its lifecycle
 */
class BubbleLifecycleTransitionDriver implements BubbleLifecycleController, BaseStateMachine.Listener<BubbleLifeCycleStateMachine.State> {

    private final Engine engine;
    private final BubbleLifeCycleStateMachine stateMachine;
    private final Sprite bubbleSprite;

    private IUpdateHandler currentStateTransition;

    BubbleLifecycleTransitionDriver(Sprite bubbleSprite, Engine engine, BubbleLifeCycleStateMachine stateMachine) {
        this.bubbleSprite = bubbleSprite;
        this.engine = engine;
        this.stateMachine = stateMachine;
        addListeners();
    }

    @Override
    public void onDestroy() {
        removeListeners();
    }

    private void addListeners() {
        stateMachine.addAllStateTransitionListener(this);
    }

    private void removeListeners() {
        removeCurrentUpdateHandler();
        stateMachine.removeAllStateTransitionListener(this);
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
            case EXPLODING:
                nextState = null;
                break;
        }
        if (nextState != null) {
            float[] bubbleCenter = bubbleSprite.getCenter();
            if (nextState == BLINKING_SLOWLY && !ScreenUtils.getScreenRect().contains(bubbleCenter[0], bubbleCenter[1])) {
                // Only transition to BLINKING_SLOWLY if the bubble is on the screen.
                currentStateTransition = new TimerHandler(STABLE.duration, new NextStateDriver(STABLE));
            } else if (nextState == EXPLODING && bubbleSprite.getY() > ScreenUtils.getSreenSize().heightPx) {
                // If the bubble has already passed the bottom of the screen it shouldn't explode
                // Should just disappear
                return;
            } else {
                currentStateTransition = new TimerHandler(newState.duration, new NextStateDriver(nextState));
            }
            engine.registerUpdateHandler(currentStateTransition);
        }
    }

    private void removeCurrentUpdateHandler() {
        if (currentStateTransition != null && engine.containsUpdateHandler(currentStateTransition)) {
            engine.unregisterUpdateHandler(currentStateTransition);
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
