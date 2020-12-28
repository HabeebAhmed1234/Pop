package com.stupidfungames.pop.bubblespawn.bombbubbles;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.fixturedefdata.BombBubbleEntityUserData;
import com.stupidfungames.pop.pool.BaseSpriteInitializerParams;
import com.stupidfungames.pop.pool.BaseSpriteItemInitializer;
import com.stupidfungames.pop.pool.ItemPool;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

public class BombBubbleSpritePool extends ItemPool {

  private static final AndengineColor STARTING_BOMB_BUBBLE_COLOR = AndengineColor.RED;

  private final ItemInitializer<Sprite, BaseSpriteInitializerParams> initializer = new BaseSpriteItemInitializer<BaseSpriteInitializerParams>() {
    @Override
    public Sprite createNew(BaseSpriteInitializerParams params) {
      Sprite sprite = new Sprite(
          params.x,
          params.y,
          get(GameTexturesManager.class).getTextureRegion(TextureId.BOMB_BUBBLE),
          vertexBufferObjectManager);
      sprite.setUserData(new BombBubbleEntityUserData());
      sprite.setColor(STARTING_BOMB_BUBBLE_COLOR);
      clipBubblePosition(sprite);
      return sprite;
    }

    @Override
    public void onRecycle(Sprite item) {
      super.onRecycle(item);
      removePhysics(item);
    }
  };

  public BombBubbleSpritePool(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected ItemInitializer getInitializer() {
    return initializer;
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
}
