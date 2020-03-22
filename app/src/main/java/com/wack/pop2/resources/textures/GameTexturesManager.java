package com.wack.pop2.resources.textures;

import android.content.Context;
import android.media.Image;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.texturepacker.RectanglePacker;

import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameTexturesManager extends BaseEntity {

    private static final String TAG = "GameTexturesManager";
    /**
     * Encodes an image resource being used by the app.
     */
    private static class ImageResource {

        private TextureId textureId;
        private final String filename;
        private final int widthPx;
        private final int heightPx;

        private boolean isTiled;
        @Nullable private Pair<Integer, Integer> tilesSize;

        ImageResource(TextureId textureId, final String filename, int widthPx, int heightPx) {
            this(textureId, filename, widthPx, heightPx, false, null);
        }

        ImageResource(TextureId textureId, final String filename, int widthPx, int heightPx, boolean isTiled, Pair<Integer, Integer> tilesSize) {
            this.textureId = textureId;
            this.filename = filename;
            this.widthPx = widthPx;
            this.heightPx = heightPx;
            this.isTiled = isTiled;
            this.tilesSize = tilesSize;
        }
    }

    private static final List<ImageResource> IMAGE_RESOURCES = Arrays.asList(
            new ImageResource(TextureId.EXPLOSION, "explosion.png", 850, 950, true, new Pair<>(3, 4)),
            new ImageResource(TextureId.BALL, "ball.png", 300, 300),
            new ImageResource(TextureId.GAME_OVER, "gameover_fade.png", 100, 100),
            new ImageResource(TextureId.CHAIN_LINK, "chain_link.png", 150, 75),
            new ImageResource(TextureId.BALL_AND_CHAIN_ICON, "ball_and_chain_icon.png",300, 300),
            new ImageResource(TextureId.LINE, "line.png", 110, 30),
            new ImageResource(TextureId.TURRETS_ICON, "turrets_icon.png",300, 300),
            new ImageResource(TextureId.WALLS_ICON, "walls_icon.png",300, 300),
            new ImageResource(TextureId.BULLET, "bullet.png", 30, 30),
            new ImageResource(TextureId.WHITE_PIXEL, "white_pixel.png", 1, 1),
            new ImageResource(TextureId.NUKE_ICON, "nuke_icon.png", 300, 300),
            new ImageResource(TextureId.BACKGROUND, "main_menu_background.png", 576, 1022),
            new ImageResource(TextureId.OPEN_BTN, "open_button.png", 300, 300),
            new ImageResource(TextureId.X_BTN, "x_button.png", 300, 300),
            new ImageResource(TextureId.QUICK_SETTINGS_BTN, "quick_settings_button.png", 300, 300),
            new ImageResource(TextureId.MUSIC_QUICK_SETTING_ICON, "music_setting_button.png", 200, 200),
            new ImageResource(TextureId.PAUSE_BTN, "pause_button.png", 200, 200)
    );

    private static final int MAX_TEXTURE_ATLAS_WIDTH = 1024;
    private static final int MAX_TEXTURE_ATLAS_HEIGHT = 1024;


    private final Context context;
    private final TextureManager textureManager;
    private final Map<TextureId, ITextureRegion> mTextureRegions = new HashMap();
    private List<BitmapTextureAtlas> atlases = new ArrayList<>();

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
        arrangeTextures();
        loadAtlases();
    }

    private void arrangeTextures() {
        Iterator<ImageResource> imageResourceIterator = IMAGE_RESOURCES.iterator();

        RectanglePacker<ImageResource> packer = createNewRectanglePacker();
        BitmapTextureAtlas currentTextureAtlas = createNewAtlas();

        while (imageResourceIterator.hasNext()) {
            ImageResource resource = imageResourceIterator.next();
            boolean packingSuccessful = packThenAddToAtlas(packer, currentTextureAtlas, resource);
            if (!packingSuccessful) {
                // The resource could not fit. We must make a new atlas and add the image in
                currentTextureAtlas = createNewAtlas();
                packer = createNewRectanglePacker();

                if (!packThenAddToAtlas(packer, currentTextureAtlas, resource)) {
                    throw new IllegalArgumentException("given texture " + resource.filename + " is too large to fit an atlas");
                }
            }
        }
    }

    private RectanglePacker<ImageResource> createNewRectanglePacker() {
        return new RectanglePacker<>(MAX_TEXTURE_ATLAS_WIDTH, MAX_TEXTURE_ATLAS_HEIGHT, 1);
    }

    private BitmapTextureAtlas createNewAtlas() {
        BitmapTextureAtlas atlas = new BitmapTextureAtlas(textureManager, MAX_TEXTURE_ATLAS_WIDTH, MAX_TEXTURE_ATLAS_HEIGHT, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        atlases.add(atlas);
        return atlas;
    }

    /**
     * Returns false if there was no valid packed location. Else true if it was successful
     */
    private boolean packThenAddToAtlas(RectanglePacker packer, BitmapTextureAtlas textureAtlas, ImageResource resource) {
        @Nullable RectanglePacker.Rectangle packedLocation = packer.insert(resource.widthPx, resource.heightPx, resource);
        if (packedLocation == null) {
            return false;
        }
        addToAtlas(packedLocation, textureAtlas, resource);
        return true;
    }

    private void addToAtlas(RectanglePacker.Rectangle packedLocation, BitmapTextureAtlas textureAtlas, ImageResource resource) {
        mTextureRegions.put(
                resource.textureId,
                resource.isTiled
                        ? BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                                textureAtlas,
                                context,
                                "explosion.png",
                                packedLocation.x,
                                packedLocation.y,
                                resource.tilesSize.first,
                                resource.tilesSize.second)
                        : BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                                textureAtlas,
                                context,
                                resource.filename,
                                packedLocation.x,
                                packedLocation.y));
    }

    private void loadAtlases() {
        for (BitmapTextureAtlas atlas : atlases) {
            atlas.load();
        }
    }
}
