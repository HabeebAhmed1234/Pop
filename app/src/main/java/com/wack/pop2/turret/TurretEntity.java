package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

import org.andengine.entity.sprite.Sprite;

/**
 * Contains the components of the TurretEntity. Can be used to set the position and rotation of the
 * turret.
 * Each turret contains a state machine
 */
public class TurretEntity extends BaseEntity implements TurretTargetingEntity.TurretTargetingCallback {

    private TurretStateMachine stateMachine;
    private TurretFiringEntity turretFiringEntity;
    private TurretTargetingEntity turretTargetingEntity;
    private Sprite turretBodySprite;
    private Sprite turretCannonSprite;

    public TurretEntity(Sprite turretBodySprite,
                        Sprite turretCannonSprite,
                        GameResources gameResources) {
        super(gameResources);
        stateMachine = new TurretStateMachine();
        // DEBUG
        stateMachine.transitionState(TurretStateMachine.State.DRAGGING);
        stateMachine.transitionState(TurretStateMachine.State.TARGETING);
        // DEBUG
        turretFiringEntity = new TurretFiringEntity(stateMachine, gameResources);
        turretTargetingEntity = new TurretTargetingEntity(turretFiringEntity, stateMachine, this, gameResources);
        this.turretBodySprite = turretBodySprite;
        this.turretCannonSprite = turretCannonSprite;
    }

    @Override
    public void setTurretAngle(float angle) {
        turretCannonSprite.setRotation(angle);
    }

    @Override
    public Sprite getTurretBodySprite() {
        return turretBodySprite;
    }
}
