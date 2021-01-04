package com.stupidfungames.pop.bubbletimeout;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State;
import com.stupidfungames.pop.statemachine.BaseStateMachine;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.sprite.Sprite;

class BubbleAboutToExplodeParticleSystemLifecycleController extends BaseEntity implements
    BubbleLifecycleController,
    BaseStateMachine.Listener<State> {

  private final BubbleLifeCycleStateMachine stateMachine;

  private Sprite bubble;
  private ParticleSystem currentAboutToExplodeParticleSystem;

  BubbleAboutToExplodeParticleSystemLifecycleController(Sprite bubble,
      BubbleLifeCycleStateMachine stateMachine, BaseEntity parent) {
    super(parent);
    this.bubble = bubble;
    this.stateMachine = stateMachine;
    setupListeners();
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(BubbleAboutToExplodeParticleEffectEntity.class,
        new BubbleAboutToExplodeParticleEffectEntity(this));
  }

  private void setupListeners() {
    stateMachine.addAllStateTransitionListener(this);
  }

  private void removeListeners() {
    stateMachine.removeAllStateTransitionListener(this);
  }

  @Override
  public void onLifeycleControllersDestroy() {
    bubble = null;
    removeListeners();
  }

  @Override
  public void onEnterState(State newState) {
    switch (newState) {
      case BLINKING_SLOWLY:
      case BLINKING_FAST:
      case BLINKING_IMMINENT:
        startParticleEffect();
        break;
      default:
        stopParticleEffect();
    }
  }

  private void startParticleEffect() {
    if (currentAboutToExplodeParticleSystem == null) {
      currentAboutToExplodeParticleSystem = get(BubbleAboutToExplodeParticleEffectEntity.class)
          .start(bubble);
    }
  }

  private void stopParticleEffect() {
    if (currentAboutToExplodeParticleSystem != null) {
      get(BubbleAboutToExplodeParticleEffectEntity.class).stop(currentAboutToExplodeParticleSystem);
      currentAboutToExplodeParticleSystem = null;
    }
  }
}
