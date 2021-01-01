package com.stupidfungames.pop.bubblespawn.bombbubbles;

import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.shape.IShape;
import org.andengine.util.color.AndengineColor;

public class BombStateToggleEntityModifier extends TimerHandler implements ITimerCallback {

  private final IShape bombBubble;
  private final AndengineColor startColor;
  private final AndengineColor endColor;


  public BombStateToggleEntityModifier(
      IShape bombBubble,
      float toggleDurationSeconds,
      AndengineColor startColor,
      AndengineColor endColor) {
    super(toggleDurationSeconds, true, null);
    setTimerCallback(this);
    this.bombBubble = bombBubble;
    this.startColor = startColor;
    this.endColor = endColor;
    bombBubble.setColor(startColor);
  }

  @Override
  public void onTimePassed(TimerHandler pTimerHandler) {
    if (ScreenUtils.isInScreen(bombBubble)) {
      if (bombBubble.getColor().equals(startColor)) {
        bombBubble.setColor(endColor);
      } else {
        bombBubble.setColor(startColor);
      }
    }
  }
}
