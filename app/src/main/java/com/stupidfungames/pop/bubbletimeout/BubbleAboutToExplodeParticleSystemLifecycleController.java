package com.stupidfungames.pop.bubbletimeout;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.statemachine.BaseStateMachine;
import org.andengine.audio.sound.Sound;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.sprite.Sprite;

class BubbleAboutToExplodeParticleSystemLifecycleController extends BaseEntity implements
    BubbleLifecycleController,
    BaseStateMachine.Listener<State> {

  private final BubbleLifeCycleStateMachine stateMachine;

  private Sprite bubble;
  private ParticleSystem currentAboutToExplodeParticleSystem;
  private int beebSoundStreamId = -1;

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
      case ABOUT_TO_EXPLODE:
        startParticleEffect();
        break;
      default:
        stopParticleEffect();
    }
  }

  private void startParticleEffect() {
    if (currentAboutToExplodeParticleSystem == null) {
      Sound beepSound = get(GameSoundsManager.class).getSound(SoundId.BEEP);
      beepSound.setLooping(true);
      beebSoundStreamId = beepSound.play();
      currentAboutToExplodeParticleSystem = get(BubbleAboutToExplodeParticleEffectEntity.class)
          .start(bubble);
    }
  }

  private void stopParticleEffect() {
    if (currentAboutToExplodeParticleSystem != null) {
      if (beebSoundStreamId != -1) {
        get(GameSoundsManager.class).getSound(SoundId.BEEP).stop(beebSoundStreamId);
      }
      get(BubbleAboutToExplodeParticleEffectEntity.class).stop(currentAboutToExplodeParticleSystem);
      currentAboutToExplodeParticleSystem = null;
      beebSoundStreamId = -1;
    }
  }
}
