package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameIconsTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.TurretsIconUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.AndengineColor;

/**
 * Icon used to utilized turrets. Once unlocked this icon appears. The user can then press down on
 * the icon and a turret appears under their finger. They can then drag the turret to where they
 * want it. The icon has a number on it to show the number of turrets in stock. The user can drag
 * turrets back onto the icon to store them this increasing the number turrets in stock on the icon.
 */
class TurretsIconEntity extends BaseEntity implements EventBus.Subscriber, GameAreaTouchListenerEntity.AreaTouchListener {

    private GameAreaTouchListenerEntity touchListenerEntity;
    private GameTexturesManager gameTexturesManager;
    private GameIconsTrayEntity gameIconsTrayEntity;

    // True if the Turrets have been unlocked in the game
    private boolean isUnlocked;
    private Sprite turretIconSprite;

    public TurretsIconEntity(
            GameAreaTouchListenerEntity touchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameIconsTrayEntity gameIconsTrayEntity,
            GameResources gameResources) {
        super(gameResources);
        this.touchListenerEntity = touchListenerEntity;
        this.gameTexturesManager = gameTexturesManager;
        this.gameIconsTrayEntity = gameIconsTrayEntity;
    }

    @Override
    public void onCreateScene() {
        createIcon();

        EventBus.get().subscribe(GameEvent.DIFFICULTY_CHANGE, this);
        touchListenerEntity.addAreaTouchListener(TurretsIconUserData.class, this);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.DIFFICULTY_CHANGE, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        switch (event) {
            case DIFFICULTY_CHANGE:
                DifficultyChangedEventPayload difficultyChangedEventPayload =
                        (DifficultyChangedEventPayload) payload;
                onScoreChanged(difficultyChangedEventPayload.newDifficulty);
                break;
        }
    }

    private void createIcon() {
        ITextureRegion textureRegion =
                gameTexturesManager.getTextureRegion(TextureId.TURRETS_ICON);
        turretIconSprite = new Sprite(
                0,
                0,
                textureRegion,
                vertexBufferObjectManager);
        turretIconSprite.setUserData(new TurretsIconUserData());
        addToSceneWithTouch(turretIconSprite);
        gameIconsTrayEntity.addIcon(turretIconSprite);

        onStateChanged();
    }

    private void onScoreChanged(int newDifficulty) {
        if (newDifficulty >= TurretsConstants.TURRETS_DIFFICULTY_UNLOCK_THRESHOLD && !isUnlocked) {
            isUnlocked = true;
            onStateChanged();
        }
    }

    @Override
    public boolean onTouch(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        return false;
    }

    private void onStateChanged() {
        turretIconSprite.setColor(isUnlocked ? AndengineColor.GREEN : AndengineColor.TRANSPARENT);
    }
}