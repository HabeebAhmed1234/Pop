package com.stupidfungames.pop.walls;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.fixturedefdata.WallDeleteIconUserData;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.shape.IShape;
import org.andengine.input.touch.TouchEvent;

/**
 * Detects clicks on wall delete icons and if the wall tool is active deletes the wall on click
 */
public class WallsDeletionHandlerFactoryEntity extends BaseEntity {

    public WallsDeletionHandlerFactoryEntity(BinderEnity parent) {
        super(parent);
    }

    public WallDeletionHandler getWallDeletionHandler() {
        return new WallDeletionHandler();
    }

    public class WallDeletionHandler extends ButtonUpTouchListener {

        private WallDeletionHandler() {}

        private void deleteWall(IShape deleteIconEntity) {
            WallDeleteIconUserData wallDeleteIconUserData = (WallDeleteIconUserData) deleteIconEntity.getUserData();
            removeFromScene(wallDeleteIconUserData.wallSprite);
            removeFromScene(wallDeleteIconUserData.wallBody);
            removeFromScene(deleteIconEntity);

            EventBus.get().sendEvent(GameEvent.WALL_DELETED);

            get(GameSoundsManager.class).getSound(SoundId.SCRAP).play();
        }

        @Override
        protected boolean onButtonPressed(TouchEvent sceneTouchEvent, ITouchArea touchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
            IEntity deleteIconEntity = (IEntity) touchArea;
            if (deleteIconEntity.isVisible()) {
                deleteWall((IShape) deleteIconEntity);
                return true;
            }
            return false;
        }
    }
}
