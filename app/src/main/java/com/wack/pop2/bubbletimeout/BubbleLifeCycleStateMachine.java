package com.wack.pop2.bubbletimeout;

import com.wack.pop2.statemachine.BaseStateMachine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wack.pop2.bubbletimeout.BubbleLifeCycleStateMachine.State.STABLE;

class BubbleLifeCycleStateMachine extends BaseStateMachine<BubbleLifeCycleStateMachine.State> {

    public BubbleLifeCycleStateMachine() {
        super(STABLE);
    }

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
        validTransitions.put(STABLE, new HashSet<>(Arrays.asList(State.BLINKING_SLOWLY)));
        validTransitions.put(State.BLINKING_SLOWLY, new HashSet<>(Arrays.asList(State.BLINKING_FAST)));
        validTransitions.put(State.BLINKING_FAST, new HashSet<>(Arrays.asList(State.BLINKING_IMMINENT)));
        validTransitions.put(State.BLINKING_IMMINENT, new HashSet<>(Arrays.asList(State.EXPLODING)));
        return validTransitions;
    }

    @Override
    public void transitionState(BubbleLifeCycleStateMachine.State newState) {
        super.transitionState(newState);
    }

}
