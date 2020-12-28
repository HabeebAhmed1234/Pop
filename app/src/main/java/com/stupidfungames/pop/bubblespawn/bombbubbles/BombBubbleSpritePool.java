package com.stupidfungames.pop.bubblespawn.bombbubbles;

import static com.stupidfungames.pop.GameConstants.BOMB_BUBBLE_LIFESPAN_SECONDS;
import static com.stupidfungames.pop.GameConstants.BOMB_STATES_DURATION_SECONDS;
import static com.stupidfungames.pop.utils.ScreenUtils.dpToPx;

import android.content.Context;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleSize;
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

  public static final AndengineColor DIFFUSE_BOMB_COLOUR = AndengineColor.GREEN;
  public static final AndengineColor EXPLODING_BOMB_COLOUR = AndengineColor.RED;
  public static final float BOMB_BUBBLE_SIZE_DP = BubbleSize.MEDIUM.sizeDp;
  private final float bombBubbleSizePx;

  private final ItemInitializer<Sprite, BaseSpriteInitializerParams> initializer = new BaseSpriteItemInitializer<BaseSpriteInitializerParams>() {
    @Override
    public Sprite createNew(BaseSpriteInitializerParams params) {
      Sprite sprite = new Sprite(
          params.x,
          params.y,
          get(GameTexturesManager.class).getTextureRegion(TextureId.BOMB_BUBBLE),
          vertexBufferObjectManager);
      sprite.setUserData(new BombBubbleEntityUserData());
      sprite.setColor(EXPLODING_BOMB_COLOUR);
      clipBubblePosition(sprite);
      sprite.setScale(bombBubbleSizePx / sprite.getWidth());
      return sprite;
    }

    @Override
    public void update(Sprite sprite, BaseSpriteInitializerParams params) {
      super.update(sprite, params);
      sprite.setColor(EXPLODING_BOMB_COLOUR);
      registerColorModifier(sprite);
    }

    @Override
    public void onRecycle(Sprite item) {
      super.onRecycle(item);
      item.clearEntityModifiers();
      removePhysics(item);
    }
  };

  public BombBubbleSpritePool(BinderEnity parent) {
    super(parent);
    bombBubbleSizePx = dpToPx(BOMB_BUBBLE_SIZE_DP, get(Context.class));
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

  private void registerColorModifier(Sprite bombBubble) {
    bombBubble.registerEntityModifier(
        new BombStateToggleEntityModifier(
            BOMB_STATES_DURATION_SECONDS,
            BOMB_BUBBLE_LIFESPAN_SECONDS,
            EXPLODING_BOMB_COLOUR,
            DIFFUSE_BOMB_COLOUR,
            get(BombBubbleExpiredListenerEntity.class)));
  }
}
