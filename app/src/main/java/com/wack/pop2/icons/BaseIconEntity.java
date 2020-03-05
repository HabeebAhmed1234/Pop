package com.wack.pop2.icons;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameIconsTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

/**
 * The base class for all unlockable tool icons in the game.
 */
public abstract class BaseIconEntity extends BaseEntity implements GameAreaTouchListenerEntity.AreaTouchListener, EventBus.Subscriber {

    private Sprite iconSprite;
    private boolean isUnlocked;
    private BaseEntityUserData iconUserData;

    private GameIconsTrayEntity gameIconsTrayEntity;
    private GameTexturesManager gameTexturesManager;
    private GameAreaTouchListenerEntity touchListenerEntity;

    public BaseIconEntity(
            GameIconsTrayEntity gameIconsTrayEntity,
            GameTexturesManager gameTexturesManager,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameResources gameResources) {
        super(gameResources);
        this.gameIconsTrayEntity = gameIconsTrayEntity;
        this.gameTexturesManager = gameTexturesManager;
        this.touchListenerEntity = touchListenerEntity;
    }

    @Override
    public void onCreateScene() {
        createIcon();

        EventBus.get().subscribe(GameEvent.DIFFICULTY_CHANGE, this, true);
        touchListenerEntity.addAreaTouchListener(getIconUserDataType(), this);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.DIFFICULTY_CHANGE, this);
        touchListenerEntity.removeAreaTouchListener(getIconUserDataType(), this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        switch (event) {
            case DIFFICULTY_CHANGE:
                DifficultyChangedEventPayload difficultyChangedEventPayload =
                        (DifficultyChangedEventPayload) payload;
                onScoreChanged(difficultyChangedEventPayload.newSpawnInterval);
                break;
        }
    }

    private void createIcon() {
        iconSprite = new Sprite(
                0,
                0,
                gameTexturesManager.getTextureRegion(getIconTextureId()),
                vertexBufferObjectManager);
        iconSprite.setUserData(getUserDataInternal());
        addToSceneWithTouch(iconSprite);
        setIconColor(AndengineColor.TRANSPARENT);
        gameIconsTrayEntity.addIcon(getIconId(), iconSprite);
    }

    private BaseEntityUserData getUserDataInternal() {
        if (iconUserData == null) {
            iconUserData = getUserData();
        }
        return iconUserData;
    }
    private void onScoreChanged(float newDifficultySpawnInterval) {
        if (newDifficultySpawnInterval >= getDifficultyIntervalUnlockThreshold() && !isUnlocked) {
            isUnlocked = true;
            setIconColor(getUnlockedColor());
            onIconUnlocked();
        }
    }

    protected Sprite getIconSprite () {
        return iconSprite;
    }

    protected void setIconColor(AndengineColor color) {
        iconSprite.setColor(color);
    }

    protected boolean isUnlocked() {
        return isUnlocked;
    }

    protected abstract TextureId getIconTextureId();

    protected abstract Class<? extends BaseEntityUserData> getIconUserDataType();

    protected abstract BaseEntityUserData getUserData();

    protected abstract GameIconsTrayEntity.IconId getIconId();

    protected abstract float getDifficultyIntervalUnlockThreshold();

    protected abstract void onIconUnlocked();

    protected abstract AndengineColor getUnlockedColor();
}
