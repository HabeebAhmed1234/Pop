package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.turret.TurretsConstants.TURRET_DRAGGING_SCALE_MULTIPLIER;

/**
 * Contains the components of the TurretEntity. Can be used to set the position and rotation of the
 * turret.
 * Each turret contains a state machine
 */
public class TurretEntity extends BaseEntity implements HostTurretCallback, BaseStateMachine.Listener<TurretStateMachine.State> {

    private TurretStateMachine stateMachine;
    private TurretFiringEntity turretFiringEntity;
    private TurretTargetingEntity turretTargetingEntity;
    private TurretDraggingManager turretDraggingManager;
    private TurretCannonRotationManagerEntity turretCannonRotationManagerEntity;
    private TurretsMutex mutex;

    private Sprite turretBodySprite;
    private Sprite turretCannonSprite;
    private GameTexturesManager texturesManager;

    public TurretEntity(Sprite turretBodySprite,
                        Sprite turretCannonSprite,
                        TurretsMutex mutex,
                        GameTexturesManager texturesManager,
                        GameIconsHostTrayEntity gameIconsTray,
                        GameSceneTouchListenerEntity gameSceneTouchListener,
                        GameSoundsManager soundsManager,
                        GameResources gameResources) {
        super(gameResources);
        stateMachine = new TurretStateMachine();
        this.texturesManager = texturesManager;
        this.turretBodySprite = turretBodySprite;
        this.turretCannonSprite = turretCannonSprite;
        this.mutex = mutex;
        this.turretFiringEntity = new TurretFiringEntity(this, stateMachine, texturesManager, soundsManager, gameResources);
        this.turretTargetingEntity = new TurretTargetingEntity(turretFiringEntity, stateMachine, this, gameResources);
        this.turretDraggingManager = new TurretDraggingManager(gameIconsTray, gameSceneTouchListener, mutex, stateMachine, this, gameResources);
        this.turretCannonRotationManagerEntity = new TurretCannonRotationManagerEntity(turretCannonSprite, gameResources);

        init();
    }

    @Override
    public boolean setTurretAngle(float angle) {
        return turretCannonRotationManagerEntity.setRotation(angle);
    }

    @Override
    public Sprite getTurretBodySprite() {
        return turretBodySprite;
    }

    @Override
    public Sprite getTurretCannonSprite() {
        return turretCannonSprite;
    }

    @Override
    public void setTurretPosition(float x, float y) {
        turretBodySprite.setX(x - turretBodySprite.getWidthScaled() / 2);
        turretBodySprite.setY(y - turretBodySprite.getHeightScaled() / 2);
    }

    public void forceStartDragging(float pointerX, float pointerY) {
        turretDraggingManager.forceStartDragging(pointerX, pointerY);
    }

    private void init() {
        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onEnterState(TurretStateMachine.State newState) {
        AndengineColor color = AndengineColor.WHITE;
        switch (newState) {
            case DRAGGING:
                color = AndengineColor.YELLOW;
                break;
            case FIRING:
                color = AndengineColor.RED;
                break;
            case DOCKED:
                color = AndengineColor.TRANSPARENT;
                break;
            case TARGETING:
                color = AndengineColor.GREEN;
                break;
        }

        turretBodySprite.setColor(color);
        turretCannonSprite.setColor(color);

        updateScale(newState);
    }

    private void updateScale(TurretStateMachine.State state) {
        if (state == TurretStateMachine.State.DRAGGING) {
            turretBodySprite.setScale(TURRET_DRAGGING_SCALE_MULTIPLIER);
        } else {
            turretBodySprite.setScale(1);
        }
    }
}
