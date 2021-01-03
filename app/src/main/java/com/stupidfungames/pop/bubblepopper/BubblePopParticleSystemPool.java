package com.stupidfungames.pop.bubblepopper;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopParticleSystemPool.BubblePopParticleSystemPoolParams;
import com.stupidfungames.pop.particles.BaseParticleSystemPool;
import com.stupidfungames.pop.particles.BaseParticleSystemPoolParams;
import com.stupidfungames.pop.resources.textures.TextureId;
import java.util.ArrayList;
import java.util.List;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.particle.emitter.IParticleEmitter;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.IParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.util.color.AndengineColor;

public class BubblePopParticleSystemPool extends
    BaseParticleSystemPool<BubblePopParticleSystemPoolParams> {

  public static final float PARTICLE_DURATION_SECONDS = 0.3f;
  private static final float RATE_MIN = 40;
  private static final float RATE_MAX = 50;
  private static final int PARTICLES_MAX = 90;

  public static class BubblePopParticleSystemPoolParams extends BaseParticleSystemPoolParams {

    private final float radius;
    private final AndengineColor color;

    public BubblePopParticleSystemPoolParams(float x, float y, float radius, AndengineColor color) {
      super(x, y);
      this.radius = radius;
      this.color = color;
    }
  }

  public BubblePopParticleSystemPool(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void updateParticleSystem(
      ParticleSystem particleSystem,
      IParticleEmitter particleEmitter,
      BubblePopParticleSystemPoolParams params) {
    CircleOutlineParticleEmitter circleParticleEmitter = (CircleOutlineParticleEmitter) particleEmitter;
    circleParticleEmitter.setRadius(params.radius);
    particleSystem.removeParticleModifiers(ColorParticleModifier.class);
    particleSystem.addParticleModifier(
        new ColorParticleModifier(0, PARTICLE_DURATION_SECONDS, params.color,
            AndengineColor.WHITE));

  }

  @Override
  protected IParticleEmitter createNewParticleEmitter(BubblePopParticleSystemPoolParams params) {
    return new CircleOutlineParticleEmitter(params.emitterX, params.emitterY, params.radius);
  }

  @Override
  protected List<IParticleModifier> getParticleModifiers() {
    List<IParticleModifier> modifiers = new ArrayList<>();
    modifiers.add(new ScaleParticleModifier(0, PARTICLE_DURATION_SECONDS, 1, 0));
    return modifiers;
  }

  @Override
  protected TextureId getParticleTexture() {
    return TextureId.BULLET;
  }

  @Override
  protected float getRateMin() {
    return RATE_MIN;
  }

  @Override
  protected float getRateMax() {
    return RATE_MAX;
  }

  @Override
  protected int getParticlesMax() {
    return PARTICLES_MAX;
  }

  @Override
  protected float getParticleDurationSeconds() {
    return PARTICLE_DURATION_SECONDS;
  }
}
