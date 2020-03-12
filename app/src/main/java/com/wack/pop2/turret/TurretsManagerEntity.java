package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.bubblepopper.BubblePopperEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.icontray.GameIconsTrayEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;

/**
 * Holds all the mixins for turret management.
 */
public class TurretsManagerEntity extends BaseEntity {

    private GameTexturesManager texturesManager;
    private TurretsIconEntity turretsIconEntity;
    private TurretEntityCreator turretEntityCreator;
    private TurretsBulletsCollisionManager turretsBulletsCollisionManager;
    private TurretsMutex turretsMutex;

    public TurretsManagerEntity(
            BubblePopperEntity bubblePopperEntity,
            GameTexturesManager gameTextureManager,
            GameIconsTrayEntity gameIconsTray,
            GamePhysicsContactsEntity gamePhysicsContactsEntity,
            GameFontsManager fontsManager,
            GameAreaTouchListenerEntity gameAreaTouchListenerEntity,
            GameSceneTouchListenerEntity sceneTouchListenerEntity,
            GameTexturesManager texturesManager,
            GameSoundsManager soundsManager,
            GameResources gameResources) {
        super(gameResources);
        this.texturesManager = texturesManager;
        this.turretsMutex = new TurretsMutex(gameResources);
        this.turretEntityCreator = new TurretEntityCreator(texturesManager, gameIconsTray, sceneTouchListenerEntity, soundsManager, turretsMutex, gameResources);
        this.turretsIconEntity = new TurretsIconEntity(fontsManager, gameAreaTouchListenerEntity, sceneTouchListenerEntity, gameTextureManager, gameIconsTray, turretEntityCreator, turretsMutex, gameResources);
        this.turretsBulletsCollisionManager = new TurretsBulletsCollisionManager(bubblePopperEntity, gamePhysicsContactsEntity, gameResources);
    }
}
