package com.wack.pop2.walls;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.WallDeleteIconUserData;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.shape.IShape;
import org.andengine.input.touch.TouchEvent;

/**
 * Detects clicks on wall delete icons and if the wall tool is active deletes the wall on click
 */
public class WallsDeletionHandlerFactoryEntity extends BaseEntity {

    private GameSoundsManager soundsManager;

    public WallsDeletionHandlerFactoryEntity(
            GameSoundsManager soundsManager,
            GameResources gameResources) {
        super(gameResources);
        this.soundsManager = soundsManager;
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

            soundsManager.getSound(SoundId.SCRAP).play();
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
