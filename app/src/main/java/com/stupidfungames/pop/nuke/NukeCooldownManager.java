package com.stupidfungames.pop.nuke;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.statemachine.BaseStateMachine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

public class NukeCooldownManager extends BaseEntity implements
    BaseStateMachine.Listener<NukeStateMachine.State> {

  public static final int NUKE_COOLDOWN_SECONDS_STARTING = 40;
  public static final int NUKE_COOLDOWN_SECONDS_DELTA_PER_UPGRADE = 3;

  private int nukeCooldownSeconds = NUKE_COOLDOWN_SECONDS_STARTING;

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
    get(NukeStateMachine.class).removeTransitionListener(NukeStateMachine.State.COOLDOWN, this);
  }

  public void onUpgrade(int upgradeLevel) {
    nukeCooldownSeconds =
        NUKE_COOLDOWN_SECONDS_STARTING - upgradeLevel * NUKE_COOLDOWN_SECONDS_DELTA_PER_UPGRADE;
  }

  @Override
  public void onEnterState(NukeStateMachine.State newState) {
    engine.registerUpdateHandler(new TimerHandler(nukeCooldownSeconds, new ITimerCallback() {
      @Override
      public void onTimePassed(TimerHandler pTimerHandler) {
        get(NukeStateMachine.class).transitionState(NukeStateMachine.State.READY);
      }
    }));
  }
}
