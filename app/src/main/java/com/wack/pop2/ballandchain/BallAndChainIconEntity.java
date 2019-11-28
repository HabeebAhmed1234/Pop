package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;

/**
 * Appears when the ball and chain tool is unlocked. The user can tap and hold the icon to start
 * using the ball and chain.
 */
public class BallAndChainIconEntity extends BaseEntity implements EventBus.Subscriber, BallAndChainStateMachine.Listener {

    private BallAndChainStateMachine stateMachine;

    public BallAndChainIconEntity(
            BallAndChainStateMachine stateMachine,
            GameResources gameResources) {
        super(gameResources);
        stateMachine = stateMachine;
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(GameEvent.DIFFICULTY_CHANGE, this);
        stateMachine
                .addTransitionListener(BallAndChainStateMachine.State.UNLOCKED_CHARGED, this)
                .addTransitionListener(BallAndChainStateMachine.State.UNLOCKED_DISCHARGED, this)
                .addTransitionListener(BallAndChainStateMachine.State.IN_USE_CHARGED, this)
                .addTransitionListener(BallAndChainStateMachine.State.IN_USE_DISCHARGED, this);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.DIFFICULTY_CHANGE, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        switch (event) {
            case DIFFICULTY_CHANGE:
                DifficultyChangedEventPayload difficultyChangedEventPayload =
                        (DifficultyChangedEventPayload) payload;
                onScoreChanged(difficultyChangedEventPayload.newDifficulty);
                break;
        }
    }

    private void onScoreChanged(int newDifficulty) {
        if (newDifficulty >= BallAndChainConstants.BALL_AND_CHAIN_DIFFICULTY_UNLOCK_THRESHOLD) {
            stateMachine.transitionState(
                    BallAndChainStateMachine.State.UNLOCKED_CHARGED);
        }
    }

    @Override
    public void onEnterState(BallAndChainStateMachine.State newState) {
        switch (newState) {
            case UNLOCKED_CHARGED:
                break;
            case UNLOCKED_DISCHARGED:
                break;
            case IN_USE_CHARGED:
                break;
            case IN_USE_DISCHARGED:
                break;
        }
    }
}
