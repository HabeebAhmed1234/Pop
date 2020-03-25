package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import static com.wack.pop2.utils.GeometryUtils.initSpriteDimens;
import static com.wack.pop2.utils.GeometryUtils.initSpriteDimensCenterPos;

public class TurretEntityCreator extends BaseEntity {

    private static final int TURRET_BODY_SIZE_DP = 88;

    private static final int TURRET_CANNON_HEIGHT_DP = 12;
    private static final int TURRET_CANNON_LENGTH_DP = 32;

    private GameResources gameResources;
    private BulletExplosionsEntity bulletExplosionsEntity;
    private GameTexturesManager texturesManager;
    private GameSceneTouchListenerEntity gameSceneTouchListener;
    private GameIconsHostTrayEntity gameIconsTray;
    private GameSoundsManager soundsManager;
    private TurretsMutex mutex;

    public TurretEntityCreator(
            BulletExplosionsEntity bulletExplosionsEntity,
            GameTexturesManager texturesManager,
            GameIconsHostTrayEntity gameIconsTray,
            GameSceneTouchListenerEntity gameSceneTouchListener,
            GameSoundsManager soundsManager,
            TurretsMutex mutex,
            GameResources gameResources) {
        super(gameResources);
        this.gameResources = gameResources;
        this.bulletExplosionsEntity = bulletExplosionsEntity;
        this.texturesManager = texturesManager;
        this.gameSceneTouchListener = gameSceneTouchListener;
        this.gameIconsTray = gameIconsTray;
        this.soundsManager = soundsManager;
        this.mutex = mutex;
    }

    public TurretEntity createTurret(int centerX, int centerY) {
        ITextureRegion turretBodyTexture = texturesManager.getTextureRegion(TextureId.BALL);
        ITextureRegion turretCannonTexture = texturesManager.getTextureRegion(TextureId.LINE);

        Sprite turretBodySprite = new Sprite(
                0,
                0,
                turretBodyTexture,
                vertexBufferObjectManager);

        initSpriteDimensCenterPos(hostActivity.getActivityContext(), turretBodySprite, centerX, centerY, TURRET_BODY_SIZE_DP);

        addToScene(turretBodySprite);

        final Sprite turretCannonSprite = new Sprite(
                0,
                0,
                turretCannonTexture,
                vertexBufferObjectManager);

        int cannonHeightPx = ScreenUtils.dpToPx(TURRET_CANNON_HEIGHT_DP, hostActivity.getActivityContext());

        initSpriteDimens(
                turretCannonSprite,
                (int) turretBodySprite.getWidth() / 2,
                (int) turretBodySprite.getHeight() / 2 - cannonHeightPx / 2,
                ScreenUtils.dpToPx(TURRET_CANNON_LENGTH_DP, hostActivity.getActivityContext()),
                cannonHeightPx);

        turretBodySprite.attachChild(turretCannonSprite);
        turretCannonSprite.setRotationCenter(0f, cannonHeightPx / 2);

        return new TurretEntity(turretBodySprite, turretCannonSprite, mutex, bulletExplosionsEntity, texturesManager, gameIconsTray, gameSceneTouchListener, soundsManager, gameResources);
    }
}
