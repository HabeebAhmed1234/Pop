package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Holds all the mixins for turret management.
 */
public class TurretsManagerEntity extends BaseEntity {

    private GameTexturesManager texturesManager;

    public TurretsManagerEntity(GameTexturesManager texturesManager, GameResources gameResources) {
        super(gameResources);
        this.texturesManager = texturesManager;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();

        ITextureRegion turretBodyTexture = texturesManager.getTextureRegion(TextureId.BALL);
        ITextureRegion turretCannonTexture = texturesManager.getTextureRegion(TextureId.LINE);

        Sprite turretbody = new Sprite(
                ScreenUtils.getSreenSize().width / 2 - turretBodyTexture.getWidth() / 2,
                ScreenUtils.getSreenSize().height / 2 - turretBodyTexture.getHeight() / 2,
                turretBodyTexture,
                vertexBufferObjectManager);
        addToScene(turretbody);


        final Sprite turretCannon = new Sprite(
                turretbody.getX() + turretbody.getWidth() / 2,
                turretbody.getY() + turretbody.getHeight() / 2 - turretCannonTexture.getHeight() / 2,
                turretCannonTexture,
                vertexBufferObjectManager);
        addToScene(turretCannon);
        turretCannon.setRotationCenter(0f, turretCannonTexture.getHeight() / 2);

        engine.registerUpdateHandler(new TimerHandler(
                1/30f,
                true,
                new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        turretCannon.setRotation(360 / 30 + turretCannon.getRotation());
                    }
                }));
    }

    /**
     * Returns all the bubbles present in the scene
     */
    private List<IEntity> getAllBubbles() {
        return scene.query(new IEntityMatcher() {
            @Override
            public boolean matches(IEntity pEntity) {
                @Nullable Object userdata = pEntity.getUserData();
                if (userdata != null) {
                    return userdata instanceof BubbleEntityUserData;
                }
                return false;
            }
        });
    }
}
