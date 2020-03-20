package com.wack.pop2.walls;

import android.content.Context;

import com.wack.pop2.fixturedefdata.WallDeleteIconUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.dynamics.Body;

public class WallDeleteIconUtil {

    private static final int WALL_DELETE_ICON_SIZE_DP = 44;

    public static Sprite getWallDeletionSprite(Context context, Line wallSprite, Body wallBody, GameTexturesManager texturesManager, VertexBufferObjectManager vertexBufferObjectManager) {
        float[] wallCoords = wallSprite.getBoundingRectangleCoords();
        final Sprite wallDeleteSprite = new Sprite(
                0,
                0,
                texturesManager.getTextureRegion(TextureId.X_BTN),
                vertexBufferObjectManager);
        wallDeleteSprite.setColor(AndengineColor.RED);

        int iconSize = ScreenUtils.dpToPx(WALL_DELETE_ICON_SIZE_DP, context);

        wallDeleteSprite.setWidth(iconSize);
        wallDeleteSprite.setHeight(iconSize);

        wallDeleteSprite.setX(wallCoords[0] + wallSprite.getBoundingRectangleWidth() / 2 - wallDeleteSprite.getWidth() / 2);
        wallDeleteSprite.setY(wallCoords[1] + wallSprite.getBoundingRectangleHeight() / 2 - wallDeleteSprite.getHeight() / 2);

        wallDeleteSprite.setUserData(new WallDeleteIconUserData(wallSprite, wallBody));
        return wallDeleteSprite;
    }
}
