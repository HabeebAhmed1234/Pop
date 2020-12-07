package com.stupidfungames.pop.turrets.turret;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.turrets.turret.TurretStateMachine.State;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;

/**
 * Responsible for actually firing a projectile at a bubble. When prompted to fire this transitions
 * the state machine to FIRING, fires the projectile, and transitions the state machine back to
 * TARGETING
 */
public class TurretFiringEntity extends BaseEntity {

  public static final int TURRETS_FIRING_DELAY_SECONDS = 2;

  private boolean isReadyToFire = true;

  public TurretFiringEntity(BinderEnity parent) {
    super(parent);
    init();
  }

  private void init() {
    get(GameSoundsManager.class).getSound(SoundId.LAZER_BURST).setVolume(0.1f);
  }

  public boolean isReadyToFire() {
    return isReadyToFire;
  }

  public void fire(Sprite target) {
    TurretStateMachine stateMachine = get(TurretStateMachine.class);
    if (stateMachine.getCurrentState() != State.TARGETING) {
      return;
    }
    isReadyToFire = false;

    stateMachine.transitionState(TurretStateMachine.State.FIRING);

    // Fire the bullet
    TurretBulletEntity bulletEntity = new TurretBulletEntity(target, this);
    // Set timer to enable us to fire again after a delay
    engine.registerUpdateHandler(
        new TimerHandler(TURRETS_FIRING_DELAY_SECONDS,
            false,
            new ITimerCallback() {
              @Override
              public void onTimePassed(TimerHandler pTimerHandler) {
                isReadyToFire = true;
              }
            }));
    stateMachine.transitionState(TurretStateMachine.State.TARGETING);
    get(GameSoundsManager.class).getSound(SoundId.LAZER_BURST).play();
  }
}
