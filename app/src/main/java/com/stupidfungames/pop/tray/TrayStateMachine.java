package com.stupidfungames.pop.tray;

import static com.stupidfungames.pop.tray.TrayStateMachine.State.CLOSED;
import static com.stupidfungames.pop.tray.TrayStateMachine.State.CLOSING;
import static com.stupidfungames.pop.tray.TrayStateMachine.State.EMPTY;
import static com.stupidfungames.pop.tray.TrayStateMachine.State.EXPANDED;
import static com.stupidfungames.pop.tray.TrayStateMachine.State.EXPANDING;

import com.stupidfungames.pop.statemachine.BaseStateMachine;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TrayStateMachine extends BaseStateMachine<TrayStateMachine.State> {

  public enum State {
    EMPTY,
    EXPANDED,
    EXPANDING,
    CLOSED,
    CLOSING,
  }

  TrayStateMachine(State startingState) {
    super(startingState);
  }

  @Override
  protected List<State> getAllStatesList() {
    return Arrays.asList(State.values());
  }

  @Override
  protected Map<State, Set<State>> getAllValidStateTransitions() {
    Map<State, Set<State>> validTransitions = new HashMap<>();
    validTransitions.put(EMPTY, new HashSet<>(Arrays.asList(CLOSED)));
    validTransitions.put(EXPANDED, new HashSet<>(Arrays.asList(CLOSING)));
    validTransitions.put(CLOSING, new HashSet<>(Arrays.asList(CLOSED, EXPANDING)));
    validTransitions.put(CLOSED, new HashSet<>(Arrays.asList(EXPANDING)));
    validTransitions.put(EXPANDING, new HashSet<>(Arrays.asList(EXPANDED, CLOSING)));
    return validTransitions;
  }

  @Override
  protected String getLoggingName() {
    return "TrayStateMachine";
  }

  public boolean canOpen() {
    return getCurrentState() == TrayStateMachine.State.CLOSING
        || getCurrentState() == TrayStateMachine.State.CLOSED;
  }

  public boolean canClose() {
    return getCurrentState() == TrayStateMachine.State.EXPANDING
        || getCurrentState() == TrayStateMachine.State.EXPANDED;

  }
}
