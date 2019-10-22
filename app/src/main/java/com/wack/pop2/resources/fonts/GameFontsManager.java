package com.wack.pop2.resources.textures;

import android.graphics.Color;
import android.graphics.Typeface;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;

public class GameFontsManager extends BaseEntity {

    public GameFontsManager(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateResources() {
        this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48,true, Color.WHITE);
        this.mFont.load();

        this.mScoreTickerFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 50,true, Color.WHITE);
        this.mScoreTickerFont.load();

        this.mCountdownFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 100,true, Color.WHITE);
        this.mCountdownFont.load();
    }
}
