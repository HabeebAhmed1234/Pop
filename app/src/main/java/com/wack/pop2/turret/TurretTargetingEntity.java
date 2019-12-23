package com.wack.pop2.turret;

import android.util.Log;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.BubblesEntityMatcher;
import com.wack.pop2.GameResources;
import com.wack.pop2.comparators.ClosestDistanceComparator;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

import java.util.Collections;
import java.util.List;

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

    public interface TurretTargetingCallback {
        void setTurretAngle(float angle);
        Sprite getTurretBodySprite();
    }

    private TurretFiringEntity turretFiringEntity;
    private TurretStateMachine stateMachine;
    private TurretTargetingCallback turretTargetingCallback;

    // Set to a bubble when we want to target this bubble
    private IEntity targetBubbleSprite = null;

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
            TurretTargetingCallback turretTargetingCallback,
            GameResources gameResources) {
        super(gameResources);
        this.turretFiringEntity = turretFiringEntity;
        this.stateMachine = stateMachine;
        this.turretTargetingCallback = turretTargetingCallback;
    }

    @Override
    public void onLazyInit() {
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
                Log.d("asdasd", "Fire");
                turretFiringEntity.fire(targetBubbleSprite);
            }
        }
    }

    @Nullable
    private IEntity getBubbleToTarget() {
        List<IEntity> allBubbles = getAllBubbles();
        if (allBubbles == null || allBubbles.isEmpty()) {
            return null;
        }
        Collections.sort(allBubbles, new ClosestDistanceComparator(turretTargetingCallback.getTurretBodySprite()));
        return allBubbles.get(0);
    }

    /**
     * Returns all the bubbles present in the scene
     */
    private List<IEntity> getAllBubbles() {
        return scene.query(new BubblesEntityMatcher());
    }

    private void rotateTurretToTarget(IEntity target) {
        Sprite turretBodySprite = turretTargetingCallback.getTurretBodySprite();
        float turretCenterX = turretBodySprite.getX() + turretBodySprite.getWidth() / 2;
        float turretCenterY = turretBodySprite.getY() + turretBodySprite.getHeight() / 2;
        float angle = getAngle(turretCenterX, turretCenterY, target.getX(), target.getY());
        turretTargetingCallback.setTurretAngle(angle);

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
