package com.stupidfungames.pop;

import android.opengl.GLES20;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpritePool;
import com.stupidfungames.pop.eventbus.DecrementScoreEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.resources.fonts.FontId;
import com.stupidfungames.pop.resources.fonts.GameFontsManager;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.text.Text;
import org.jbox2d.dynamics.Fixture;

/**
 * This entity detects whether or not any bubbles have fallen below the screen. Thus resulting in
 * points lost.
 */
public class BubbleLossDetectorEntity extends BaseLossDetectorEntity {

  public static final int SCORE_DECREMENT_AMOUNT = 20;


  public BubbleLossDetectorEntity(BinderEnity parent) {
    super(parent);
  }


  @Override
  protected Class getUserDataClassToDetectLossOf() {
    return BubbleEntityUserData.class;
  }

  @Override
  protected void processLoss(Fixture bubbleFixture) {
    BubbleEntityUserData data = (BubbleEntityUserData) bubbleFixture.getUserData();
    if (!data.bubbleSprite.isVisible()) {
      return;
    }
    if (data.isScoreLossBubble) {
      spawnScoreLossText(
          data.bubbleSprite.getX(),
          levelHeight - 80);
      EventBus.get().sendEvent(GameEvent.DECREMENT_SCORE,
          new DecrementScoreEventPayload(SCORE_DECREMENT_AMOUNT));
    }
    get(BubbleSpritePool.class).recycle(data.bubbleSprite);
  }

  private void spawnScoreLossText(float x, float y) {
    final Text scoreminusText = new Text(x, y,
        get(GameFontsManager.class).getFont(FontId.SCORE_TICKER_FONT), "-20",
        vertexBufferObjectManager);
    scoreminusText.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    scoreminusText.setColor(1, 0, 0);
    addToScene(scoreminusText);
    get(GameAnimationManager.class).startModifier(scoreminusText, new ParallelEntityModifier(
        new ScaleModifier(1.2f, 0.1f, 1.5f),
        new AlphaModifier(1.5f, 1f, 0f)));

    get(ShakeCamera.class).shake(0.5f, 4);
  }
}
