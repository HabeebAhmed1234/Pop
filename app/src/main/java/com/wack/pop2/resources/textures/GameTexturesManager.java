package com.wack.pop2.resources.textures;

import android.content.Context;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;

import java.util.HashMap;
import java.util.Map;

public class GameTexturesManager extends BaseEntity {

    private static final int EXPLOSION_TEXTURE_ATLAS_W_PX = 850;
    private static final int EXPLOSION_TEXTURE_ATLAS_H_PX = 950;

    private Context context;
    private TextureManager textureManager;

    private Map<TextureIds, ITextureRegion> mTextureRegions = new HashMap();

    public GameTexturesManager(Context context, TextureManager textureManager, GameResources gameResources) {
        super(gameResources);
        this.context = context;
        this.textureManager = textureManager;
    }

    @Override
    public void onCreateResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        BitmapTextureAtlas explosionBitmapTextureAtlas = new BitmapTextureAtlas(textureManager, EXPLOSION_TEXTURE_ATLAS_W_PX, EXPLOSION_TEXTURE_ATLAS_H_PX, TextureOptions.BILINEAR);

        mTextureRegions.put(
                TextureIds.EXPLOSION,
                BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                        explosionBitmapTextureAtlas,
                        context,
                        "explosion.png",
                        0, 0, 3, 4));

        BitmapTextureAtlas mainBitmapTextureAtlas = new BitmapTextureAtlas(textureManager, 1000, 700, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        mTextureRegions.put(
                TextureIds.BALL_1,
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(mainBitmapTextureAtlas, context, "red_ball.png", 0, 0));
        mTextureRegions.put(TextureIds.BALL_2,
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(mainBitmapTextureAtlas, context, "blue_ball.png", 100, 0);
        mTextureRegions.put(TextureIds.BALL_3,
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(mainBitmapTextureAtlas, context, "green_ball.png", 200, 0);

        mTextureRegions.put(TextureIds.SKULL_BALL,
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(mainBitmapTextureAtlas, context, "skull_ball.png", 300, 0));
        mTextureRegions.put(TextureIds.SCORE_BACKGROUND,
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(mainBitmapTextureAtlas, context, "ScoreBack.png", 407, 0));
        mTextureRegions.put(TextureIds.GAME_OVER,
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(mainBitmapTextureAtlas, context, "gameover_fade.png", 607, 0));

        explosionBitmapTextureAtlas.load();
        mainBitmapTextureAtlas.load();

    }
}
