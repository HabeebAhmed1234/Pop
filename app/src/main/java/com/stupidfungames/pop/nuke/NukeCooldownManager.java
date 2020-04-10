package com.stupidfungames.pop.nuke;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.statemachine.BaseStateMachine;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import static com.stupidfungames.pop.nuke.NukeConstants.NUKE_COOLDOWN_SECONDS;

public class NukeCooldownManager extends BaseEntity implements BaseStateMachine.Listener<NukeStateMachine.State> {

    public NukeCooldownManager(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {

    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        get(NukeStateMachine.class).addTransitionListener(NukeStateMachine.State.COOLDOWN, this, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        get(NukeStateMachine.class).removeTransitionListener(NukeStateMachine.State.COOLDOWN,this);
    }

    @Override
    public void onEnterState(NukeStateMachine.State newState) {
        engine.registerUpdateHandler(new TimerHandler(NUKE_COOLDOWN_SECONDS, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                get(NukeStateMachine.class).transitionState(NukeStateMachine.State.READY);
            }
        }));
    }
}
