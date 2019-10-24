package com.wack.pop2;

import android.opengl.GLES20;

import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class GameOverSequenceEntity extends BaseEntity {

    private GameTexturesManager texturesManager;
    private GameSoundsManager soundsManager;
    private ShakeCamera camera;

    private Sprite gameOverFadeRedEffect;

    public GameOverSequenceEntity(
            GameTexturesManager texturesManager,
            GameSoundsManager soundsManager,
            ShakeCamera camera,
            GameResources gameResources) {
        super(gameResources);
        this.texturesManager = texturesManager;
        this.soundsManager = soundsManager;
        this.camera = camera;
    }

    @Override
    public void onCreateScene() {
        gameOverFadeRedEffect = new Sprite(0, 0, texturesManager.getTextureRegion(TextureId.GAME_OVER), vertexBufferObjectManager);
        gameOverFadeRedEffect.setAlpha(0);
        ScreenUtils.ScreenSize screenSize = ScreenUtils.getSreenSize();
        gameOverFadeRedEffect.setWidth(screenSize.width);
        gameOverFadeRedEffect.setHeight(screenSize.height);
    }


    public void runGameOverSequence(float x, float y, float bubbleWidth, float bubbleHeight, float scale) {
        final AnimatedSprite explosion = new AnimatedSprite(
                x,
                y,
                (ITiledTextureRegion) texturesManager.getTextureRegion(TextureId.EXPLOSION),
                vertexBufferObjectManager);
        explosion.setX(x+(bubbleWidth/2-explosion.getWidth()/2));
        explosion.setY(y+(bubbleHeight/2-explosion.getHeight()/2));
        explosion.setScale((float) (scale*0.6));
        explosion.animate(80,0);
        scene.attachChild(explosion);

        soundsManager.getSound(SoundId.EXPOSION).play();
        gameOverFadeRedEffect.registerEntityModifier(
                new SequenceEntityModifier(
                        new ParallelEntityModifier(
                                new AlphaModifier(3, 0, 1))));
        gameOverFadeRedEffect.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        scene.attachChild(gameOverFadeRedEffect);
        camera.shake(3, 4);
    }
}
