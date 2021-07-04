package com.stupidfungames.pop.sword;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.pool.ItemPool;
import com.stupidfungames.pop.sword.SwordLineSpritesPool.LineSpriteInitializerParams;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierMatcher;
import org.andengine.entity.primitive.Line;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

public class SwordLineSpritesPool extends ItemPool<Line, LineSpriteInitializerParams> {

  private static final int FADE_TIME_SECONDS = 1;
  private static final float LINE_THICKNESS_PX = 10;
  private static final IEntityModifierMatcher ALPHA_ENTITY_MOD_MATCHER = new IEntityModifierMatcher() {
    @Override
    public boolean matches(IModifier<IEntity> pObject) {
      return pObject instanceof AlphaModifier;
    }
  };

  public static class LineSpriteInitializerParams {

    public final float x1;
    public final float y1;
    public final float x2;
    public final float y2;

    public LineSpriteInitializerParams(float x1, float y1, float x2, float y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }
  }

  private final ItemInitializer<Line, LineSpriteInitializerParams> initializer =
      new ItemInitializer<Line, LineSpriteInitializerParams>() {

        private AlphaModifier getAlphaMod(final Line line) {
          AlphaModifier alphaModifier = new AlphaModifier(FADE_TIME_SECONDS, 1, 0);
          alphaModifier.addModifierListener(new IModifierListener<IEntity>() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
              recycle(line);
            }
          });

          return alphaModifier;
        }

        @Override
        public Line createNew(LineSpriteInitializerParams params) {
          Line line = new Line(params.x1, params.y1, params.x2, params.y2,
              vertexBufferObjectManager);
          line.registerEntityModifier(getAlphaMod(line));
          line.setLineWidth(LINE_THICKNESS_PX);
          return line;
        }

        @Override
        public void update(Line line, LineSpriteInitializerParams params) {
          line.setPosition(params.x1, params.y1, params.x2, params.y2);
          line.setVisible(true);
          if (line.getEntityModifierCount() > 0) {
            IModifier alphaMod = line.getEntityModifier(ALPHA_ENTITY_MOD_MATCHER);
            if (alphaMod != null) {
              alphaMod.reset();
              return;
            }
          }
          line.registerEntityModifier(getAlphaMod(line));
        }

        @Override
        public void onRecycle(Line item) {
          item.setVisible(false);
        }

        @Override
        public void destroy(Line item) {
        }
      };

  public SwordLineSpritesPool(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected ItemInitializer getInitializer() {
    return initializer;
  }
}
