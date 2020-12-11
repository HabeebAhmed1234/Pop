package com.stupidfungames.pop.bubblepopper;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopParticleSystemPool.BubblePopParticleSystemParams;
import com.stupidfungames.pop.pool.ItemPool;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.BaseCircleParticleEmitter;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.particle.emitter.IParticleEmitter;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.util.color.AndengineColor;

public class BubblePopParticleSystemPool extends
    ItemPool<ParticleSystem, BubblePopParticleSystemParams> {

  public static final float PARTICLE_DURATION_SECONDS = 0.3f;

  private static final float RATE_MIN = 40;
  private static final float RATE_MAX = 50;
  private static final int PARTICLES_MAX = 90;

  public static class BubblePopParticleSystemParams {

    private final float x, y;
    private final float radius;
    private final AndengineColor color;

    public BubblePopParticleSystemParams(float x, float y, float radius,
        AndengineColor color) {
      this.x = x;
      this.y = y;
      this.radius = radius;
      this.color = color;
    }
  }

  private final ItemInitializer itemInitializer = new ItemInitializer<ParticleSystem, BubblePopParticleSystemParams>() {

    @Override
    public ParticleSystem createNew(BubblePopParticleSystemParams params) {
      IParticleEmitter emitter = new CircleOutlineParticleEmitter(params.x, params.y,
          params.radius);
      ParticleSystem particleSystem = new BatchedSpriteParticleSystem(
          emitter,
          RATE_MIN,
          RATE_MAX,
          PARTICLES_MAX,
          get(GameTexturesManager.class).getTextureRegion(TextureId.BULLET),
          vertexBufferObjectManager);

      particleSystem
          .addParticleInitializer(new ExpireParticleInitializer(PARTICLE_DURATION_SECONDS));
      particleSystem
          .addParticleModifier(new AlphaParticleModifier(0, PARTICLE_DURATION_SECONDS, 1, 0));
      particleSystem
          .addParticleModifier(new ScaleParticleModifier(0, PARTICLE_DURATION_SECONDS, 1, 0));
      scene.attachChild(particleSystem);
      return particleSystem;
    }

    @Override
    public void update(ParticleSystem system, BubblePopParticleSystemParams params) {
      BaseCircleParticleEmitter particleEmitter = ((BaseCircleParticleEmitter) system
          .getParticleEmitter());
      particleEmitter.setCenter(params.x, params.y);
      particleEmitter.setRadius(params.radius);
      system.removeParticleModifiers(ColorParticleModifier.class);
      system.addParticleModifier(
          new ColorParticleModifier(0, PARTICLE_DURATION_SECONDS, params.color,
              AndengineColor.WHITE));
      system.setParticlesSpawnEnabled(true);
    }

    @Override
    public void onRecycle(ParticleSystem item) {
      item.setParticlesSpawnEnabled(false);
    }

    @Override
    public void destroy(ParticleSystem item) {
      /** NOOP **/
    }
  };

  public BubblePopParticleSystemPool(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected ItemInitializer getInitializer() {
    return itemInitializer;
  }
}
