package com.stupidfungames.pop;

import static com.stupidfungames.pop.analytics.Events.GAME_OVER_PROGRESS;

import android.content.Context;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.androidui.music.MusicPlayer;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.difficulty.GameDifficultyEntity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameOverExplosionEventPayload;
import com.stupidfungames.pop.globalpoppedbubbles.GlobalPoppedBubbleManager;
import com.stupidfungames.pop.hudentities.ScoreHudEntity;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.IParticleEmitter;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.util.color.AndengineColor;

public class GameOverSequenceEntity extends BaseEntity {

  private static final float EXPLOSION_DURATION_SECONDS = 3f;
  private static final float FADE_OUT_TIME = 1f;
  private final float PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE = 1000;
  private static final float RATE_MIN = 50;
  private static final float RATE_MAX = 100;
  private static final int PARTICLES_MAX = 1000;

  private boolean isGameOverStarted = false;

  private EventBus.Subscriber gameOverExplosionSubscriber = new EventBus.Subscriber() {
    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
      runGameOverSequenceWithExplosion((GameOverExplosionEventPayload) payload);
    }
  };

  public GameOverSequenceEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    EventBus.get().subscribe(GameEvent.GAME_OVER_ON_EXPLOSION_EVENT, gameOverExplosionSubscriber);
  }

  @Override
  public void onDestroy() {
    EventBus.get().unSubscribe(GameEvent.GAME_OVER_ON_EXPLOSION_EVENT, gameOverExplosionSubscriber);
  }

  private void runGameOverSequenceWithExplosion(GameOverExplosionEventPayload payload) {
    if (isGameOverStarted) {
      return;
    }
    isGameOverStarted = true;
    IParticleEmitter emitter = new PointParticleEmitter(
        payload.explosionLocationCenter.x,
        payload.explosionLocationCenter.y);
    Vec2Pool.recycle(payload.explosionLocationCenter);
    final ParticleSystem explosionParticleSystem = new BatchedSpriteParticleSystem(
        emitter,
        RATE_MIN,
        RATE_MAX,
        PARTICLES_MAX,
        get(GameTexturesManager.class).getTextureRegion(TextureId.BULLET),
        vertexBufferObjectManager);

    explosionParticleSystem.addParticleInitializer(new VelocityParticleInitializer(
        -PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
        PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
        -PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
        PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE));
    explosionParticleSystem.addParticleInitializer(new ExpireParticleInitializer(FADE_OUT_TIME));
    explosionParticleSystem.addParticleModifier(
        new ColorParticleModifier(0, FADE_OUT_TIME, AndengineColor.WHITE, AndengineColor.RED));
    explosionParticleSystem.addParticleModifier(new AlphaParticleModifier(0, FADE_OUT_TIME, 1, 0));

    scene.attachChild(explosionParticleSystem);

    get(GameSoundsManager.class).getSound(SoundId.EXPOSION).play();
    get(ShakeCamera.class).shake(3, 4);

    scene.registerUpdateHandler(new TimerHandler(EXPLOSION_DURATION_SECONDS, new ITimerCallback() {
      @Override
      public void onTimePassed(TimerHandler pTimerHandler) {
        explosionParticleSystem.setParticlesSpawnEnabled(false);
        onGameOver();
      }
    }));
  }

  private void onGameOver() {
    Context context = get(Context.class);
    context.startActivity(
        GameOverActivity.newIntent(
            context,
            get(ScoreHudEntity.class).getScore(),
            GlobalPoppedBubbleManager.getInstance().getGlobalBubblesPoppedCached(context),
            get(BubblePopperEntity.class).getBubblesPoppedCount(),
            get(GameSaver.class).fabricateSaveGame()));
    MusicPlayer.get().onLeaveGameActivity();
    hostActivity.finish();

    Logger.logSelect(
        hostActivity.getContext(),
        GAME_OVER_PROGRESS,
        get(GameDifficultyEntity.class).getGameProgress());
  }
}
