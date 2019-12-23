package com.wack.pop2.statemachine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages the state of the ball and chain tool that is used to pop bubbles
 */
public abstract class BaseStateMachine<StateType extends Enum> {

    public interface Listener<T> {
        void onEnterState(T newState);
    }

    private StateType currentState = null;
    private Map<StateType, Set<Listener<StateType>>> transitionListeners = new HashMap<>();
    private Map<StateType, Set<StateType>> validTransitions = null;

    protected abstract StateType getInitialState();
    protected abstract List<StateType> getAllStatesList();
    protected abstract Map<StateType, Set<StateType>> getAllValidStateTransitions();

    public BaseStateMachine() {
        currentState = getInitialState();
    }

    /**
     * Adds a transition listener for all states.
     *
     * Notifies of the current state once.
     *
     * @param listener
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
     * Add a new listener for the given state.
     * Triggers a callback on this listener for the current state immediately
     * @param state
     * @param listener
     * @param shouldNotify whether or not we should immediately notify of the current state
     */
    public BaseStateMachine<StateType> addTransitionListener(
            StateType state,
            Listener<StateType> listener,
            boolean shouldNotify) {
        if (!transitionListeners.containsKey(state)) {
            transitionListeners.put(state, new HashSet<Listener<StateType>>());
        }
        Set<Listener<StateType>> listeners = transitionListeners.get(state);
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

    public BaseStateMachine<StateType> removeTransitionListener(StateType state, Listener<StateType> listener) {
        if (transitionListeners.containsKey(state)) {
            Set<Listener<StateType>> listeners =  transitionListeners.get(state);
            if (listeners.contains(listener)) {
                listeners.remove(listener);
            } else {
                throw new IllegalStateException("Listener " + listener + " was never added for state " + state);
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

    public void transitionState(StateType newState) {
        Map<StateType, Set<StateType>> validTransitions = getAllValidStateTransitionsInternal();
        boolean isValidTransition =
                validTransitions.containsKey(currentState)
                        ? validTransitions.get(currentState).contains(newState)
                        : false;

        if (isValidTransition) {
            currentState = newState;
            notifyTransition(currentState);
        } else {
            throw new IllegalArgumentException("Cannot transition ball and chain from " + currentState + " to " + newState);
        }
    }

    private Map<StateType, Set<StateType>> getAllValidStateTransitionsInternal() {
        if (validTransitions == null) {
            validTransitions = getAllValidStateTransitions();
        }
        return validTransitions;
    }

    private void notifyTransition(StateType newState) {
        if (transitionListeners.containsKey(newState)) {
            for (Listener listener : transitionListeners.get(newState)) {
                notifyListenerOfNewState(newState, listener);
            }
        }
    }

    private void notifyListenerOfNewState(StateType newState, Listener<StateType> listener) {
        listener.onEnterState(newState);
    }
}
