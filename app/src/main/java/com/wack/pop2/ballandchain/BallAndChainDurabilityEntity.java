package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

/**
 * Monitors the durability of the ball and chain. Sets the ball and chain state accordingly.
 *
 * If the ball and chain enters the UNLOCKED_DISCHARGED or IN_USE_DISCHARGED state we start
 * charging the ball and chain up again (if its not already charging) and reflect that in a charge
 * meter on the screen.
 *
 *
 */
class BallAndChainDurabilityEntity extends BaseEntity implements EventBus.Subscriber, BallAndChainStateMachine.Listener {

    private static final float BALL_AND_CHAIN_CHARGE_INTERVAL_TIME = 1.0f;
    private static final float BALL_AND_CHAIN_CHARGE_INCREMENT = 0.1f;
    private static final float BALL_AND_CHAIN_DISCHARGE_DECREMENT = 0.1f;

    private BallAndChainStateMachine stateMachine;

    private float ballAndChainChargePercent = 1.0f;
    private boolean isCharging = false;


    private TimerHandler chargeHandler = new TimerHandler(
            BALL_AND_CHAIN_CHARGE_INTERVAL_TIME,
            true,
            new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    applyChargeToBallAndChain();
                }
            });

    public BallAndChainDurabilityEntity(
            BallAndChainStateMachine stateMachine,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
    }

    @Override
    public void onCreateScene() {
        stateMachine.addTransitionListener(BallAndChainStateMachine.State.UNLOCKED_DISCHARGED, this)
                .addTransitionListener(BallAndChainStateMachine.State.IN_USE_DISCHARGED, this);
        EventBus.get().subscribe(GameEvent.BALL_AND_CHAIN_POPPED_BUBBLE, this);
    }

    @Override
    public void onDestroy() {
        stateMachine.removeTransitionListener(BallAndChainStateMachine.State.UNLOCKED_DISCHARGED, this)
                .removeTransitionListener(BallAndChainStateMachine.State.IN_USE_DISCHARGED, this);
        EventBus.get().unSubscribe(GameEvent.BALL_AND_CHAIN_POPPED_BUBBLE, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == GameEvent.BALL_AND_CHAIN_POPPED_BUBBLE) {
            applyDischargeToBallAndChain(BALL_AND_CHAIN_DISCHARGE_DECREMENT);
        }
    }

    @Override
    public void onEnterState(BallAndChainStateMachine.State newState) {
        switch (newState) {
            case UNLOCKED_DISCHARGED:
            case IN_USE_DISCHARGED:
                startChargingBallAndChain();
        }
    }

    /**
     * The ball and chain was used to pop a bubble. Apply a discharge
     */
    private void applyDischargeToBallAndChain(float dischargeDecrement) {
        ballAndChainChargePercent -= dischargeDecrement;
        if (ballAndChainChargePercent < 0) {
            ballAndChainChargePercent = 0;
        }

        if (ballAndChainChargePercent == 0) {
            if (stateMachine.getCurrentState() == BallAndChainStateMachine.State.IN_USE_CHARGED) {
                stateMachine.transitionState(BallAndChainStateMachine.State.IN_USE_DISCHARGED);
            } else if (stateMachine.getCurrentState() == BallAndChainStateMachine.State.UNLOCKED_CHARGED) {
                stateMachine.transitionState(BallAndChainStateMachine.State.UNLOCKED_DISCHARGED);
            }
        }
    }

    private void startChargingBallAndChain() {
        if (!isCharging) {
            isCharging = true;
            engine.registerUpdateHandler(chargeHandler);
        }
    }

    private void applyChargeToBallAndChain() {
        ballAndChainChargePercent += BALL_AND_CHAIN_CHARGE_INCREMENT;
        if (ballAndChainChargePercent > 1.0f) {
            ballAndChainChargePercent = 1.0f;
        }
        if (ballAndChainChargePercent == 1.0f) {
            onBallAndChainCharged();
        }
    }

    private void onBallAndChainCharged() {
        isCharging = false;
        if (stateMachine.getCurrentState() == BallAndChainStateMachine.State.IN_USE_DISCHARGED) {
            stateMachine.transitionState(BallAndChainStateMachine.State.IN_USE_CHARGED);
        } else if (stateMachine.getCurrentState() == BallAndChainStateMachine.State.UNLOCKED_DISCHARGED) {
            stateMachine.transitionState(BallAndChainStateMachine.State.UNLOCKED_CHARGED);
        }
        engine.unregisterUpdateHandler(chargeHandler);
    }
}
