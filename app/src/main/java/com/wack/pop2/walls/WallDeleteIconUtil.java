package com.wack.pop2.walls;

import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.AndengineColor;

public class WallDeleteIconUtil {

    public static Sprite getWallDeletionSprite(Line wall, GameTexturesManager texturesManager, VertexBufferObjectManager vertexBufferObjectManager) {
        ITextureRegion deleteTexture = texturesManager.getTextureRegion(TextureId.DELETE_WALL_ICON);
        float[] wallCoords = wall.getBoundingRectangleCoords();
        final Sprite bubbleSprite = new Sprite(
                wallCoords[0] + wall.getBoundingRectangleWidth() / 2 - deleteTexture.getWidth() / 2,
                wallCoords[1] + wall.getBoundingRectangleHeight() / 2 - deleteTexture.getHeight() / 2,
                deleteTexture,
                vertexBufferObjectManager);
        bubbleSprite.setColor(AndengineColor.RED);
        return bubbleSprite;
    }
}
