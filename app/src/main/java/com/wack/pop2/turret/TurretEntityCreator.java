package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.TurretBodyUserData;
import com.wack.pop2.fixturedefdata.TurretCannonUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

public class TurretEntityCreator extends BaseEntity {

    private GameResources gameResources;
    private GameTexturesManager texturesManager;

    public TurretEntityCreator(GameTexturesManager texturesManager, GameResources gameResources) {
        super(gameResources);
        this.gameResources = gameResources;
        this.texturesManager = texturesManager;
    }

    public TurretEntity createTurret() {
        ITextureRegion turretBodyTexture = texturesManager.getTextureRegion(TextureId.BALL);
        ITextureRegion turretCannonTexture = texturesManager.getTextureRegion(TextureId.LINE);

        Sprite turretBodySprite = new Sprite(
                ScreenUtils.getSreenSize().width / 2 - turretBodyTexture.getWidth() / 2,
                ScreenUtils.getSreenSize().height / 2 - turretBodyTexture.getHeight() / 2,
                turretBodyTexture,
                vertexBufferObjectManager);
        turretBodySprite.setUserData(new TurretBodyUserData());
        addToScene(turretBodySprite);

        final Sprite turretCannonSprite = new Sprite(
                turretBodySprite.getX() + turretBodySprite.getWidth() / 2,
                turretBodySprite.getY() + turretBodySprite.getHeight() / 2 - turretCannonTexture.getHeight() / 2,
                turretCannonTexture,
                vertexBufferObjectManager);
        turretCannonSprite.setUserData(new TurretCannonUserData());
        addToScene(turretCannonSprite);
        turretCannonSprite.setRotationCenter(0f, turretCannonTexture.getHeight() / 2);

        return new TurretEntity(turretBodySprite, turretCannonSprite, texturesManager, gameResources);
    }
}
