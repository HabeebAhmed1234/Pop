package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.bubblepopper.BubblePopperEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.tooltips.GameTooltipsEntity;

/**
 * Holds all the mixins for turret management.
 */
public class TurretsManagerEntity extends BaseEntity {

    private GameTexturesManager texturesManager;
    private TurretsIconEntity turretsIconEntity;
    private TurretEntityCreator turretEntityCreator;
    private TurretsBulletsCollisionManager turretsBulletsCollisionManager;
    private BulletExplosionsEntity bulletExplosionsEntity;
    private TurretsMutex turretsMutex;

    public TurretsManagerEntity(
            BubblePopperEntity bubblePopperEntity,
            GameTooltipsEntity gameTooltips,
            GameTexturesManager gameTextureManager,
            GameIconsHostTrayEntity gameIconsTray,
            GamePhysicsContactsEntity gamePhysicsContactsEntity,
            GameFontsManager fontsManager,
            GameSceneTouchListenerEntity sceneTouchListenerEntity,
            GameTexturesManager texturesManager,
            GameSoundsManager soundsManager,
            GameResources gameResources) {
        super(gameResources);
        this.texturesManager = texturesManager;
        this.turretsMutex = new TurretsMutex(gameResources);
        this.bulletExplosionsEntity = new BulletExplosionsEntity(texturesManager, gameResources);
        this.turretEntityCreator = new TurretEntityCreator(bulletExplosionsEntity, texturesManager, gameIconsTray, sceneTouchListenerEntity, soundsManager, turretsMutex, gameResources);
        this.turretsIconEntity = new TurretsIconEntity(fontsManager,  sceneTouchListenerEntity, soundsManager, gameTooltips, gameTextureManager, gameIconsTray, turretEntityCreator, turretsMutex, gameResources);
        this.turretsBulletsCollisionManager = new TurretsBulletsCollisionManager(bubblePopperEntity, gamePhysicsContactsEntity, gameResources);
    }
}
