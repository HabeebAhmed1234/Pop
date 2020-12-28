package com.stupidfungames.pop.bubblepopper;

import android.opengl.GLES20;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.pool.ItemPool;
import com.stupidfungames.pop.pool.BaseSpriteInitializerParams;
import com.stupidfungames.pop.resources.fonts.FontId;
import com.stupidfungames.pop.resources.fonts.GameFontsManager;
import org.andengine.entity.text.Text;

public class ScoreTickerSpritePool extends ItemPool {

  public ScoreTickerSpritePool(BinderEnity parent) {
    super(parent);
  }

  private final ItemInitializer initializer = new ItemInitializer<Text, BaseSpriteInitializerParams>() {

    @Override
    public Text createNew(BaseSpriteInitializerParams params) {
      Text text = new Text(params.x, params.y,
          get(GameFontsManager.class).getFont(FontId.SCORE_TICKER_FONT), "+10!",
          vertexBufferObjectManager);
      text.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
      text.setColor(0, 1, 0);
      return text;
    }

    @Override
    public void update(Text item, BaseSpriteInitializerParams params) {
      item.setX(params.x);
      item.setY(params.y);
    }

    @Override
    public void onRecycle(Text item) {

    }

    @Override
    public void destroy(Text item) {

    }
  };

  @Override
  protected ItemInitializer getInitializer() {
    return initializer;
  }
}
