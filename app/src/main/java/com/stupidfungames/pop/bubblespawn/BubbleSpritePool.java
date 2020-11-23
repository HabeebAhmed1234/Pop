package com.stupidfungames.pop.bubblespawn;

import static com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType.RED;

import android.content.Context;
import com.stupidfungames.pop.BaseSpritePool;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleSize;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;
import com.stupidfungames.pop.fixturedefdata.BaseEntityUserData;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.AndengineColor;

public class BubbleSpritePool extends BaseSpritePool {

  private BubbleType nextBubbleType = RED;
  private BubbleSize nextBubbleSize = BubbleSize.LARGE;

  public BubbleSpritePool(BinderEnity parent) {
    super(parent);
  }

  public void setNextBubbleMetaData(BubbleType bubbleType, BubbleSize bubbleSize) {
    this.nextBubbleType = bubbleType;
    this.nextBubbleSize = bubbleSize;
  }

  @Override
  protected Sprite createNewSprite(float x, float y) {
    Sprite sprite = new Sprite(
        x,
        y,
        getBubbleTexture(nextBubbleType),
        vertexBufferObjectManager);
    sprite.setUserData(getBubbleUserData(sprite, nextBubbleType, nextBubbleSize));
    adjustBubbleSprite(sprite, nextBubbleSize, nextBubbleType);
    return sprite;
  }

  @Override
  protected void updateSprite(Sprite sprite) {
    adjustBubbleSprite(sprite, nextBubbleSize, nextBubbleType);
  }

  private void adjustBubbleSprite(Sprite sprite, BubbleSize bubbleSize, BubbleType bubbleType) {
    adjustBubbleScale(sprite, bubbleSize);
    colorBubble(bubbleType, sprite);
    clipBubblePosition(sprite);
  }

  private ITextureRegion getBubbleTexture(BubbleType bubbleType) {
    switch (bubbleType) {
      case RED:
      case GREEN:
      case BLUE:
        return get(GameTexturesManager.class).getTextureRegion(TextureId.BALL);
      default:
        throw new IllegalStateException(
            "there is no bubble texture for bubbleType = " + bubbleType);

    }
  }

  private BaseEntityUserData getBubbleUserData(Sprite bubbleSprite, BubbleType bubbleType,
      BubbleSize bubbleSize) {
    switch (bubbleType) {
      case RED:
      case GREEN:
      case BLUE:
        return new BubbleEntityUserData(true, bubbleSize, bubbleType, bubbleSprite);
    }
    throw new IllegalStateException("there is no bubble user data for bubbleType = " + bubbleType);
  }

  private void colorBubble(BubbleType type, Sprite bubble) {
    AndengineColor color = AndengineColor.WHITE;
    switch (type) {
      case RED:
        color = AndengineColor.RED;
        break;
      case GREEN:
        color = AndengineColor.GREEN;
        break;
      case BLUE:
        color = AndengineColor.BLUE;
        break;
      default:
        throw new IllegalStateException("there is no bubble color for bubbleType = " + type);
    }
    if (color != null) {
      bubble.setColor(color);
    }
  }

  private void clipBubblePosition(Sprite bubbleSprite) {
    if (bubbleSprite.getX() < 0) {
      bubbleSprite.setX(0);
    }
    float bubbleWidth = bubbleSprite.getWidthScaled();
    if (bubbleSprite.getX() + bubbleWidth > ScreenUtils.getSreenSize().widthPx) {
      bubbleSprite.setX(ScreenUtils.getSreenSize().widthPx - bubbleWidth);
    }
  }

  private void adjustBubbleScale(Sprite bubbleSprite, BubbleSize bubbleSize) {
    float bubbleSizePx = ScreenUtils.dpToPx(bubbleSize.sizeDp, get(Context.class));
    bubbleSprite.setScale(bubbleSizePx / bubbleSprite.getWidthScaled());
  }
}
