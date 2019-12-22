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

    private final Context context;
    private final TextureManager textureManager;
    private final Map<TextureId, ITextureRegion> mTextureRegions = new HashMap();

    private int currentTexturesWidth = 0;

    public GameTexturesManager(Context context, TextureManager textureManager, GameResources gameResources) {
        super(gameResources);
        this.context = context;
        this.textureManager = textureManager;
    }

    public ITextureRegion getTextureRegion(TextureId textureId) {
        if (!mTextureRegions.containsKey(textureId)) {
            throw new IllegalArgumentException("Texture " + textureId + " does not exist in GameTexturesManager");
        }
        return mTextureRegions.get(textureId);
    }

    @Override
    public void onCreateResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        BitmapTextureAtlas explosionBitmapTextureAtlas = new BitmapTextureAtlas(textureManager, EXPLOSION_TEXTURE_ATLAS_W_PX, EXPLOSION_TEXTURE_ATLAS_H_PX, TextureOptions.BILINEAR);

        mTextureRegions.put(
                TextureId.EXPLOSION,
                BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                        explosionBitmapTextureAtlas,
                        context,
                        "explosion.png",
                        0, 0, 3, 4));

        BitmapTextureAtlas mainBitmapTextureAtlas = new BitmapTextureAtlas(textureManager, 800, 700, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        addTexture(TextureId.BALL, mainBitmapTextureAtlas, "ball.png", 150);
        addTexture(TextureId.SKULL_BALL, mainBitmapTextureAtlas,  "skull_ball.png", 100);
        addTexture(TextureId.GAME_OVER, mainBitmapTextureAtlas, "gameover_fade.png", 100);
        addTexture(TextureId.CHAIN_LINK, mainBitmapTextureAtlas, "chain_link.png", 100);
        addTexture(TextureId.BALL_AND_CHAIN_ICON, mainBitmapTextureAtlas, "ball_and_chain_icon.png",100);
        addTexture(TextureId.LINE, mainBitmapTextureAtlas, "line.png", 110);
        addTexture(TextureId.TURRETS_ICON, mainBitmapTextureAtlas, "turrets_icon.png",110);

        explosionBitmapTextureAtlas.load();
        mainBitmapTextureAtlas.load();

    }

    private void addTexture(TextureId id, BitmapTextureAtlas textureAtlas, String filename, int width) {
        mTextureRegions.put(
                id,
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                        textureAtlas, context, filename,currentTexturesWidth, 0));
        currentTexturesWidth += width + 1;
    }
}
