package com.stupidfungames.pop.ballandchain;

import androidx.annotation.Nullable;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;

import org.andengine.util.color.AndengineColor;

/**
 * Sets the color of the ball and chain given the current state.
 */
class BallAndChainColorEntity extends BaseEntity implements BallAndChainStateMachine.Listener<BallAndChainStateMachine.State> {

    private static final AndengineColor CHARGED_COLOR = AndengineColor.GREEN;
    private static final AndengineColor DISCHARGED_COLOR = AndengineColor.RED;

    @Nullable private BallAndChain ballAndChain;

    public BallAndChainColorEntity(BinderEnity parent) {
        super(parent);
    }

    public void setBallAndChain(BallAndChain ballAndChain) {
        this.ballAndChain = ballAndChain;
    }

    @Override
    public void onCreateScene() {
        get(BallAndChainStateMachine.class).addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        get(BallAndChainStateMachine.class).removeAllStateTransitionListener(this);
    }

    @Override
    public void onEnterState(BallAndChainStateMachine.State newState) {
        if (ballAndChain == null) return;
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
