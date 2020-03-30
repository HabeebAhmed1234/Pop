package com.wack.pop2.bubbletimeout;

import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.shape.IShape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private final int NUM_BLINKS = 10;
    private final float OFF_ALPHA = 0.2f;
    private final float ON_ALPHA = 1f;

    private IShape bubble;
    private BubbleLifeCycleStateMachine stateMachine;
    private GameSoundsManager soundsManager;
    private IEntityModifier currentBlinkModifier;

    private Map<BubbleLifeCycleStateMachine.State, IEntityModifier> stateToModifiersMap = new HashMap<>();

    private BlinkerModifier.NewSegmentCallback newSegmentCallback = new BlinkerModifier.NewSegmentCallback() {
        @Override
        public void onNewSegment(int segment) {
            if (segment % 2 == 0) {
                soundsManager.getSound(SoundId.BEEP).play();
            }
        }
    };

    public BubbleBlinkAnimationManager(IShape bubble, BubbleLifeCycleStateMachine stateMachine, GameSoundsManager soundsManager) {
        this.bubble = bubble;
        this.stateMachine = stateMachine;
        this.soundsManager = soundsManager;
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
        final float[] blinkingPattern = getBlinkingPattern(NUM_BLINKS);
        stateToModifiersMap.put(STABLE, null);
        stateToModifiersMap.put(BLINKING_SLOWLY, new BlinkerModifier(blinkingPattern, BLINKING_SLOWLY.duration, newSegmentCallback));
        stateToModifiersMap.put(BLINKING_FAST, new BlinkerModifier(blinkingPattern, BLINKING_FAST.duration, newSegmentCallback));
        stateToModifiersMap.put(BLINKING_IMMINENT, new BlinkerModifier(blinkingPattern, BLINKING_IMMINENT.duration, newSegmentCallback));
        stateToModifiersMap.put(EXPLODING, null);
    }

    private float[] getBlinkingPattern(int numBlinks) {
        List<Float> pattern = new ArrayList<>();
        pattern.add(ON_ALPHA);
        for (int i = 0 ; i < numBlinks ; i++) {
            pattern.add(OFF_ALPHA);
            pattern.add(ON_ALPHA);
        }
        float[] array = new float[pattern.size()];
        for (int i = 0 ; i < pattern.size() ; i++) {
            array[i] = pattern.get(i);
        }
        return array;

    }
}
