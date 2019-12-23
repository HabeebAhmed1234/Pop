package com.wack.pop2.turret;

import com.wack.pop2.statemachine.BaseStateMachine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wack.pop2.turret.TurretStateMachine.State.DOCKED;
import static com.wack.pop2.turret.TurretStateMachine.State.DRAGGING;
import static com.wack.pop2.turret.TurretStateMachine.State.FIRING;
import static com.wack.pop2.turret.TurretStateMachine.State.TARGETING;

class TurretStateMachine extends BaseStateMachine<TurretStateMachine.State> {

    public enum State {
        DOCKED, // The turret is not in play
        DRAGGING, // The turret is being dragged by the player
        TARGETING, // The turret is looking for a new target
        FIRING, // The turret has found a target and is firing a new projectile
    }

    @Override
    protected State getInitialState() {
        return DOCKED;
    }

    @Override
    protected List<State> getAllStatesList() {
        return Arrays.asList(State.values());
    }

    @Override
    protected Map<State, Set<State>> getAllValidStateTransitions() {
        Map<State, Set<State>> transitions = new HashMap<>();
        transitions.put(DOCKED, new HashSet<>(Arrays.asList(DRAGGING)));
        transitions.put(DRAGGING, new HashSet<>(Arrays.asList(TARGETING, DOCKED)));
        transitions.put(TARGETING, new HashSet<>(Arrays.asList(DRAGGING, FIRING)));
        transitions.put(FIRING, new HashSet<>(Arrays.asList(TARGETING)));
        return transitions;
    }
}
