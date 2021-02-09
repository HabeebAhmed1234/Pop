package com.stupidfungames.pop.bubblespawn;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleSize;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;
import com.stupidfungames.pop.eventbus.BubbleRecycledEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.pool.BaseSpriteInitializerParams;
import com.stupidfungames.pop.pool.BaseSpriteItemInitializer;
import com.stupidfungames.pop.pool.ItemPool;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;

public class BubbleSpritePool extends ItemPool {

  public static class BubbleSpritePoolParams extends BaseSpriteInitializerParams {

    public final BubbleType bubbleType;
    public final BubbleSize bubbleSize;

    public BubbleSpritePoolParams(
        float x,
        float y,
        BubbleType bubbleType,
        BubbleSize bubbleSize) {
      super(x, y);
      this.bubbleType = bubbleType;
      this.bubbleSize = bubbleSize;
    }
  }

  private final ItemInitializer<Sprite, BubbleSpritePoolParams> initializer = new BaseSpriteItemInitializer<BubbleSpritePoolParams>() {
    @Override
    public Sprite createNew(BubbleSpritePoolParams params) {
      Sprite sprite = new Sprite(
          params.x,
          params.y,
          get(GameTexturesManager.class).getTextureRegion(TextureId.BALL),
          vertexBufferObjectManager);
      sprite.setUserData(
          new BubbleEntityUserData(
              true,
              params.bubbleSize,
              params.bubbleType,
              sprite));
      adjustBubbleSprite(sprite, params.bubbleSize, params.bubbleType);
      return sprite;
    }

    @Override
    public void update(Sprite item, BubbleSpritePoolParams params) {
      super.update(item, params);
      adjustBubbleSprite(item, params.bubbleSize, params.bubbleType);
      updateBubbleEntityUserData(item, params.bubbleSize, params.bubbleType);
    }

    @Override
    public void onRecycle(Sprite item) {
      super.onRecycle(item);
      BubbleEntityUserData userData = (BubbleEntityUserData) item.getUserData();
      userData.isTargeted = false;
      userData.isMarkedForRecursivePopping = false;
      removePhysics(item);
      EventBus.get()
          .sendEvent(GameEvent.BUBBLE_RECYCLED, new BubbleRecycledEventPayload(userData.getId()));
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
    sprite.setColor(bubbleType.color);
    clipBubblePosition(sprite);
  }

  private void updateBubbleEntityUserData(Shape bubbleSprite, BubbleSize size, BubbleType type) {
    BubbleEntityUserData userData = (BubbleEntityUserData) bubbleSprite.getUserData();
    userData.update(size, type);
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
    bubbleSprite
        .setScale(bubbleSize.sizePx / ((Sprite) bubbleSprite).getWidth());
  }
}
