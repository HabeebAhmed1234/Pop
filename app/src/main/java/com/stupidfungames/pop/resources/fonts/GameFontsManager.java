package com.stupidfungames.pop.resources.fonts;

import android.content.res.AssetManager;
import android.graphics.Color;
import com.stupidfungames.pop.BaseEntity;
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

    FontFactory.setAssetBasePath("font/");
    mFonts.put(
        FontId.SCORE_TICKER_FONT,
        FontFactory.createFromAsset(fontManager, new BitmapTextureAtlas(textureManager, 256, 512),
            assetManager, "neon.ttf", 80f, true,
            Color.WHITE));
    mFonts.put(
        FontId.INVENTORY_ICON_FONT,
        FontFactory.createFromAsset(fontManager, new BitmapTextureAtlas(textureManager, 256, 256),
            assetManager, "neon.ttf", 60f, true,
            Color.WHITE));
    mFonts.put(
        FontId.BOMB_BUBBLE_COUNTDOWN_FONT,
        FontFactory.createFromAsset(fontManager, new BitmapTextureAtlas(textureManager, 256, 256),
            assetManager, "neon.ttf", 45f,
            true, Color.WHITE));
    loadFonts();
  }

  private void loadFonts() {
    for (Font font : mFonts.values()) {
      font.load();
    }
  }
}
