package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;

import static com.wack.pop2.turret.TurretsConstants.TURRETS_FIRING_DELAY_SECONDS;

/**
 * Responsible for actually firing a projectile at a bubble. When prompted to fire this transitions
 * the state machine to FIRING, fires the projectile, and transitions the state machine back to
 * TARGETING
 */
public class TurretFiringEntity extends BaseEntity {

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
        isReadyToFire = false;

        TurretStateMachine stateMachine = get(TurretStateMachine.class);
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
