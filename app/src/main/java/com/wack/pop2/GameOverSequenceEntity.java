package com.wack.pop2;

import android.content.Context;
import android.opengl.GLES20;

import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameOverExplosionEventPayload;
import com.wack.pop2.hudentities.ScoreHudEntity;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class GameOverSequenceEntity extends BaseEntity {

    private Context context;
    private ScoreHudEntity scoreHudEntity;
    private GameTexturesManager texturesManager;
    private GameSoundsManager soundsManager;
    private GameAnimationManager animationManager;
    private ShakeCamera camera;

    private Sprite gameOverFadeRedEffect;

    private EventBus.Subscriber gameOverExplosionSubscriber = new EventBus.Subscriber() {
        @Override
        public void onEvent(GameEvent event, EventPayload payload) {
                runGameOverSequenceWithExplosion((GameOverExplosionEventPayload) payload);
        }
    };

    private EventBus.Subscriber gameOverTimeoutSubscriber = new EventBus.Subscriber() {
        @Override
        public void onEvent(GameEvent event, EventPayload payload) {
            onGameover();
        }
    };

    public GameOverSequenceEntity(
            Context context,
            ScoreHudEntity scoreHudEntity,
            GameTexturesManager texturesManager,
            GameSoundsManager soundsManager,
            GameAnimationManager animationManager,
            ShakeCamera camera,
            GameResources gameResources) {
        super(gameResources);
        this.context = context;
        this.scoreHudEntity = scoreHudEntity;
        this.texturesManager = texturesManager;
        this.soundsManager = soundsManager;
        this.animationManager = animationManager;
        this.camera = camera;
    }

    @Override
    public void onCreateScene() {
        gameOverFadeRedEffect = new Sprite(
                0,
                0,
                texturesManager.getTextureRegion(TextureId.GAME_OVER),
                vertexBufferObjectManager);
        gameOverFadeRedEffect.setAlpha(0);
        ScreenUtils.ScreenSize screenSize = ScreenUtils.getSreenSize();
        gameOverFadeRedEffect.setWidth(screenSize.width);
        gameOverFadeRedEffect.setHeight(screenSize.height);

        EventBus.get().subscribe(GameEvent.GAME_OVER_ON_EXPLOSION_EVENT, gameOverExplosionSubscriber);
        EventBus.get().subscribe(GameEvent.GAME_TIMEOUT_EVENT, gameOverTimeoutSubscriber);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.GAME_OVER_ON_EXPLOSION_EVENT, gameOverExplosionSubscriber);
        EventBus.get().unSubscribe(GameEvent.GAME_TIMEOUT_EVENT, gameOverTimeoutSubscriber);
    }

    private void startGameOverEffectAnimation() {
        animationManager.startModifier(
                gameOverFadeRedEffect,
                new ParallelEntityModifier(new AlphaModifier(3, 0, 1)),
                new GameAnimationManager.AnimationListener() {
                    @Override
                    public void onFinished() {
                        onGameover();
                    }
                });
    }

    private void runGameOverSequenceWithExplosion(GameOverExplosionEventPayload payload) {
        final AnimatedSprite explosion = new AnimatedSprite(
                payload.x,
                payload.y,
                (ITiledTextureRegion) texturesManager.getTextureRegion(TextureId.EXPLOSION),
                vertexBufferObjectManager);
        explosion.setX(payload.x+(payload.bubbleWidth/2-explosion.getWidth()/2));
        explosion.setY(payload.y+(payload.bubbleHeight/2-explosion.getHeight()/2));
        explosion.setScale((float) (payload.scale*0.6));
        explosion.animate(80,0);
        scene.attachChild(explosion);

        soundsManager.getSound(SoundId.EXPOSION).play();
        startGameOverEffectAnimation();
        gameOverFadeRedEffect.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        scene.attachChild(gameOverFadeRedEffect);
        camera.shake(3, 4);
    }

    private void onGameover() {
        context.startActivity(GameOverActivity.newIntent(context, scoreHudEntity.getScore()));
        hostActivity.finish();
    }
}
