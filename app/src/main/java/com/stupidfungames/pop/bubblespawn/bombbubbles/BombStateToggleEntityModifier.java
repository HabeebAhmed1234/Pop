package com.stupidfungames.pop.bubblespawn.bombbubbles;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.util.color.AndengineColor;

public class BombStateToggleEntityModifier extends TimerHandler implements ITimerCallback {

  private enum State {
    DIFFUSE,
    WARN,
    EXPLODE,
    WARN2,
  }

  private final IAreaShape bombBubble;
  private final AndengineColor diffuseColor;
  private final AndengineColor warnColor;
  private final AndengineColor explodeColor;

  private State state = State.DIFFUSE;


  public BombStateToggleEntityModifier(
      IAreaShape bombBubble,
      float toggleDurationSeconds,
      AndengineColor diffuseColor,
      AndengineColor warnColor,
      AndengineColor explodeColor) {
    super(toggleDurationSeconds, true, null);
    setTimerCallback(this);
    this.bombBubble = bombBubble;
    this.diffuseColor = diffuseColor;
    this.warnColor = warnColor;
    this.explodeColor = explodeColor;
    bombBubble.setColor(diffuseColor);
  }

  @Override
  public void onTimePassed(TimerHandler pTimerHandler) {
    advanceState();
    bombBubble.setColor(getColor());
  }

  private void advanceState() {
    switch (state) {
      case DIFFUSE:
        state = State.WARN;
        break;
      case WARN:
        state = State.EXPLODE;
        break;
      case EXPLODE:
        state = State.WARN2;
        break;
      case WARN2:
        state = State.DIFFUSE;
        break;
    }
  }

  private AndengineColor getColor() {
    switch (state) {
      case DIFFUSE:
        return diffuseColor;
      case WARN:
      case WARN2:
        return warnColor;
      case EXPLODE:
        return explodeColor;
    }
    return diffuseColor;
  }
}
