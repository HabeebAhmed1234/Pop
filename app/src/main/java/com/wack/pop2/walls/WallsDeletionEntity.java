package com.wack.pop2.walls;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.areatouch.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.WallDeleteIconUserData;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.shape.IShape;
import org.andengine.input.touch.TouchEvent;

/**
 * Detects clicks on wall delete icons and if the wall tool is active deletes the wall on click
 */
public class WallsDeletionEntity extends BaseEntity implements GameAreaTouchListenerEntity.AreaTouchListener {

    private GameAreaTouchListenerEntity touchListenerEntity;
    private GameSoundsManager soundsManager;

    public WallsDeletionEntity(
            GameAreaTouchListenerEntity touchListenerEntity,
            GameSoundsManager soundsManager,
            GameResources gameResources) {
        super(gameResources);
        this.touchListenerEntity = touchListenerEntity;
        this.soundsManager = soundsManager;
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

        EventBus.get().sendEvent(GameEvent.WALL_DELETED);

        soundsManager.getSound(SoundId.SCRAP).play();
    }
}
