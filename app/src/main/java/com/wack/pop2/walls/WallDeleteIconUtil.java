package com.wack.pop2.walls;

import com.wack.pop2.fixturedefdata.WallDeleteIconUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.dynamics.Body;

public class WallDeleteIconUtil {

    public static Sprite getWallDeletionSprite(Line wallSprite, Body wallBody, GameTexturesManager texturesManager, VertexBufferObjectManager vertexBufferObjectManager) {
        ITextureRegion deleteTexture = texturesManager.getTextureRegion(TextureId.DELETE_WALL_ICON);
        float[] wallCoords = wallSprite.getBoundingRectangleCoords();
        final Sprite bubbleSprite = new Sprite(
                wallCoords[0] + wallSprite.getBoundingRectangleWidth() / 2 - deleteTexture.getWidth() / 2,
                wallCoords[1] + wallSprite.getBoundingRectangleHeight() / 2 - deleteTexture.getHeight() / 2,
                deleteTexture,
                vertexBufferObjectManager);
        bubbleSprite.setColor(AndengineColor.RED);
        WallDeleteIconUserData wallDeleteIconUserData = new WallDeleteIconUserData(wallSprite, wallBody);
        bubbleSprite.setUserData(wallDeleteIconUserData);
        return bubbleSprite;
    }
}
