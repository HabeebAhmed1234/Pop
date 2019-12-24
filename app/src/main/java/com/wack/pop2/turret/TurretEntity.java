package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;

import org.andengine.entity.sprite.Sprite;

/**
 * Contains the components of the TurretEntity. Can be used to set the position and rotation of the
 * turret.
 * Each turret contains a state machine
 */
public class TurretEntity extends BaseEntity implements HostTurretCallback {

    private TurretStateMachine stateMachine;
    private TurretFiringEntity turretFiringEntity;
    private TurretTargetingEntity turretTargetingEntity;
    private Sprite turretBodySprite;
    private Sprite turretCannonSprite;
    private GameTexturesManager texturesManager;

    public TurretEntity(Sprite turretBodySprite,
                        Sprite turretCannonSprite,
                        GameTexturesManager texturesManager,
                        GameResources gameResources) {
        super(gameResources);
        stateMachine = new TurretStateMachine();
        // DEBUG
        stateMachine.transitionState(TurretStateMachine.State.DRAGGING);
        stateMachine.transitionState(TurretStateMachine.State.TARGETING);
        // DEBUG
        this.texturesManager = texturesManager;
        this.turretBodySprite = turretBodySprite;
        this.turretCannonSprite = turretCannonSprite;
        this.turretFiringEntity = new TurretFiringEntity(this, stateMachine, texturesManager, gameResources);
        this.turretTargetingEntity = new TurretTargetingEntity(turretFiringEntity, stateMachine, this, gameResources);
    }

    @Override
    public void setTurretAngle(float angle) {
        turretCannonSprite.setRotation(angle);
    }

    @Override
    public Sprite getTurretBodySprite() {
        return turretBodySprite;
    }

    @Override
    public Sprite getTurretCannonSprite() {
        return turretCannonSprite;
    }
}
