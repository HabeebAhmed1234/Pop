package com.wack.pop2.ballandchain;

import com.wack.pop2.statemachine.BaseStateMachine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages the state of the ball and chain tool that is used to pop bubbles
 */
public class BallAndChainStateMachine extends BaseStateMachine<BallAndChainStateMachine.State> {

    public BallAndChainStateMachine() {
        super(State.LOCKED);
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

    @Override
    protected List getAllStatesList() {
        return Arrays.asList(State.values());
    }

    @Override
    protected Map getAllValidStateTransitions() {
        Map<State, Set<State>> validTransitions = new HashMap<>();
        validTransitions.put(State.LOCKED, new HashSet<>(Arrays.asList(State.UNLOCKED_CHARGED)));
        validTransitions.put(State.UNLOCKED_CHARGED, new HashSet<>(Arrays.asList(State.IN_USE_CHARGED)));
        validTransitions.put(State.UNLOCKED_DISCHARGED, new HashSet<>(Arrays.asList(State.UNLOCKED_CHARGED)));
        validTransitions.put(State.IN_USE_CHARGED, new HashSet<>(Arrays.asList(State.IN_USE_DISCHARGED, State.UNLOCKED_CHARGED)));
        validTransitions.put(State.IN_USE_DISCHARGED, new HashSet<>(Arrays.asList(State.UNLOCKED_DISCHARGED, State.IN_USE_CHARGED)));
        return validTransitions;
    }
}
