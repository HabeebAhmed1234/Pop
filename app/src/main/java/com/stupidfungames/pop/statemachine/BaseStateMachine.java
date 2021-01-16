package com.stupidfungames.pop.statemachine;

import android.util.Log;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Manages the state of the ball and chain tool that is used to pop bubbles
 */
public abstract class BaseStateMachine<StateType extends Enum> {

  public interface Listener<T> {

    void onEnterState(T newState);
  }

  private final StateType initialState;
  private StateType currentState = null;
  private Map<StateType, Queue<Listener<StateType>>> transitionListeners = new ConcurrentHashMap<>();
  private Map<StateType, Set<StateType>> validTransitions = null;

  protected abstract List<StateType> getAllStatesList();

  protected abstract Map<StateType, Set<StateType>> getAllValidStateTransitions();

  protected abstract String getLoggingName();

  protected boolean allowSelfStateTransitions() {
    return false;
  }


  public BaseStateMachine(StateType initialState) {
    this.initialState = initialState;
    currentState = initialState;
  }

  /**
   * Adds a transition listener for all states.
   *
   * Notifies of the current state once.
   */
  public void addAllStateTransitionListener(Listener<StateType> listener) {
    for (StateType state : getAllStatesList()) {
      addTransitionListener(state, listener, false);
    }
    notifyListenerOfNewState(currentState, listener);
  }

  public void removeAllStateTransitionListener(Listener<StateType> listener) {
    for (StateType state : getAllStatesList()) {
      removeTransitionListener(state, listener);
    }
  }

  /**
   * Add a new listener for the given state. Triggers a callback on this listener for the current
   * state immediately
   *
   * @param shouldNotify whether or not we should immediately notify of the current state
   */
  public BaseStateMachine<StateType> addTransitionListener(
      StateType state,
      Listener<StateType> listener,
      boolean shouldNotify) {
    if (!transitionListeners.containsKey(state)) {
      transitionListeners.put(state, new LinkedBlockingQueue<Listener<StateType>>());
    }
    Queue<Listener<StateType>> listeners = transitionListeners.get(state);
    if (listeners.contains(listener)) {
      throw new IllegalStateException("Listener had already been added");
    }
    listeners.add(listener);
    if (shouldNotify) {
      notifyListenerOfNewState(currentState, listener);
    }
    return this;
  }

  public BaseStateMachine<StateType> addTransitionListener(
      StateType state,
      Listener<StateType> listener) {
    return addTransitionListener(state, listener, true);
  }

  public BaseStateMachine<StateType> removeTransitionListener(StateType state,
      Listener<StateType> listener) {
    if (transitionListeners.containsKey(state)) {
      Queue<Listener<StateType>> listeners = transitionListeners.get(state);
      if (listeners.contains(listener)) {
        listeners.remove(listener);
      } else {
        throw new IllegalStateException(
            "Listener " + listener + " was never added for state " + state);
      }
      if (listeners.isEmpty()) {
        transitionListeners.remove(state);
      }
    }
    return this;
  }

  public StateType getCurrentState() {
    return currentState;
  }

  public boolean isValidTransition(StateType newState) {
    Map<StateType, Set<StateType>> validTransitions = getAllValidStateTransitionsInternal();
    return validTransitions.containsKey(currentState)
        ? validTransitions.get(currentState).contains(newState)
        : false;
  }

  public void transitionState(StateType newState) {
    boolean isValidTransition = isValidTransition(newState);

    if (newState == currentState && allowSelfStateTransitions()) {
      isValidTransition = true;
    }

    if (isValidTransition) {
      currentState = newState;
      notifyTransition(currentState);
    } else {
      Log.e(getLoggingName(), "Illegal state transition", new IllegalArgumentException(
          "Cannot transition state machine from " + currentState + " to " + newState));
    }
  }

  /**
   * Resets then state back to initial state and forces the transition.
   */
  public void reset() {
    currentState = initialState;
    notifyTransition(currentState);
  }

  private Map<StateType, Set<StateType>> getAllValidStateTransitionsInternal() {
    if (validTransitions == null) {
      validTransitions = getAllValidStateTransitions();
    }
    return validTransitions;
  }

  private void notifyTransition(StateType newState) {
    if (transitionListeners.containsKey(newState)) {
      Iterator<Listener<StateType>> listeners = transitionListeners.get(newState).iterator();
      while (listeners.hasNext()) {
        notifyListenerOfNewState(newState, listeners.next());
      }
    }
  }

  private void notifyListenerOfNewState(StateType newState, Listener<StateType> listener) {
    listener.onEnterState(newState);
  }
}
