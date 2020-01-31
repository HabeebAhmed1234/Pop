package com.wack.pop2.bubbletimeout;

import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameOverExplosionEventPayload;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.entity.sprite.Sprite;

class BubbleLifecycleGameOverEntity implements BubbleLifecycleController, BaseStateMachine.Listener<BubbleLifeCycleStateMachine.State> {

    private BubbleLifeCycleStateMachine stateMachine;
    private Sprite bubble;

    public BubbleLifecycleGameOverEntity(Sprite bubble, BubbleLifeCycleStateMachine stateMachine) {
        this.bubble = bubble;
        this.stateMachine = stateMachine;

        stateMachine.addTransitionListener(BubbleLifeCycleStateMachine.State.EXPLODING, this, false);
    }

    @Override
    public void onDestroy() {
        stateMachine.removeTransitionListener(BubbleLifeCycleStateMachine.State.EXPLODING, this);
    }

    public void triggerGameOverExplosion() {
        EventBus.get().sendEvent(
                GameEvent.GAME_OVER_ON_EXPLOSION_EVENT,
                new GameOverExplosionEventPayload(bubble));
    }

    @Override
    public void onEnterState(BubbleLifeCycleStateMachine.State newState) {
        triggerGameOverExplosion();
    }
}
