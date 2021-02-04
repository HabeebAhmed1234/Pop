package com.stupidfungames.pop.resources.fonts;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.binder.BinderEnity;
import java.util.HashMap;
import java.util.Map;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

public class GameFontsManager extends BaseEntity {

  private final Map<FontId, Font> mFonts = new HashMap();

  public GameFontsManager(BinderEnity parent) {
    super(parent);
  }

  public Font getFont(FontId fontId) {
    if (!mFonts.containsKey(fontId)) {
      throw new IllegalArgumentException("Font " + fontId + " was never initialized");
    }
    return mFonts.get(fontId);
  }

  @Override
  public void onCreateResources() {
    FontManager fontManager = get(FontManager.class);
    TextureManager textureManager = get(TextureManager.class);
    AssetManager assetManager = get(AssetManager.class);

    Context context = get(Context.class);
    Resources resources = context.getResources();

    FontFactory.setAssetBasePath("font/");
    mFonts.put(
        FontId.SCORE_TICKER_FONT,
        FontFactory.createFromAsset(fontManager, new BitmapTextureAtlas(textureManager, 256, 512),
            assetManager, "neon.ttf", resources.getDimension(R.dimen.ticker_font_size), true,
            Color.WHITE));
    mFonts.put(
        FontId.INVENTORY_ICON_FONT,
        FontFactory.createFromAsset(fontManager, new BitmapTextureAtlas(textureManager, 256, 256),
            assetManager, "neon.ttf", resources.getDimension(R.dimen.inventory_font_size), true,
            Color.WHITE));
    mFonts.put(
        FontId.BOMB_BUBBLE_COUNTDOWN_FONT,
        FontFactory.createFromAsset(fontManager, new BitmapTextureAtlas(textureManager, 256, 256),
            assetManager, "neon.ttf", resources.getDimension(R.dimen.bomb_bubble_countdown_font),
            true, Color.WHITE));
    loadFonts();
  }

  private void loadFonts() {
    for (Font font : mFonts.values()) {
      font.load();
    }
  }
}
