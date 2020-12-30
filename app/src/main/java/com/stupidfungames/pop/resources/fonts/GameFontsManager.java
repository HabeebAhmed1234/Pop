package com.stupidfungames.pop.resources.fonts;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

import java.util.HashMap;
import java.util.Map;

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
        AssetManager assetManager= get(AssetManager.class);

        mFonts.put(
                FontId.MAIN_FONT,
                FontFactory.create(fontManager, textureManager, 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48,true, Color.WHITE));
        mFonts.put(
                FontId.COUNT_DOWN_FONT,
                FontFactory.create(fontManager, textureManager, 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 100,true, Color.WHITE));

        FontFactory.setAssetBasePath("font/");
        mFonts.put(
                FontId.SCORE_TICKER_FONT,
                FontFactory.createFromAsset(fontManager, new BitmapTextureAtlas(textureManager,256,512), assetManager,"neon.ttf",80f,true, Color.WHITE));
        mFonts.put(
                FontId.TIMER_TICKER_FONT,
                FontFactory.createFromAsset(fontManager, new BitmapTextureAtlas(textureManager,256,256), assetManager,"neon.ttf",80f,true, Color.WHITE));
        mFonts.put(
                FontId.INVENTORY_ICON_FONT,
                FontFactory.createFromAsset(fontManager, new BitmapTextureAtlas(textureManager,256,256), assetManager,"neon.ttf",60f,true, Color.WHITE));
        mFonts.put(
                FontId.TOOLTIP_TEXT_FONT,
                FontFactory.createFromAsset(fontManager, new BitmapTextureAtlas(textureManager,256,256), assetManager,"neon.ttf",60f,true, Color.WHITE));
        mFonts.put(
            FontId.BOMB_BUBBLE_COUNTDOWN_FONT,
            FontFactory.createFromAsset(fontManager, new BitmapTextureAtlas(textureManager,256,256), assetManager,"neon.ttf",20f,true, Color.WHITE));
        loadFonts();
    }

    private void loadFonts() {
        for (Font font : mFonts.values()) {
            font.load();
        }
    }
}
