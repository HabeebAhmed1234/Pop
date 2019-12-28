package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.BubblePopperEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.utils.ScreenUtils;

/**
 * Holds all the mixins for turret management.
 */
public class TurretsManagerEntity extends BaseEntity {

    private GameTexturesManager texturesManager;
    private TurretEntityCreator turretEntityCreator;
    private TurretsBulletsCollisionManager turretsBulletsCollisionManager;

    private TurretEntity turret1;
    private TurretEntity turret2;

    public TurretsManagerEntity(
            BubblePopperEntity bubblePopperEntity,
            GamePhysicsContactsEntity gamePhysicsContactsEntity,
            GameSceneTouchListenerEntity sceneTouchListenerEntity,
            GameTexturesManager texturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.texturesManager = texturesManager;
        this.turretEntityCreator = new TurretEntityCreator(texturesManager, sceneTouchListenerEntity, gameResources);
        this.turretsBulletsCollisionManager = new TurretsBulletsCollisionManager(bubblePopperEntity, gamePhysicsContactsEntity, gameResources);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        turret1 = turretEntityCreator.createTurret(ScreenUtils.getSreenSize().width / 2, ScreenUtils.getSreenSize().height / 2 );
        turret2 = turretEntityCreator.createTurret(ScreenUtils.getSreenSize().width / 3, ScreenUtils.getSreenSize().height / 3);
    }

    @Override
    public void onDestroy() {
    }
}
