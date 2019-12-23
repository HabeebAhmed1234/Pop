package com.wack.pop2.turret;

import android.util.Log;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;

import static com.wack.pop2.turret.TurretsConstants.TURRETS_FIRING_DELAY_SECONDS;

/**
 * Responsible for actually firing a projectile at a bubble. When prompted to fire this transitions
 * the state machine to FIRING, fires the projectile, and transitions the state machine back to
 * TARGETING
 */
public class TurretFiringEntity extends BaseEntity {

    private final TurretStateMachine stateMachine;

    private boolean isReadyToFire = true;

    public TurretFiringEntity(TurretStateMachine stateMachine, GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
    }

    public boolean isReadyToFire() {
        return isReadyToFire;
    }

    public void fire(IEntity target) {
        isReadyToFire = false;
        stateMachine.transitionState(TurretStateMachine.State.FIRING);
        final float currentTime = engine.getSecondsElapsedTotal();
        engine.registerUpdateHandler(
                new TimerHandler(TURRETS_FIRING_DELAY_SECONDS,
                        false,
                        new ITimerCallback() {
                            @Override
                            public void onTimePassed(TimerHandler pTimerHandler) {
                                float secondsSinceFired = engine.getSecondsElapsedTotal() - currentTime;
                                Log.d("asdasd", "seconds since firing = " + secondsSinceFired + " resetting");
                                isReadyToFire = true;
                            }
                        }));
        stateMachine.transitionState(TurretStateMachine.State.TARGETING);
    }
}
