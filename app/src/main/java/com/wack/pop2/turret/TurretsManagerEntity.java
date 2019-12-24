package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.BubblePopperEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;

/**
 * Holds all the mixins for turret management.
 */
public class TurretsManagerEntity extends BaseEntity {

    private GameTexturesManager texturesManager;
    private TurretEntityCreator turretEntityCreator;
    private GameSceneTouchListenerEntity sceneTouchListenerEntity;
    private TurretsBulletsCollisionManager turretsBulletsCollisionManager;

    private TurretEntity turret;

    public TurretsManagerEntity(
            BubblePopperEntity bubblePopperEntity,
            GamePhysicsContactsEntity gamePhysicsContactsEntity,
            GameSceneTouchListenerEntity sceneTouchListenerEntity,
            GameTexturesManager texturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.sceneTouchListenerEntity = sceneTouchListenerEntity;
        this.texturesManager = texturesManager;
        this.turretEntityCreator = new TurretEntityCreator(texturesManager, gameResources);
        this.turretsBulletsCollisionManager = new TurretsBulletsCollisionManager(bubblePopperEntity, gamePhysicsContactsEntity, gameResources);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        turret = turretEntityCreator.createTurret();
    }

    @Override
    public void onDestroy() {
    }
}
