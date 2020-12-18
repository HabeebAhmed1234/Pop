package com.stupidfungames.pop.particles;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.pool.ItemPool;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import java.util.List;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.BaseParticleEmitter;
import org.andengine.entity.particle.emitter.IParticleEmitter;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.IParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.IParticleModifier;

public abstract class BaseParticleSystemPool<P extends BaseParticleSystemPoolParams> extends
    ItemPool<ParticleSystem, P> {

  public BaseParticleSystemPool(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected ItemInitializer getInitializer() {
    return new ItemInitializer<ParticleSystem, P>() {
      @Override
      public ParticleSystem createNew(P params) {
        IParticleEmitter particleEmitter = createNewParticleEmitter(params);
        ParticleSystem particleSystem = new BatchedSpriteParticleSystem(
            particleEmitter,
            getRateMin(),
            getRateMax(),
            getParticlesMax(),
            get(GameTexturesManager.class).getTextureRegion(getParticleTexture()),
            vertexBufferObjectManager);

        List<IParticleInitializer> initializers = getParticleInitializers();
        if (initializers != null && !initializers.isEmpty()) {
          for (IParticleInitializer initializer : initializers) {
            particleSystem.addParticleInitializer(initializer);
          }
        }

        // add baseline set of initializers
        particleSystem.addParticleInitializer(new ExpireParticleInitializer(getParticleDuration()));

        List<IParticleModifier> modifiers = getParticleModifiers();
        if (modifiers != null && !modifiers.isEmpty()) {
          for (IParticleModifier modifier : modifiers) {
            particleSystem.addParticleModifier(modifier);
          }
        }

        // add baseline set of modifiers
        particleSystem
            .addParticleModifier(new AlphaParticleModifier(0, getParticleDuration(), 1, 0));

        scene.attachChild(particleSystem);
        return particleSystem;
      }

      @Override
      public void update(ParticleSystem system, P params) {
        BaseParticleEmitter particleEmitter = (BaseParticleEmitter) system.getParticleEmitter();
        particleEmitter.setCenter(params.emitterX, params.emitterY);
        updateParticleSystem(system, particleEmitter, params);
        system.setParticlesSpawnEnabled(true);
      }

      @Override
      public void onRecycle(ParticleSystem item) {
        item.setParticlesSpawnEnabled(false);
      }

      @Override
      public void destroy(ParticleSystem item) {

      }
    };
  }

  protected IParticleEmitter createNewParticleEmitter(P params) {
    return new PointParticleEmitter(params.emitterX, params.emitterY);
  }

  protected List<IParticleInitializer> getParticleInitializers() {
    return null;
  }

  protected List<IParticleModifier> getParticleModifiers() {
    return null;
  }

  /**
   * Called when we are updating an existing emitter to a new obtain call to the pool.
   */
  protected abstract void updateParticleSystem(
      ParticleSystem particleSystem,
      IParticleEmitter particleEmitter,
      P params);

  protected abstract TextureId getParticleTexture();

  protected abstract float getRateMin();

  protected abstract float getRateMax();

  protected abstract int getParticlesMax();

  protected abstract float getParticleDuration();
}
