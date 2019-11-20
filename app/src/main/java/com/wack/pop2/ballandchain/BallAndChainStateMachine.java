package com.wack.pop2.ballandchain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manages the state of the ball and chain tool that is used to pop bubbles
 */
public class BallAndChainStateMachine {

    public interface Listener {
        void onEnterState(State newState);
    }

    public enum State {

        /**
         * The ball and chain has not been unlocked yet (score is still too low)
         * - The activation button doesn't show and the ball and chain is not on the play field
         * - the ball and chain is sitting under the bottom right corner of the screen, inert
         */
        LOCKED,

        /**
         * The ball and chain is unlocked because the difficulty threshold was passed and is usable
         * - The activation tile is now visible on the screen and shown as "charged"
         * - the ball and chain is sitting under the bottom right corner of the screen, inert
         */
        UNLOCKED_CHARGED,

        /**
         * The ball and chain is unlocked because the difficulty threshold was passed but is unusable
         * since there is no charge
         * - The activation tile is now visible on the screen and shown as "charging"
         * - the ball and chain is sitting under the bottom right corner of the screen, inert
         */
        UNLOCKED_DISCHARGED,

        /**
         * The user is holding the ball and chain and it still has some non zero charge
         * - The ball and chain can pop bubbles
         */
        IN_USE_CHARGED,

        /**
         * The user is holding the ball and chain and it has zero charge
         * - The ball and chain cannot pop bubbles
         */
        IN_USE_DISCHARGED,
    }

    private State currentState = State.LOCKED;
    private Map<State, Set<Listener>> transitionListeners = new HashMap<>();

    public void addTransitionListener(State state, Listener listener) {
        if (transitionListeners.containsKey(state)) {
            Set<Listener> listeners = transitionListeners.get(state);
            if (listeners.contains(listener)) {
                throw new IllegalStateException("Listener had already been added");
            }
            listeners.add(listener);
        }
    }

    public void removeTransitionListener(State state, Listener listener) {
        if (transitionListeners.containsKey(state)) {
            Set<Listener> listeners =  transitionListeners.get(state);
            if (listeners.contains(listener)) {
                listeners.remove(listener);
            } else {
                throw new IllegalStateException("Listener " + listener + " was never added for state " + state);
            }
            if (listeners.isEmpty()) {
                transitionListeners.remove(state);
            }
        }
    }

    public void transitionState(State newState) {
        boolean isValidTransition = false;

        switch (currentState) {
            case LOCKED:
                if (newState == State.UNLOCKED_CHARGED) isValidTransition = true;
                break;
            case UNLOCKED_CHARGED:
                if (newState == State.IN_USE_CHARGED) isValidTransition = true;
                break;
            case UNLOCKED_DISCHARGED:
                if (newState == State.UNLOCKED_CHARGED) isValidTransition = true;
                break;
            case IN_USE_CHARGED:
                if (newState == State.IN_USE_DISCHARGED || newState == State.UNLOCKED_CHARGED) isValidTransition = true;
                break;
            case IN_USE_DISCHARGED:
                if (newState == State.UNLOCKED_DISCHARGED) isValidTransition = true;
                break;
        }

        if (isValidTransition) {
            currentState = newState;
            notifyTransition(currentState);
        } else {
            throw new IllegalArgumentException("Cannot transition ball and chain from " + currentState + " to " + newState);
        }
    }

    private void notifyTransition(State newState) {
        if (transitionListeners.containsKey(newState)) {
            for (Listener listener : transitionListeners.get(newState)) {
                listener.onEnterState(newState);
            }
        }
    }
}
