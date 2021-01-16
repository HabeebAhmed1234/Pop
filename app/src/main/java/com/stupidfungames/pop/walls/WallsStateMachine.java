package com.stupidfungames.pop.walls;

import static com.stupidfungames.pop.walls.WallsStateMachine.State.LOCKED;

import com.stupidfungames.pop.statemachine.BaseStateMachine;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WallsStateMachine extends BaseStateMachine<WallsStateMachine.State> {

  public WallsStateMachine() {
    super(LOCKED);
  }

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
  protected List<State> getAllStatesList() {
    return Arrays.asList(State.values());
  }

  @Override
  protected Map<State, Set<State>> getAllValidStateTransitions() {
    Map<State, Set<State>> validTransitions = new HashMap<>();
    validTransitions.put(State.LOCKED, new HashSet<>(Arrays.asList(State.UNLOCKED_TOGGLED_OFF)));
    validTransitions
        .put(State.UNLOCKED_TOGGLED_OFF, new HashSet<>(Arrays.asList(State.TOGGLED_ON)));
    validTransitions
        .put(State.TOGGLED_ON, new HashSet<>(Arrays.asList(State.UNLOCKED_TOGGLED_OFF)));
    return validTransitions;
  }

  @Override
  protected String getLoggingName() {
    return "WallsStateMachine";
  }
}
