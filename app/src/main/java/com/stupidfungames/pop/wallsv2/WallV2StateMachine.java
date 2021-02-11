package com.stupidfungames.pop.wallsv2;

import static com.stupidfungames.pop.wallsv2.WallV2StateMachine.State.DOCKED;
import static com.stupidfungames.pop.wallsv2.WallV2StateMachine.State.DRAGGING;
import static com.stupidfungames.pop.wallsv2.WallV2StateMachine.State.DROPPED_CHARGED;
import static com.stupidfungames.pop.wallsv2.WallV2StateMachine.State.DROPPED_DISCHARGED;

import com.stupidfungames.pop.statemachine.BaseStateMachine;
import com.stupidfungames.pop.wallsv2.WallV2StateMachine.State;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WallV2StateMachine extends BaseStateMachine<State> {

  public WallV2StateMachine() {
    super(DOCKED);
  }

  public enum State {
    DOCKED,
    DRAGGING,
    DROPPED_CHARGED,
    DROPPED_DISCHARGED,
  }

  @Override
  protected List<State> getAllStatesList() {
    return Arrays.asList(State.values());
  }

  @Override
  protected Map<State, Set<State>> getAllValidStateTransitions() {
    Map<State, Set<State>> validTransitions = new HashMap<>();
    validTransitions.put(DOCKED, new HashSet<>(Arrays.asList(DRAGGING)));
    validTransitions.put(DRAGGING, new HashSet<>(Arrays.asList(DOCKED, DROPPED_CHARGED, DROPPED_DISCHARGED)));
    validTransitions.put(DROPPED_CHARGED, new HashSet<>(Arrays.asList(DRAGGING, DROPPED_DISCHARGED)));
    validTransitions.put(DROPPED_DISCHARGED, new HashSet<>(Arrays.asList(DRAGGING, DROPPED_CHARGED)));
    return validTransitions;
  }

  @Override
  protected String getLoggingName() {
    return "WallV2StateMachine";
  }
}
