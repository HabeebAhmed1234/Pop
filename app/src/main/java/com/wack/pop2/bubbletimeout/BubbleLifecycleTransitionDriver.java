package com.wack.pop2.bubbletimeout;

import android.util.Log;

import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_FAST;
import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_IMMINENT;
import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_SLOWLY;
import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.EXPLODING;

/**
 * Responsible for transitioning the bubble's state machine through its lifecycle
 */
class BubbleLifecycleTransitionDriver implements BubbleLifecycleController, BaseStateMachine.Listener<BubbleLifeCycleStateMachine.State> {

    private final Engine engine;
    private final BubbleLifeCycleStateMachine stateMachine;

    private IUpdateHandler currentStateTransition;

    BubbleLifecycleTransitionDriver(Engine engine, BubbleLifeCycleStateMachine stateMachine) {
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
            Log.d("asdasd", "CurrentState =  " + stateMachine.getCurrentState());
            currentStateTransition = new TimerHandler(newState.duration, new NextStateDriver(nextState));
            Log.d("asdasd", "scheduling next state =  " + nextState);
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
            Log.d("asdasd", "tranistioning to state =  " + nextState);
            stateMachine.transitionState(nextState);
        }
    }
}
