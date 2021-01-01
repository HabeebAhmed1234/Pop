package com.stupidfungames.pop.bubblespawn.bombbubbles;

import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.shape.IShape;
import org.andengine.util.color.AndengineColor;

public class BombStateToggleEntityModifier extends TimerHandler implements ITimerCallback {

  private final IShape bombBubble;
  private final AndengineColor diffuseColor;
  private final AndengineColor warnColor;
  private final AndengineColor explodeColor;


  public BombStateToggleEntityModifier(
      IShape bombBubble,
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
    if (ScreenUtils.isInScreen(bombBubble)) {
      AndengineColor currentColor = bombBubble.getColor();
      if (currentColor.equals(diffuseColor)) {
        bombBubble.setColor(warnColor);
      } else if (currentColor.equals(warnColor)) {
        bombBubble.setColor(explodeColor);
      } else if (currentColor.equals(explodeColor)) {
        bombBubble.setColor(diffuseColor);
      }
    }
  }
}
