package com.wack.pop2.turret;

import com.wack.pop2.statemachine.BaseStateMachine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wack.pop2.turret.TurretStateMachine.State.DOCKED;

class TurretStateMachine extends BaseStateMachine<TurretStateMachine.State> {

    public enum State {
        DOCKED, // The turret is not in play
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
        return null;
    }
}
