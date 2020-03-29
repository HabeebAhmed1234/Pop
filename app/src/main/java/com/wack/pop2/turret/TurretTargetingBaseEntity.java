package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;

import androidx.annotation.Nullable;

import static com.wack.pop2.turret.TurretsConstants.TARGETING_TIMER_UPDATE_INTERVAL_SECONDS;
import static com.wack.pop2.utils.GeometryUtils.getAngleOfCenters;

/**
 * Manages the targeting for each turret (unique TurretTargetingEntity is made for each turret).
 * If the state machine for the turret enters the TARGETING state then we lock onto a bubble and
 * fire when we are allowed. We fire by calling the TurretFiringEntity.
 *
 *
 */
public class TurretTargetingBaseEntity extends BaseEntity implements BaseStateMachine.Listener<TurretStateMachine.State> {

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

    public TurretTargetingBaseEntity(BinderEnity parent) {
        super(parent);
        get(TurretStateMachine.class).addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        get(TurretStateMachine.class).removeAllStateTransitionListener(this);
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
        maybeAquireNewBubbleTarget();
        engine.registerUpdateHandler(targetingUpdateHandler);
    }

    private void stopTargeting() {
        engine.unregisterUpdateHandler(targetingUpdateHandler);
        maybeStopTargetingBubble();
    }

    /**
     * Called every frame while we are supposed to be targeting
     */
    private void onTargetingUpdate() {
        if (!isInScene(targetBubbleSprite)) {
            maybeStopTargetingBubble();
        }
        if (targetBubbleSprite == null) {
            maybeAquireNewBubbleTarget();
        }
        if (targetBubbleSprite != null) {
            boolean isPointingAtTarget = rotateTurretToTarget(targetBubbleSprite);
            TurretFiringEntity turretFiringEntity = get(TurretFiringEntity.class);
            if (isPointingAtTarget && turretFiringEntity.isReadyToFire()) {
                turretFiringEntity.fire(targetBubbleSprite);
            }
        }
    }

    private void maybeAquireNewBubbleTarget() {
        targetBubbleSprite = getBubbleToTarget();
        BubbleEntityUserData.markTargeted(targetBubbleSprite, true);
    }

    private void maybeStopTargetingBubble() {
        BubbleEntityUserData.markTargeted(targetBubbleSprite, false);
        targetBubbleSprite = null;
    }

    @Nullable
    private Sprite getBubbleToTarget() {
        return TurretUtils.getClosestPoppableBubble(scene, get(HostTurretCallback.class).getTurretBodySprite());
    }

    private boolean rotateTurretToTarget(Sprite target) {
        HostTurretCallback hostTurretCallback = get(HostTurretCallback.class);
        Sprite turretBodySprite = hostTurretCallback.getTurretBodySprite();
        float angle = getAngleOfCenters(turretBodySprite, target);
        return hostTurretCallback.setTurretAngle(angle);
    }
}
