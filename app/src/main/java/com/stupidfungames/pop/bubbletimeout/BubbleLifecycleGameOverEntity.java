package com.stupidfungames.pop.bubbletimeout;

import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameOverExplosionEventPayload;
import com.stupidfungames.pop.statemachine.BaseStateMachine;

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
        bubble = null;
        stateMachine.removeTransitionListener(BubbleLifeCycleStateMachine.State.EXPLODING, this);
    }

    @Override
    public void onEnterState(BubbleLifeCycleStateMachine.State newState) {
        if (newState == BubbleLifeCycleStateMachine.State.EXPLODING) {
            triggerGameOverExplosion();
        }
    }

    private void triggerGameOverExplosion() {
        EventBus.get().sendEvent(
                GameEvent.GAME_OVER_ON_EXPLOSION_EVENT,
                new GameOverExplosionEventPayload(bubble));
    }
}
