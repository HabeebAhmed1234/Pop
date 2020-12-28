package com.stupidfungames.pop.bubblespawn.bombbubbles;

import android.util.Log;
import androidx.core.util.Preconditions;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.util.color.AndengineColor;
import org.andengine.util.modifier.BaseDurationModifier;

public class BombStateToggleEntityModifier extends BaseDurationModifier<IEntity> implements
    IEntityModifier {

  private final BombBubbleExpiredListenerEntity expiredListenerEntity;
  private final float toggleDurationSeconds;
  private final AndengineColor startColor;
  private final AndengineColor endColor;
  private float lastToggleSeconds = 0;

  public BombStateToggleEntityModifier(
      float toggleDurationSeconds,
      float totalDuration,
      AndengineColor startColor,
      AndengineColor endColor,
      BombBubbleExpiredListenerEntity expiredListenerEntity) {
    super(totalDuration);
    // Check that the total duration divides into TOGGLE_DURATION_SECONDS and that we will always
    // end on a red state
    Preconditions.checkArgument(
        totalDuration % toggleDurationSeconds == 0
            && (totalDuration / toggleDurationSeconds) % 2 == 1);
    this.expiredListenerEntity = expiredListenerEntity;
    this.toggleDurationSeconds = toggleDurationSeconds;
    this.startColor = startColor;
    this.endColor = endColor;
    setAutoUnregisterWhenFinished(false);
  }

  public BombStateToggleEntityModifier(BombStateToggleEntityModifier other) {
    super(other.mDuration);
    this.expiredListenerEntity = other.expiredListenerEntity;
    this.toggleDurationSeconds = other.toggleDurationSeconds;
    this.startColor = other.startColor;
    this.endColor = other.endColor;
    setAutoUnregisterWhenFinished(false);
  }

  @Override
  protected void onManagedUpdate(float pSecondsElapsed, IEntity pItem) {
    final float elapsed = getSecondsElapsed();
    if (elapsed - lastToggleSeconds > toggleDurationSeconds) {
      if (pItem.getColor().equals(startColor)) {
        pItem.setColor(endColor);
      } else {
        pItem.setColor(startColor);
      }
      lastToggleSeconds = elapsed;
    }
  }

  @Override
  protected void onManagedInitialize(IEntity pItem) {
    pItem.setColor(startColor);
  }

  @Override
  protected void onModifierFinished(IEntity pItem) {
    super.onModifierFinished(pItem);
    expiredListenerEntity.onBombBubbleExpired(pItem);
  }

  @Override
  public void reset() {
    super.reset();
    lastToggleSeconds = 0;
  }

  @Override
  public IEntityModifier deepCopy() throws DeepCopyNotSupportedException {
    return new BombStateToggleEntityModifier(this);
  }
}
