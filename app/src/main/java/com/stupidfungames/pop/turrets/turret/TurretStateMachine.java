package com.stupidfungames.pop.turrets.turret;

import static com.stupidfungames.pop.turrets.turret.TurretStateMachine.State.DOCKED;
import static com.stupidfungames.pop.turrets.turret.TurretStateMachine.State.DRAGGING;
import static com.stupidfungames.pop.turrets.turret.TurretStateMachine.State.FIRING;
import static com.stupidfungames.pop.turrets.turret.TurretStateMachine.State.TARGETING;

import com.stupidfungames.pop.statemachine.BaseStateMachine;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TurretStateMachine extends BaseStateMachine<TurretStateMachine.State> {

  public TurretStateMachine() {
    super(DOCKED);
  }

  public enum State {
    DOCKED, // The turret is not in play
    DRAGGING, // The turret is being dragged by the player
    TARGETING, // The turret is looking for a new target
    FIRING, // The turret has found a target and is firing a new projectile
  }

  @Override
  protected List<State> getAllStatesList() {
    return Arrays.asList(State.values());
  }

  @Override
  protected Map<State, Set<State>> getAllValidStateTransitions() {
    Map<State, Set<State>> transitions = new HashMap<>();
    transitions.put(DOCKED, new HashSet<>(Arrays.asList(DRAGGING, TARGETING)));
    transitions.put(DRAGGING, new HashSet<>(Arrays.asList(TARGETING, DOCKED)));
    transitions.put(TARGETING, new HashSet<>(Arrays.asList(DRAGGING, FIRING)));
    transitions.put(FIRING, new HashSet<>(Arrays.asList(TARGETING)));
    return transitions;
  }

  @Override
  protected String getLoggingName() {
    return "TurretStateMachine";
  }
}
