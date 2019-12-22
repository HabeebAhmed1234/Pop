package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

import org.andengine.entity.sprite.Sprite;

/**
 * Contains the components of the TurretEntity. Can be used to set the position and rotation of the
 * turret.
 * Each turret contains a state machine
 */
public class TurretEntity extends BaseEntity implements TurrentTargetingEntity.TurretTargetingCallback {

    private TurretStateMachine stateMachine;
    private TurretFiringEntity turretFiringEntity;
    private TurrentTargetingEntity turrentTargetingEntity;
    private Sprite turretBodySprite;
    private Sprite turretCannonSprite;

    public TurretEntity(Sprite turretBodySprite,
                        Sprite turretCannonSprite,
                        GameResources gameResources) {
        super(gameResources);
        stateMachine = new TurretStateMachine();
        turretFiringEntity = new TurretFiringEntity(gameResources);
        turrentTargetingEntity = new TurrentTargetingEntity(turretFiringEntity, stateMachine, this, gameResources);
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
