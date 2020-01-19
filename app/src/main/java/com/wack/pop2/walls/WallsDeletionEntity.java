package com.wack.pop2.walls;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.WallDeleteIconUserData;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.shape.IShape;
import org.andengine.input.touch.TouchEvent;

/**
 * Detects clicks on wall delete icons and if the wall tool is active deletes the wall on click
 */
public class WallsDeletionEntity extends BaseEntity implements GameAreaTouchListenerEntity.AreaTouchListener {

    private GameAreaTouchListenerEntity touchListenerEntity;

    public WallsDeletionEntity(
            GameAreaTouchListenerEntity touchListenerEntity,
            GameResources gameResources) {
        super(gameResources);
        this.touchListenerEntity = touchListenerEntity;
    }

    @Override
    public void onCreateScene() {
        touchListenerEntity.addAreaTouchListener(WallDeleteIconUserData.class, this);
    }

    @Override
    public void onDestroy() {
        touchListenerEntity.removeAreaTouchListener(WallDeleteIconUserData.class, this);
    }

    @Override
    public boolean onTouch(TouchEvent sceneTouchEvent, ITouchArea touchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if (sceneTouchEvent.isActionUp()) {
            IEntity deleteIconEntity = (IEntity) touchArea;
            if (deleteIconEntity.isVisible()) {
                deleteWall((IShape) deleteIconEntity);
            }
            return true;
        }
        return false;
    }

    private void deleteWall(IShape deleteIconEntity) {
        WallDeleteIconUserData wallDeleteIconUserData = (WallDeleteIconUserData) deleteIconEntity.getUserData();
        removeFromScene(wallDeleteIconUserData.wallSprite);
        removeFromScene(wallDeleteIconUserData.wallBody);
        removeFromScene(deleteIconEntity);
    }
}
