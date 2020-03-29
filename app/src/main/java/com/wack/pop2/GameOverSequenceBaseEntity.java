package com.wack.pop2;

import android.content.Context;
import android.opengl.GLES20;

import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameOverExplosionEventPayload;
import com.wack.pop2.hudentities.ScoreHudEntity;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class GameOverSequenceBaseEntity extends BaseEntity {

    private Sprite gameOverFadeRedEffect;
    private boolean isGameOverStarted = false;

    private EventBus.Subscriber gameOverExplosionSubscriber = new EventBus.Subscriber() {
        @Override
        public void onEvent(GameEvent event, EventPayload payload) {
                runGameOverSequenceWithExplosion((GameOverExplosionEventPayload) payload);
        }
    };

    private EventBus.Subscriber gameOverTimeoutSubscriber = new EventBus.Subscriber() {
        @Override
        public void onEvent(GameEvent event, EventPayload payload) {
            if (isGameOverStarted) return;
            isGameOverStarted = true;
            onGameover();
        }
    };

    public GameOverSequenceBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        gameOverFadeRedEffect = new Sprite(
                0,
                0,
                get(GameTexturesManager.class).getTextureRegion(TextureId.GAME_OVER),
                vertexBufferObjectManager);
        gameOverFadeRedEffect.setAlpha(0);
        ScreenUtils.ScreenSize screenSize = ScreenUtils.getSreenSize();
        gameOverFadeRedEffect.setWidth(screenSize.widthPx);
        gameOverFadeRedEffect.setHeight(screenSize.heightPx);

        EventBus.get().subscribe(GameEvent.GAME_OVER_ON_EXPLOSION_EVENT, gameOverExplosionSubscriber);
        EventBus.get().subscribe(GameEvent.GAME_TIMEOUT_EVENT, gameOverTimeoutSubscriber);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.GAME_OVER_ON_EXPLOSION_EVENT, gameOverExplosionSubscriber);
        EventBus.get().unSubscribe(GameEvent.GAME_TIMEOUT_EVENT, gameOverTimeoutSubscriber);
    }

    private void runGameOverSequenceWithExplosion(GameOverExplosionEventPayload payload) {
        if (isGameOverStarted) return;
        isGameOverStarted = true;
        final Sprite bubble  = payload.bubble;
        final AnimatedSprite explosion = new AnimatedSprite(
                bubble.getX(),
                bubble.getY(),
                (ITiledTextureRegion) get(GameTexturesManager.class).getTextureRegion(TextureId.EXPLOSION),
                vertexBufferObjectManager);
        explosion.setX(bubble.getX()+(bubble.getWidthScaled()/2-explosion.getWidth()/2));
        explosion.setY(bubble.getY()+(bubble.getHeightScaled()/2-explosion.getHeight()/2));
        explosion.setScale((float) (bubble.getScaleX()*0.6));
        explosion.animate(80,0);
        scene.attachChild(explosion);

        get(GameSoundsManager.class).getSound(SoundId.EXPOSION).play();
        startGameOverEffectAnimation();
        gameOverFadeRedEffect.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        scene.attachChild(gameOverFadeRedEffect);
        get(ShakeCamera.class).shake(3, 4);

        scene.postRunnable(new Runnable() {
            @Override
            public void run() {
                removeFromScene(bubble);
            }
        });
    }

    private void startGameOverEffectAnimation() {
        get(GameAnimationManager.class).startModifier(
                gameOverFadeRedEffect,
                new ParallelEntityModifier(new AlphaModifier(3, 0, 1)),
                new GameAnimationManager.AnimationListener() {
                    @Override
                    public void onFinished() {
                        onGameover();
                    }
                });
    }

    private void onGameover() {
        Context context = get(Context.class);
        context.startActivity(GameOverActivity.newIntent(context, get(ScoreHudEntity.class).getScore()));
        hostActivity.finish();
    }
}
