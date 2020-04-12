package com.stupidfungames.pop.bubbletimeout;

import com.stupidfungames.pop.statemachine.BaseStateMachine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_FAST;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_IMMINENT;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_SLOWLY;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.EXPLODING;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.STABLE;

class BubbleLifeCycleStateMachine extends BaseStateMachine<BubbleLifeCycleStateMachine.State> {

    public BubbleLifeCycleStateMachine() {
        super(STABLE);
    }

    /**
     * The following state transition durations are overridden by whether or not the bubble is on
     * the screen or not
     */
    public enum State {
        /**
         * The bubble is stable and not about to explode
         */
        STABLE(10f),

        /**
         * The bubble has started blinking slowly
         */
        BLINKING_SLOWLY(3f),

        /**
         * The bubble blinking has sped up
         */
        BLINKING_FAST(2f),

        /**
         * The bubble is about to explode
         */
        BLINKING_IMMINENT(1f),

        /**
         * The bubble has exploded
         */
        EXPLODING(1f);

        public final float duration;

        State(final float duration) {
            this.duration = duration;
        }
    }

    @Override
    protected List<BubbleLifeCycleStateMachine.State> getAllStatesList() {
        return Arrays.asList(State.values());
    }

    @Override
    protected Map getAllValidStateTransitions() {
        Map<State, Set<State>> validTransitions = new HashMap<>();
        validTransitions.put(STABLE, new HashSet<>(Arrays.asList(BLINKING_SLOWLY, STABLE)));
        validTransitions.put(BLINKING_SLOWLY, new HashSet<>(Arrays.asList(BLINKING_FAST)));
        validTransitions.put(BLINKING_FAST, new HashSet<>(Arrays.asList(BLINKING_IMMINENT)));
        validTransitions.put(BLINKING_IMMINENT, new HashSet<>(Arrays.asList(EXPLODING)));
        return validTransitions;
    }

    @Override
    public void transitionState(BubbleLifeCycleStateMachine.State newState) {
        super.transitionState(newState);
    }

}