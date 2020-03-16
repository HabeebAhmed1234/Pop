package com.wack.pop2.tray;

import com.wack.pop2.statemachine.BaseStateMachine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wack.pop2.tray.TrayStateMachine.State.CLOSED;
import static com.wack.pop2.tray.TrayStateMachine.State.CLOSING;
import static com.wack.pop2.tray.TrayStateMachine.State.EXPANDED;
import static com.wack.pop2.tray.TrayStateMachine.State.EXPANDING;

class TrayStateMachine extends BaseStateMachine<TrayStateMachine.State> {

    public enum State {
        EXPANDED,
        EXPANDING,
        CLOSED,
        CLOSING,
    }

    TrayStateMachine(boolean isInitiallyExpanded) {
        super(isInitiallyExpanded ? EXPANDED : CLOSED);
    }

    @Override
    protected List<State> getAllStatesList() {
        return Arrays.asList(State.values());
    }

    @Override
    protected Map<State, Set<State>> getAllValidStateTransitions() {
        Map<State, Set<State>> validTransitions = new HashMap<>();
        validTransitions.put(EXPANDED, new HashSet<>(Arrays.asList(CLOSING)));
        validTransitions.put(CLOSING, new HashSet<>(Arrays.asList(CLOSED, EXPANDING)));
        validTransitions.put(CLOSED, new HashSet<>(Arrays.asList(EXPANDING)));
        validTransitions.put(EXPANDING, new HashSet<>(Arrays.asList(EXPANDED, CLOSING)));
        return validTransitions;
    }

}
