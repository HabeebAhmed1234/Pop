package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;

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

    private final HostTurretCallback hostTurretCallback;
    private final TurretStateMachine stateMachine;
    private final GameTexturesManager texturesManager;
    private final GameResources gameResources;

    private boolean isReadyToFire = true;

    public TurretFiringEntity(
            HostTurretCallback hostTurretCallback,
            TurretStateMachine stateMachine,
            GameTexturesManager texturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.hostTurretCallback = hostTurretCallback;
        this.stateMachine = stateMachine;
        this.texturesManager = texturesManager;
        this.gameResources = gameResources;
    }

    public boolean isReadyToFire() {
        return isReadyToFire;
    }

    public void fire(Sprite target) {
        isReadyToFire = false;
        stateMachine.transitionState(TurretStateMachine.State.FIRING);

        // Fire the bullet
        TurretBulletEntity bulletEntity = new TurretBulletEntity(target, hostTurretCallback, texturesManager, gameResources);
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

    }
}
