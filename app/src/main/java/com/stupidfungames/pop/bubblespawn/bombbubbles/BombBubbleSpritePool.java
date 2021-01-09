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
import com.stupidfungames.pop.resources.fonts.FontId;
import com.stupidfungames.pop.resources.fonts.GameFontsManager;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.GeometryUtils;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.color.AndengineColor;

public class BombBubbleSpritePool extends ItemPool {

  public static final AndengineColor DIFFUSE_BOMB_COLOUR = AndengineColor.GREEN;
  public static final AndengineColor WARN_BOMB_COLOUR = AndengineColor.YELLOW;
  public static final AndengineColor EXPLODING_BOMB_COLOUR = AndengineColor.RED;
  public static final float BOMB_BUBBLE_SIZE_PX = BubbleSize.MEDIUM.getSizePx();
  public static final float BOMB_BUBBLE_COUNTDOWN_TEXT_TOP_PADDING_PX = dpToPx(14);

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
      sprite.setScale(BOMB_BUBBLE_SIZE_PX / sprite.getWidth());

      Text countDownText = new Text(0, 0,
          get(GameFontsManager.class).getFont(FontId.BOMB_BUBBLE_COUNTDOWN_FONT), "1",
          3,
          vertexBufferObjectManager);
      countDownText.setY(BOMB_BUBBLE_COUNTDOWN_TEXT_TOP_PADDING_PX);
      GeometryUtils.centerInHorizontal(sprite, countDownText);
      sprite.attachChild(countDownText);
      return sprite;
    }

    @Override
    public void update(Sprite sprite, BaseSpriteInitializerParams params) {
      super.update(sprite, params);
      sprite.setColor(EXPLODING_BOMB_COLOUR);
      registerColorModifier(sprite);
      registerCountdownTimerModifier((Text) sprite.getFirstChild());
    }

    @Override
    public void onRecycle(Sprite item) {
      super.onRecycle(item);
      item.clearEntityModifiers();
      item.clearUpdateHandlers();
      item.getFirstChild().clearEntityModifiers();
      item.getFirstChild().clearUpdateHandlers();
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

  private void registerColorModifier(Sprite bombBubble) {
    bombBubble.registerUpdateHandler(
        new BombStateToggleEntityModifier(
            bombBubble,
            BOMB_STATES_DURATION_SECONDS,
            EXPLODING_BOMB_COLOUR,
            WARN_BOMB_COLOUR,
            DIFFUSE_BOMB_COLOUR));
  }

  private void registerCountdownTimerModifier(Text timerText) {
    timerText.registerUpdateHandler(new BombTimerEntityModifier(
        timerText,
        BOMB_BUBBLE_LIFESPAN_SECONDS,
        get(BombBubbleExpiredListenerEntity.class)));
  }
}
