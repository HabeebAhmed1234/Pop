package com.stupidfungames.pop.bubbletimeout;

import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_FAST;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_IMMINENT;
import static com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State.BLINKING_SLOWLY;
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
    STABLE(6f),

    /**
     * The bubble has started blinking slowly
     */
    BLINKING_SLOWLY(3f),

    /**
     * The bubble blinking has sped up
     */
    BLINKING_FAST(2f),

    /**
     * The bubble is about to explode
     */
    BLINKING_IMMINENT(1f),

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
    validTransitions.put(STABLE, new HashSet<>(Arrays.asList(IDLE, BLINKING_SLOWLY, STABLE)));
    validTransitions.put(BLINKING_SLOWLY, new HashSet<>(Arrays.asList(IDLE, STABLE, BLINKING_FAST)));
    validTransitions.put(BLINKING_FAST, new HashSet<>(Arrays.asList(IDLE, STABLE, BLINKING_IMMINENT)));
    validTransitions.put(BLINKING_IMMINENT, new HashSet<>(Arrays.asList(IDLE, STABLE, EXPLODING)));
    validTransitions.put(EXPLODING, new HashSet<>(Arrays.asList(IDLE, STABLE)));
    return validTransitions;
  }

  @Override
  public void transitionState(BubbleLifeCycleStateMachine.State newState) {
    super.transitionState(newState);
  }

}
