package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.fixturedefdata.TurretBodyUserData;
import com.wack.pop2.fixturedefdata.TurretCannonUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

public class TurretEntityCreator extends BaseEntity {

    private GameResources gameResources;
    private GameTexturesManager texturesManager;
    private GameSceneTouchListenerEntity gameSceneTouchListener;
    private TurretsMutex mutex;

    public TurretEntityCreator(
            GameTexturesManager texturesManager,
            GameSceneTouchListenerEntity gameSceneTouchListener,
            TurretsMutex mutex,
            GameResources gameResources) {
        super(gameResources);
        this.gameResources = gameResources;
        this.texturesManager = texturesManager;
        this.gameSceneTouchListener = gameSceneTouchListener;
        this.mutex = mutex;
    }

    public TurretEntity createTurret(float centerX, float centerY) {
        ITextureRegion turretBodyTexture = texturesManager.getTextureRegion(TextureId.BALL);
        ITextureRegion turretCannonTexture = texturesManager.getTextureRegion(TextureId.LINE);

        Sprite turretBodySprite = new Sprite(
                centerX - turretBodyTexture.getWidth() / 2,
                centerY - turretBodyTexture.getHeight() / 2,
                turretBodyTexture,
                vertexBufferObjectManager);
        turretBodySprite.setUserData(new TurretBodyUserData());
        addToScene(turretBodySprite);

        final Sprite turretCannonSprite = new Sprite(
                turretBodySprite.getWidth() / 2,
                turretBodySprite.getHeight() / 2 - turretCannonTexture.getHeight() / 2,
                turretCannonTexture,
                vertexBufferObjectManager);
        turretCannonSprite.setUserData(new TurretCannonUserData());
        turretBodySprite.attachChild(turretCannonSprite);
        turretCannonSprite.setRotationCenter(0f, turretCannonTexture.getHeight() / 2);

        return new TurretEntity(turretBodySprite, turretCannonSprite, mutex, texturesManager, gameSceneTouchListener, gameResources);
    }
}
