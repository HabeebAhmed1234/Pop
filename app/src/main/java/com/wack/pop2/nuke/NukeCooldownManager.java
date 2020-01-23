package com.wack.pop2.nuke;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import static com.wack.pop2.nuke.NukeConstants.NUKE_COOLDOWN_SECONDS;

public class NukeCooldownManager extends BaseEntity implements BaseStateMachine.Listener<NukeStateMachine.State> {

    private NukeStateMachine stateMachine;

    public NukeCooldownManager(NukeStateMachine stateMachine, GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        stateMachine.addTransitionListener(NukeStateMachine.State.COOLDOWN, this, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stateMachine.removeTransitionListener(NukeStateMachine.State.COOLDOWN,this);
    }

    @Override
    public void onEnterState(NukeStateMachine.State newState) {
        engine.registerUpdateHandler(new TimerHandler(NUKE_COOLDOWN_SECONDS, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                stateMachine.transitionState(NukeStateMachine.State.READY);
            }
        }));
    }
}
