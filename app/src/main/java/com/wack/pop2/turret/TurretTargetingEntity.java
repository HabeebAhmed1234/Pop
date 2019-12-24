package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

import androidx.annotation.Nullable;

import static com.wack.pop2.turret.TurretsConstants.TARGETING_TIMER_UPDATE_INTERVAL_SECONDS;

/**
 * Manages the targeting for each turret (unique TurretTargetingEntity is made for each turret).
 * If the state machine for the turret enters the TARGETING state then we lock onto a bubble and
 * fire when we are allowed. We fire by calling the TurretFiringEntity.
 *
 *
 */
public class TurretTargetingEntity extends BaseEntity implements BaseStateMachine.Listener<TurretStateMachine.State> {

    private TurretFiringEntity turretFiringEntity;
    private TurretStateMachine stateMachine;
    private HostTurretCallback hostTurretCallback;

    // Set to a bubble when we want to target this bubble
    private Sprite targetBubbleSprite = null;

    private TimerHandler targetingUpdateHandler = new TimerHandler(
            TARGETING_TIMER_UPDATE_INTERVAL_SECONDS,
            true,
            new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    onTargetingUpdate();
                }
            });

    public TurretTargetingEntity(
            TurretFiringEntity turretFiringEntity,
            TurretStateMachine stateMachine,
            HostTurretCallback hostTurretCallback,
            GameResources gameResources) {
        super(gameResources);
        this.turretFiringEntity = turretFiringEntity;
        this.stateMachine = stateMachine;
        this.hostTurretCallback = hostTurretCallback;

        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        stateMachine.removeAllStateTransitionListener(this);
    }

    @Override
    public void onEnterState(TurretStateMachine.State newState) {
        if (newState == TurretStateMachine.State.TARGETING) {
            startTargeting();
        } else {
            stopTargeting();
        }
    }

    private void startTargeting() {
        targetBubbleSprite = getBubbleToTarget();
        engine.registerUpdateHandler(targetingUpdateHandler);
    }

    private void stopTargeting() {
        engine.unregisterUpdateHandler(targetingUpdateHandler);
        targetBubbleSprite = null;
    }

    /**
     * Called every frame while we are supposed to be targeting
     */
    private void onTargetingUpdate() {
        if (!isInScene(targetBubbleSprite)) {
            targetBubbleSprite = null;
        }
        if (targetBubbleSprite == null) {
            targetBubbleSprite = getBubbleToTarget();
        }
        if (targetBubbleSprite != null) {
            rotateTurretToTarget(targetBubbleSprite);
            if (turretFiringEntity.isReadyToFire()) {
                turretFiringEntity.fire(targetBubbleSprite);
            }
        }
    }

    @Nullable
    private Sprite getBubbleToTarget() {
        return TurretUtils.getClosestPoppableBubble(scene, hostTurretCallback.getTurretBodySprite());
    }

    private void rotateTurretToTarget(IEntity target) {
        Sprite turretBodySprite = hostTurretCallback.getTurretBodySprite();
        float turretCenterX = turretBodySprite.getX() + turretBodySprite.getWidth() / 2;
        float turretCenterY = turretBodySprite.getY() + turretBodySprite.getHeight() / 2;
        float angle = getAngle(turretCenterX, turretCenterY, target.getX(), target.getY());
        hostTurretCallback.setTurretAngle(angle);

    }

    private static float getAngle(float x1, float y1, float x2, float y2) {
        double theta = Math.atan2(y2 - y1, x2 - x1);
        double angle = Math.toDegrees(theta);
        if (angle < 0) {
            angle += 360;
        }
        return (float) angle;
    }
}
