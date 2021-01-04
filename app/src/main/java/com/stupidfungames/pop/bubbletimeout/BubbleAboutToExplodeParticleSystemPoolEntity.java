package com.stupidfungames.pop.bubbletimeout;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubbletimeout.BubbleAboutToExplodeParticleSystemPoolEntity.Params;
import com.stupidfungames.pop.particles.BaseParticleSystemPool;
import com.stupidfungames.pop.particles.BaseParticleSystemPoolParams;
import com.stupidfungames.pop.resources.textures.TextureId;
import java.util.ArrayList;
import java.util.List;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.IParticleEmitter;
import org.andengine.entity.particle.initializer.IParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.IParticleModifier;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

class BubbleAboutToExplodeParticleSystemPoolEntity extends
    BaseParticleSystemPool<Params> {

  public BubbleAboutToExplodeParticleSystemPoolEntity(
      BinderEnity parent) {
    super(parent);
  }

  public static class Params extends BaseParticleSystemPoolParams {

    public final Sprite sprite;

    public Params(Sprite sprite) {
      super(sprite.getCenterX(), sprite.getCenterY());
      this.sprite = sprite;
    }
  }

  private static final float RATE_MIN = 40;
  private static final float RATE_MAX = 60;
  private static final int PARTICLES_MAX = 100;
  private static final float PARTICLE_DURATION_SECONDS = 0.8f;
  private static final float PARTICLE_VELOCITY = 150f;

  @Override
  protected void updateParticleSystem(ParticleSystem particleSystem,
      IParticleEmitter particleEmitter, Params params) {
    ((BubbleAboutToExplodeParticleEmitter) particleEmitter).setSprite(params.sprite);
  }

  @Override
  protected TextureId getParticleTexture() {
    return TextureId.STAR;
  }

  @Override
  protected List<IParticleInitializer> getParticleInitializers(Params params) {
    List<IParticleInitializer> initializers = new ArrayList<>();
    initializers.add(
        new VelocityParticleInitializer(-PARTICLE_VELOCITY, PARTICLE_VELOCITY, -PARTICLE_VELOCITY,
            PARTICLE_VELOCITY));
    initializers.add(new ExpireParticleInitializer(PARTICLE_DURATION_SECONDS));
    return initializers;
  }

  @Override
  protected List<IParticleModifier> getParticleModifiers() {
    List<IParticleModifier> modifiers = new ArrayList<>();
    modifiers.add(new RotationParticleModifier(0, PARTICLE_DURATION_SECONDS, 0, 360));
    modifiers.add(new ScaleParticleModifier(0, PARTICLE_DURATION_SECONDS, 1, 0.1f));
    modifiers.add(new AlphaParticleModifier(0, PARTICLE_DURATION_SECONDS, 1f, 0f));
    modifiers.add(new ColorParticleModifier(0, PARTICLE_DURATION_SECONDS, AndengineColor.YELLOW,
        AndengineColor.RED));
    return modifiers;
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

  @Override
  protected IParticleEmitter createNewParticleEmitter(Params params) {
    return new BubbleAboutToExplodeParticleEmitter(params.sprite);
  }
}
