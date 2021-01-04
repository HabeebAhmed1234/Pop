package com.stupidfungames.pop.bubbletimeout;

import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.sprite.Sprite;

public class BubbleAboutToExplodeParticleEmitter extends CircleOutlineParticleEmitter {

  private Sprite bubbleSprite;

  public BubbleAboutToExplodeParticleEmitter(Sprite bubbleSprite) {
    super(bubbleSprite.getCenterX(), bubbleSprite.getCenterY(),
        (bubbleSprite.getWidthScaled() * BubbleSpawnerEntity.BUBBLE_BODY_SCALE_FACTOR) / 2);
    this.bubbleSprite = bubbleSprite;
  }

  public void setSprite(Sprite bubbleSprite) {
    this.bubbleSprite = bubbleSprite;
  }

  @Override
  public void onUpdate(float pSecondsElapsed) {
    super.onUpdate(pSecondsElapsed);
    if (bubbleSprite.getCenterX() != getCenterX()) {
      setCenterX(bubbleSprite.getCenterX());
    }
    if (bubbleSprite.getCenterY() != getCenterY()) {
      setCenterY(bubbleSprite.getCenterY());
    }
  }
}
