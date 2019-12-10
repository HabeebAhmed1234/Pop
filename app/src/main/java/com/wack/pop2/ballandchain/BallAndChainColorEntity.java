package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

import org.andengine.util.color.AndengineColor;

/**
 * Sets the color of the ball and chain given the current state.
 */
class BallAndChainColorEntity extends BaseEntity implements BallAndChainStateMachine.Listener {

    private static final AndengineColor CHARGED_COLOR = AndengineColor.GREEN;
    private static final AndengineColor DISCHARGED_COLOR = AndengineColor.RED;

    private BallAndChainStateMachine stateMachine;
    private BallAndChain ballAndChain;

    public BallAndChainColorEntity(
            BallAndChainStateMachine stateMachine,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
    }

    public void setBallAndChain(BallAndChain ballAndChain) {
        this.ballAndChain = ballAndChain;
    }

    @Override
    public void onCreateScene() {
        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        stateMachine.removeAllStateTransitionListener(this);
    }

    @Override
    public void onEnterState(BallAndChainStateMachine.State newState) {
        switch (newState) {
            case UNLOCKED_CHARGED:
            case IN_USE_CHARGED:
                ballAndChain.setColor(CHARGED_COLOR);
                break;
            case UNLOCKED_DISCHARGED:
            case IN_USE_DISCHARGED:
                ballAndChain.setColor(DISCHARGED_COLOR);
                break;
            case LOCKED:
                ballAndChain.setColor(AndengineColor.WHITE);
        }
    }
}
