package com.stupidfungames.pop.sword;

import static com.stupidfungames.pop.sword.SwordStateMachine.State.CHARGED;
import static com.stupidfungames.pop.sword.SwordStateMachine.State.LOCKED;
import static com.stupidfungames.pop.sword.SwordStateMachine.State.UNCHARGED;

import com.stupidfungames.pop.statemachine.BaseStateMachine;
import com.stupidfungames.pop.sword.SwordStateMachine.State;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SwordStateMachine extends BaseStateMachine<State> {

  public enum State {
    LOCKED,
    CHARGED,
    UNCHARGED,
  }

  public SwordStateMachine() {
    super(LOCKED);
  }

  @Override
  protected List getAllStatesList() {
    return Arrays.asList(State.values());
  }

  @Override
  protected Map getAllValidStateTransitions() {
    Map<State, Set<State>> validTransitions = new HashMap<>();
    validTransitions.put(LOCKED, new HashSet<>(Arrays.asList(CHARGED)));
    validTransitions.put(CHARGED, new HashSet<>(Arrays.asList(CHARGED, UNCHARGED)));
    validTransitions.put(UNCHARGED, new HashSet<>(Arrays.asList(UNCHARGED, CHARGED)));
    return validTransitions;
  }

  @Override
  protected String getLoggingName() {
    return "SwordStateMachine";
  }
}
