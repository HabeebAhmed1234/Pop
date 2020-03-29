package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;

/**
 * Manages the smooth rotation of a turret cannon given the target angle it should be pointing at
 */
public class TurretCannonRotationManagerEntity extends BaseEntity implements ITimerCallback {

    private static final float ROTATION_UPDATE_INTERVAL_SECONDS = 0.03f;
    private static final float ROTATION_SPEED_PER_INTERVAL = 20.0f;
    private static final float POINTING_AT_TARGET_ANGLE_TOLERANCE = ROTATION_SPEED_PER_INTERVAL;

    private float targetRotation = 0;
    private final TimerHandler smoothRotationUpdater = new TimerHandler(ROTATION_UPDATE_INTERVAL_SECONDS, true, this);

    public TurretCannonRotationManagerEntity(BinderEnity parent) {
        super(parent);
        init();
    }

    private void init() {
        engine.registerUpdateHandler(smoothRotationUpdater);
    }

    @Override
    public void onDestroy() {
        engine.unregisterUpdateHandler(smoothRotationUpdater);
    }

    public boolean setRotation(float angle) {
        targetRotation = angle;
        return isAtTargetAngleRotation(POINTING_AT_TARGET_ANGLE_TOLERANCE);
    }

    @Override
    public void onTimePassed(TimerHandler pTimerHandler) {
        Sprite turretCannon = get(HostTurretCallback.class).getTurretCannonSprite();
        if (turretCannon != null && !isAtTargetAngleRotation()) {
            turretCannon.setRotation(turretCannon.getRotation() + getRotationIncrement());
        }
    }

    private float getRotationIncrement() {
        Sprite turretCannon = get(HostTurretCallback.class).getTurretCannonSprite();
        float remainingRotation = targetRotation - turretCannon.getRotation();
        if (Math.abs(remainingRotation) < ROTATION_SPEED_PER_INTERVAL) {
            return remainingRotation;
        } else {
            return ROTATION_SPEED_PER_INTERVAL * ((remainingRotation < 0) ? -1 : 1);
        }
    }

    private boolean isAtTargetAngleRotation() {
        Sprite turretCannon = get(HostTurretCallback.class).getTurretCannonSprite();
        return turretCannon != null && turretCannon.getRotation() == targetRotation;
    }

    private boolean isAtTargetAngleRotation(float tolerance) {
        Sprite turretCannon = get(HostTurretCallback.class).getTurretCannonSprite();
        return turretCannon != null
            && turretCannon.getRotation() >= (targetRotation - tolerance)
            && turretCannon.getRotation() <= (targetRotation + tolerance);
    }
}
