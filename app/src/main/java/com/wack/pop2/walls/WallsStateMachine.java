package com.wack.pop2.walls;

import com.wack.pop2.statemachine.BaseStateMachine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wack.pop2.walls.WallsStateMachine.State.LOCKED;

public class WallsStateMachine extends BaseStateMachine<WallsStateMachine.State> {

    public enum State {

        /**
         * The walls tool is locked
         */
        LOCKED,

        /**
         * The walls tool is unlocked but not in use
         */
        UNLOCKED_TOGGLED_OFF,

        /**
         * The walls tool is toggled on
         */
        TOGGLED_ON,
    }

    @Override
    protected State getInitialState() {
        return LOCKED;
    }

    @Override
    protected List<State> getAllStatesList() {
        return Arrays.asList(State.values());
    }

    @Override
    protected Map<State, Set<State>> getAllValidStateTransitions() {
        Map<State, Set<State>> validTransitions = new HashMap<>();
        validTransitions.put(State.LOCKED, new HashSet<>(Arrays.asList(State.UNLOCKED_TOGGLED_OFF)));
        validTransitions.put(State.UNLOCKED_TOGGLED_OFF, new HashSet<>(Arrays.asList(State.TOGGLED_ON)));
        validTransitions.put(State.TOGGLED_ON, new HashSet<>(Arrays.asList(State.UNLOCKED_TOGGLED_OFF)));
        return validTransitions;
    }
}
