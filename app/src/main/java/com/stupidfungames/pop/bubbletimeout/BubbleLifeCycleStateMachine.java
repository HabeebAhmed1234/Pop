package com.stupidfungames.pop.bubbletimeout;

import static com.stupidfungames.pop.GameConstants.BUBBLE_ABOUT_TO_EXPLODE_TIME_SECONDS;
import static com.stupidfungames.pop.GameConstants.BUBBLE_STABLE_TIME_SECONDS;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.ABOUT_TO_EXPLODE;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.EXPLODING;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.IDLE;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.STABLE;

import com.stupidfungames.pop.statemachine.BaseStateMachine;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class BubbleLifeCycleStateMachine extends BaseStateMachine<BubbleLifeCycleStateMachine.State> {

  public BubbleLifeCycleStateMachine() {
    super(IDLE);
  }

  /**
   * The following state transition durations are overridden by whether or not the bubble is on the
   * screen or not
   */
  public enum State {

    /**
     * The bubble is not on the screen and should not be doing anything
     */
    IDLE(0f),

    /**
     * The bubble is stable and not about to explode
     */
    STABLE(BUBBLE_STABLE_TIME_SECONDS),

    /**
     * The bubble has showing its going to explode
     */
    ABOUT_TO_EXPLODE(BUBBLE_ABOUT_TO_EXPLODE_TIME_SECONDS),

    /**
     * The bubble has exploded
     */
    EXPLODING(1f);

    public final float duration;

    State(final float duration) {
      this.duration = duration;
    }
  }

  @Override
  protected List<BubbleLifeCycleStateMachine.State> getAllStatesList() {
    return Arrays.asList(State.values());
  }

  @Override
  protected Map getAllValidStateTransitions() {
    Map<State, Set<State>> validTransitions = new HashMap<>();
    validTransitions.put(IDLE, new HashSet<>(Arrays.asList(STABLE)));
    validTransitions.put(STABLE, new HashSet<>(Arrays.asList(STABLE, IDLE, ABOUT_TO_EXPLODE)));
    validTransitions.put(ABOUT_TO_EXPLODE, new HashSet<>(Arrays.asList(STABLE, IDLE, EXPLODING)));
    validTransitions.put(EXPLODING, new HashSet<>(Arrays.asList(IDLE, STABLE)));
    return validTransitions;
  }

  @Override
  public void transitionState(BubbleLifeCycleStateMachine.State newState) {
    super.transitionState(newState);
  }

}
