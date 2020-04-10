package com.stupidfungames.pop.nuke;

import com.stupidfungames.pop.statemachine.BaseStateMachine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.stupidfungames.pop.nuke.NukeStateMachine.State.LOCKED;

public class NukeStateMachine extends BaseStateMachine<NukeStateMachine.State> {

    public NukeStateMachine() {
        super(LOCKED);
    }

    public enum State {

        /**
         * The nuke tool is locked and invisible
         */
        LOCKED,

        /**
         * The nuke is unlocked and ready to use
         */
        READY,

        /**
         * The nuke is firing its waves
         */
        NUKING,

        /**
         * The nuke is on cooldown. cannot be used
         */
        COOLDOWN,
    }

    @Override
    protected List<State> getAllStatesList() {
        return Arrays.asList(State.values());
    }

    @Override
    protected Map<State, Set<State>> getAllValidStateTransitions() {
        Map<State, Set<State>> validTransitions = new HashMap<>();
        validTransitions.put(State.LOCKED, new HashSet<>(Arrays.asList(State.READY)));
        validTransitions.put(State.READY, new HashSet<>(Arrays.asList(State.NUKING)));
        validTransitions.put(State.NUKING, new HashSet<>(Arrays.asList(State.COOLDOWN)));
        validTransitions.put(State.COOLDOWN, new HashSet<>(Arrays.asList(State.READY)));
        return validTransitions;
    }
}
