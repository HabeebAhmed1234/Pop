package com.stupidfungames.pop.bubblespawn;

import android.content.Context;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleSize;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;
import com.stupidfungames.pop.fixturedefdata.BaseEntityUserData;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.pool.BaseSpriteItemInitializer;
import com.stupidfungames.pop.pool.ItemPool;
import com.stupidfungames.pop.pool.SpriteInitializerParams;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.AndengineColor;

public class BubbleSpritePool extends ItemPool {

  public static class SpritePoolParams extends SpriteInitializerParams {

    public final BubbleType bubbleType;
    public final BubbleSize bubbleSize;

    public SpritePoolParams(
        float x,
        float y,
        BubbleType bubbleType,
        BubbleSize bubbleSize) {
      super(x, y);
      this.bubbleType = bubbleType;
      this.bubbleSize = bubbleSize;
    }
  }

  private final ItemInitializer<Sprite, SpritePoolParams> initializer = new BaseSpriteItemInitializer<SpritePoolParams>() {
    @Override
    public Sprite createNew(SpritePoolParams params) {
      Sprite sprite = new Sprite(
          params.x,
          params.y,
          getBubbleTexture(params.bubbleType),
          vertexBufferObjectManager);
      sprite.setUserData(getBubbleUserData(sprite, params.bubbleType, params.bubbleSize));
      adjustBubbleSprite(sprite, params.bubbleSize, params.bubbleType);
      return sprite;
    }

    @Override
    public void update(Sprite item, SpritePoolParams params) {
      super.update(item, params);
      adjustBubbleSprite(item, params.bubbleSize, params.bubbleType);
      updateBubbleEntityUserData(item, params.bubbleSize, params.bubbleType);
    }

    @Override
    public void onRecycle(Sprite item) {
      super.onRecycle(item);
      BubbleEntityUserData userData = (BubbleEntityUserData) item.getUserData();
      userData.isTargeted = false;
      removePhysics(item);
    }
  };

  public BubbleSpritePool(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected ItemInitializer getInitializer() {
    return initializer;
  }

  private void adjustBubbleSprite(Shape sprite, BubbleSize bubbleSize, BubbleType bubbleType) {
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

  private BaseEntityUserData getBubbleUserData(Shape bubbleSprite, BubbleType bubbleType,
      BubbleSize bubbleSize) {
    switch (bubbleType) {
      case RED:
      case GREEN:
      case BLUE:
        return new BubbleEntityUserData(true, bubbleSize, bubbleType, (Sprite) bubbleSprite);
    }
    throw new IllegalStateException("there is no bubble user data for bubbleType = " + bubbleType);
  }

  private void updateBubbleEntityUserData(Shape bubbleSprite, BubbleSize size, BubbleType type) {
    BubbleEntityUserData userData = (BubbleEntityUserData) bubbleSprite.getUserData();
    userData.update(size, type);
  }

  private void colorBubble(BubbleType type, Shape bubble) {
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

  private void clipBubblePosition(Shape bubbleSprite) {
    if (bubbleSprite.getX() < 0) {
      bubbleSprite.setX(0);
    }
    float bubbleWidth = ((Sprite) bubbleSprite).getWidthScaled();
    if (bubbleSprite.getX() + bubbleWidth > ScreenUtils.getSreenSize().widthPx) {
      bubbleSprite.setX(ScreenUtils.getSreenSize().widthPx - bubbleWidth);
    }
  }

  private void adjustBubbleScale(Shape bubbleSprite, BubbleSize bubbleSize) {
    float bubbleSizePx = ScreenUtils.dpToPx(bubbleSize.sizeDp, get(Context.class));
    bubbleSprite.setScale(bubbleSizePx / ((Sprite) bubbleSprite).getWidth());
  }
}
