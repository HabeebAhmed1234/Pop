package com.wack.pop2.icons;

import android.util.Log;

import androidx.annotation.Nullable;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameProgressEventPayload;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

/**
 * The base class for all unlockable tool icons in the game.
 */
public abstract class BaseIconEntity extends BaseEntity implements EventBus.Subscriber {

    private static final IOnAreaTouchListener NO_OP_AREA_TOUCH_LISTENER = new IOnAreaTouchListener() {
        @Override
        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
            return false;
        }
    };

    private Sprite iconSprite;
    private boolean isUnlocked;

    private GameIconsHostTrayEntity gameIconsTrayEntity;
    private GameTexturesManager gameTexturesManager;

    public BaseIconEntity(
            GameIconsHostTrayEntity gameIconsTrayEntity,
            GameTexturesManager gameTexturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.gameIconsTrayEntity = gameIconsTrayEntity;
        this.gameTexturesManager = gameTexturesManager;
    }

    @Override
    public void onCreateScene() {
        createIcon();
        EventBus.get().subscribe(GameEvent.GAME_PROGRESS_CHANGED, this, true);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.GAME_PROGRESS_CHANGED, this);
        iconSprite.removeOnAreaTouchListener();
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        switch (event) {
            case GAME_PROGRESS_CHANGED:
                GameProgressEventPayload progressEventPayload = (GameProgressEventPayload) payload;
                onGameProgressChanged(progressEventPayload.percentProgress);
                break;
        }
    }

    private void createIcon() {
        iconSprite = new Sprite(
                0,
                0,
                gameTexturesManager.getTextureRegion(getIconTextureId()),
                vertexBufferObjectManager);
        setIconColor(AndengineColor.TRANSPARENT);
    }

    private void onGameProgressChanged(float newProgressPercentage) {
        if (newProgressPercentage >= getGameProgressPercentageUnlockThreshold() && !isUnlocked) {
            isUnlocked = true;
            setIconColor(getUnlockedColor());
            addIconToTray();
            onIconUnlocked();
        }
    }

    protected void addIconToTray() {
        @Nullable IOnAreaTouchListener touchListener = getTouchListener();
        gameIconsTrayEntity.addIcon(getIconId(), iconSprite, touchListener == null ? NO_OP_AREA_TOUCH_LISTENER : touchListener);
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

    protected abstract GameIconsHostTrayEntity.IconId getIconId();

    protected abstract float getGameProgressPercentageUnlockThreshold();

    protected abstract void onIconUnlocked();

    protected abstract AndengineColor getUnlockedColor();

    @Nullable
    protected abstract IOnAreaTouchListener getTouchListener();
}
