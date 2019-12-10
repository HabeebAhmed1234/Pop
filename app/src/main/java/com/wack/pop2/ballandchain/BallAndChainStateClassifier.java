package com.wack.pop2.ballandchain;

/**
 * Helps classify what states mean what is possible
 */
class BallAndChainStateClassifier {

    public static boolean isChainHandleDraggingPermitted(BallAndChainStateMachine.State state) {
        return state == BallAndChainStateMachine.State.IN_USE_CHARGED
                || state == BallAndChainStateMachine.State.IN_USE_DISCHARGED;
    }

    public static boolean isBallAndChainInUse(BallAndChainStateMachine.State state) {
        return state == BallAndChainStateMachine.State.IN_USE_DISCHARGED
                || state == BallAndChainStateMachine.State.IN_USE_CHARGED;
    }
}
