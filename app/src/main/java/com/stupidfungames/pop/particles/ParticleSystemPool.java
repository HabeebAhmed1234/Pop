package com.stupidfungames.pop.particles;

import java.util.LinkedList;
import java.util.Queue;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.scene.Scene;

/**
 * Pool of particle systems that ensures that they are paused before setting them for reuse. New
 * systems are automatically added to the scene.
 */
public class ParticleSystemPool {

  public interface ParticleSystemFactory {

    ParticleSystem create(float x, float y);
  }

  private final ParticleSystemFactory factory;
  private final Queue<ParticleSystem> pool = new LinkedList();

  public ParticleSystemPool(ParticleSystemFactory factory) {
    this.factory = factory;
  }

  public void recycle(ParticleSystem system) {
    system.setParticlesSpawnEnabled(false);
  }

  public ParticleSystem obtain(Scene scene, float x, float y) {
    if (!pool.isEmpty()) {
      ParticleSystem system = pool.poll();
      system.setX(x);
      system.setY(y);
      system.reset();
      system.setParticlesSpawnEnabled(true);
      return system;
    }
    ParticleSystem system = factory.create(x, y);
    scene.attachChild(system);
    return system;
  }
}
