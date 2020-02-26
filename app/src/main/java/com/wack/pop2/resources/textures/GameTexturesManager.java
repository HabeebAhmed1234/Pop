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
    private BitmapTextureAtlas mainBitmapTextureAtlas;

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

        loadExplosionTexture();
        loadBackgroundTexture();
        loadMainTextures();
    }

    private void loadExplosionTexture() {
        BitmapTextureAtlas explosionBitmapTextureAtlas = new BitmapTextureAtlas(textureManager, EXPLOSION_TEXTURE_ATLAS_W_PX, EXPLOSION_TEXTURE_ATLAS_H_PX, TextureOptions.BILINEAR);
        mTextureRegions.put(
                TextureId.EXPLOSION,
                BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                        explosionBitmapTextureAtlas,
                        context,
                        "explosion.png",
                        0, 0, 3, 4));
    }

    private void loadBackgroundTexture() {
        BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(textureManager, 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mTextureRegions.put(
                TextureId.BACKGROUND,
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                        textureAtlas, context, "main_menu_background.png",0, 0));
    }

    private void loadMainTextures() {
        mainBitmapTextureAtlas = new BitmapTextureAtlas(textureManager, 2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        addToMainTexture(TextureId.BALL, "ball.png", 150);
        addToMainTexture(TextureId.SKULL_BALL,  "skull_ball.png", 100);
        addToMainTexture(TextureId.GAME_OVER, "gameover_fade.png", 100);
        addToMainTexture(TextureId.CHAIN_LINK, "chain_link.png", 100);
        addToMainTexture(TextureId.BALL_AND_CHAIN_ICON, "ball_and_chain_icon.png",100);
        addToMainTexture(TextureId.LINE, "line.png", 110);
        addToMainTexture(TextureId.TURRETS_ICON, "turrets_icon.png",110);
        addToMainTexture(TextureId.WALLS_ICON, "walls_icon.png",110);
        addToMainTexture(TextureId.BULLET, "bullet.png", 30);
        addToMainTexture(TextureId.WHITE_PIXEL, "white_pixel.png", 1);
        addToMainTexture(TextureId.DELETE_WALL_ICON, "delete_wall_icon.png", 110);
        addToMainTexture(TextureId.NUKE_ICON, "nuke_icon.png", 110);
        mainBitmapTextureAtlas.load();
    }

    private void addToMainTexture(TextureId id, String filename, int width) {
        mTextureRegions.put(
                id,
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                        mainBitmapTextureAtlas, context, filename,currentTexturesWidth, 0));
        currentTexturesWidth += width + 1;
    }
}
