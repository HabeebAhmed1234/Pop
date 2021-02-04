package com.stupidfungames.pop.walls;

import android.content.Context;
import android.content.res.Resources;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.fixturedefdata.WallDeleteIconUserData;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.dynamics.Body;

public class WallDeleteIconFactory {

  public static Sprite getWallDeletionSprite(Line wallSprite, Body wallBody,
      GameTexturesManager texturesManager, VertexBufferObjectManager vertexBufferObjectManager,
      Context context) {
    float[] wallCoords = wallSprite.getBoundingRectangleCoords();
    final Sprite wallDeleteSprite = new Sprite(
        0,
        0,
        texturesManager.getTextureRegion(TextureId.X_BTN),
        vertexBufferObjectManager);
    wallDeleteSprite.setColor(AndengineColor.RED);

    Resources resources = context.getResources();
    float wallDeleteIconSizePx = resources.getDimension(R.dimen.wall_delete_icon_size);
    wallDeleteSprite.setWidth(wallDeleteIconSizePx);
    wallDeleteSprite.setHeight(wallDeleteIconSizePx);

    wallDeleteSprite.setX(wallCoords[0] + wallSprite.getBoundingRectangleWidth() / 2
        - wallDeleteSprite.getWidth() / 2);
    wallDeleteSprite.setY(wallCoords[1] + wallSprite.getBoundingRectangleHeight() / 2
        - wallDeleteSprite.getHeight() / 2);

    wallDeleteSprite.setUserData(new WallDeleteIconUserData(wallSprite, wallBody));
    return wallDeleteSprite;
  }
}
