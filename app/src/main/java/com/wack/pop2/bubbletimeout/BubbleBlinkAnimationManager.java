package com.wack.pop2.bubbletimeout;

import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.shape.IShape;

import java.util.HashMap;
import java.util.Map;

import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_FAST;
import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_IMMINENT;
import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_SLOWLY;
import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.EXPLODING;
import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.STABLE;

/**
 * Manages the transition of a bubble's animations through its lifecycle
 */
class BubbleBlinkAnimationManager implements BubbleLifecycleController, BaseStateMachine.Listener<BubbleLifeCycleStateMachine.State> {

    private static final float NUM_BLINKS_PER_STATE = 30;
    private static final float BLINK_SPAN_SECONDS = 0.2f;

    private IShape bubble;
    private BubbleLifeCycleStateMachine stateMachine;
    private IEntityModifier currentBlinkModifier;

    private Map<BubbleLifeCycleStateMachine.State, IEntityModifier> stateToModifiersMap = new HashMap<>();

    public BubbleBlinkAnimationManager(IShape bubble, BubbleLifeCycleStateMachine stateMachine) {
        this.bubble = bubble;
        this.stateMachine = stateMachine;
        setUpBlinkers();
        setupListeners();
    }

    @Override
    public void onDestroy() {
        bubble = null;
        removeListeners();
    }

    private void setupListeners() {
        stateMachine.addAllStateTransitionListener(this);
    }

    private void removeListeners() {
        stateMachine.removeAllStateTransitionListener(this);
    }

    @Override
    public void onEnterState(BubbleLifeCycleStateMachine.State newState) {
        if (currentBlinkModifier != null) {
            bubble.unregisterEntityModifier(currentBlinkModifier);
        }
        if (newState != STABLE && newState != BubbleLifeCycleStateMachine.State.EXPLODING) {
            // We have entered a blinking state. set the correct modifier and remove the existing modifier
            currentBlinkModifier = stateToModifiersMap.get(newState);
            bubble.registerEntityModifier(currentBlinkModifier);
        }
    }

    private void setUpBlinkers() {
        stateToModifiersMap.put(STABLE, null);
        stateToModifiersMap.put(BLINKING_SLOWLY, new BlinkerModifier(1f, 0.5f, BLINKING_SLOWLY.duration));
        stateToModifiersMap.put(BLINKING_FAST, new BlinkerModifier(STABLE.duration / NUM_BLINKS_PER_STATE, BLINK_SPAN_SECONDS, BLINKING_FAST.duration));
        stateToModifiersMap.put(BLINKING_IMMINENT, new BlinkerModifier(STABLE.duration / NUM_BLINKS_PER_STATE, BLINK_SPAN_SECONDS, BLINKING_IMMINENT.duration));
        stateToModifiersMap.put(EXPLODING, null);
    }
}
