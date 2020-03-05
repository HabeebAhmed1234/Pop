package com.wack.pop2.turret;

import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameIconsTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.TurretsIconUserData;
import com.wack.pop2.icons.BaseInventoryIconEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.turret.TurretsConstants.MAX_TURRET_INVENTORY;
import static com.wack.pop2.turret.TurretsConstants.TURRETS_DIFFICULTY_UNLOCK_THRESHOLD;
import static org.andengine.input.touch.TouchEvent.ACTION_CANCEL;
import static org.andengine.input.touch.TouchEvent.ACTION_DOWN;
import static org.andengine.input.touch.TouchEvent.ACTION_MOVE;
import static org.andengine.input.touch.TouchEvent.ACTION_OUTSIDE;
import static org.andengine.input.touch.TouchEvent.ACTION_UP;

/**
 * Icon used to utilized turrets. Once unlocked this icon appears. The user can then press down on
 * the icon and a turret appears under their finger. They can then drag the turret to where they
 * want it. The icon has a number on it to show the number of turrets in stock. The user can drag
 * turrets back onto the icon to store them this increasing the number turrets in stock on the icon.
 */
class TurretsIconEntity extends BaseInventoryIconEntity implements EventBus.Subscriber, GameSceneTouchListenerEntity.SceneTouchListener {

    private GameSceneTouchListenerEntity touchListenerEntity;
    private TurretEntityCreator turretEntityCreator;
    private TurretsMutex mutex;

    // True if we are currently spawning
    private boolean isSpawning;

    public TurretsIconEntity(
            GameFontsManager gameFontsManager,
            GameAreaTouchListenerEntity areaTouchListenerEntity,
            GameSceneTouchListenerEntity touchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameIconsTrayEntity gameIconsTrayEntity,
            TurretEntityCreator turretEntityCreator,
            TurretsMutex mutex,
            GameResources gameResources) {
        super(gameFontsManager, gameIconsTrayEntity, gameTexturesManager, areaTouchListenerEntity, gameResources);
        this.touchListenerEntity = touchListenerEntity;
        this.turretEntityCreator = turretEntityCreator;
        this.mutex = mutex;
    }

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        boolean handled = false;
        switch (touchEvent.getAction()) {
            case ACTION_DOWN:
                handled = maybeStartUndocking(touchEvent);
                break;
            case ACTION_UP:
                finishedSpawning();
                handled = true;
                break;
            case ACTION_CANCEL:
            case ACTION_OUTSIDE:
            case ACTION_MOVE:
                // NOOP
                break;

        }
        return handled;
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        super.onEvent(event, payload);
        switch (event) {
            case TURRET_DOCKED:
                increaseInventory();
                break;
        }
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        EventBus.get().subscribe(GameEvent.TURRET_DOCKED, this);
        touchListenerEntity.addSceneTouchListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.get().unSubscribe(GameEvent.TURRET_DOCKED, this);
        touchListenerEntity.removeSceneTouchListener(this);
    }

    /**
     * If the user has pressed down on the icon and they are not already dragging a turret then
     * we need to start undocking a new turret. While this new turret is being undocking we cannot
     * undocking another turret.
     *
     * @Return true if we started spawning
     */
    private boolean maybeStartUndocking(TouchEvent touchEvent) {
        if (canUndockTurret(touchEvent)) {
            // Create a turret and set it to be dragging
            TurretEntity turretEntity = turretEntityCreator.createTurret(touchEvent.getX(), touchEvent.getY());
            turretEntity.forceStartDragging(touchEvent.getX(), touchEvent.getY());

            decreaseInventory();
            isSpawning = true;
            return true;
        }
        return false;
    }

    /**
     * Returns true if it is permissible to undock a new turret
     * @return
     */
    private boolean canUndockTurret(TouchEvent touchEvent) {
        return !isSpawning && !mutex.isDragging() && getIconSprite().contains(touchEvent.getX(), touchEvent.getY()) && hasInventory();
    }

    private void finishedSpawning() {
        isSpawning = false;
    }

    @Override
    protected int getMaxInventoryCount() {
        return MAX_TURRET_INVENTORY;
    }

    @Override
    protected TextureId getIconTextureId() {
        return TextureId.TURRETS_ICON;
    }

    @Override
    protected Class<? extends BaseEntityUserData> getIconUserDataType() {
        return TurretsIconUserData.class;
    }

    @Override
    protected BaseEntityUserData getUserData() {
        return new TurretsIconUserData();
    }

    @Override
    protected GameIconsTrayEntity.IconId getIconId() {
        return GameIconsTrayEntity.IconId.TURRETS_ICON;
    }

    @Override
    protected float getDifficultyIntervalUnlockThreshold() {
        return TURRETS_DIFFICULTY_UNLOCK_THRESHOLD;
    }

    @Override
    protected void onIconUnlocked() {}

    @Override
    protected AndengineColor getUnlockedColor() {
        return AndengineColor.GREEN;
    }


    @Override
    public boolean onTouch(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        /** NOOP **/
        return false;
    }
}