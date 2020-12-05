package com.stupidfungames.pop.turrets;

import com.stupidfungames.pop.GameSceneTouchListenerEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.icons.BaseInventoryIconEntity;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tooltips.TooltipId;

import com.stupidfungames.pop.turrets.turret.TurretEntity;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.stupidfungames.pop.GameConstants.TURRETS_DIFFICULTY_UNLOCK_THRESHOLD;
import static com.stupidfungames.pop.turrets.TurretsConstants.MAX_TURRET_INVENTORY;
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
class TurretsInventoryIconEntity extends BaseInventoryIconEntity implements EventBus.Subscriber, GameSceneTouchListenerEntity.SceneTouchListener {

    private static final int NUM_UPGRADES = 3;

    // True if we are currently spawning
    private boolean isSpawning;

    public TurretsInventoryIconEntity(BinderEnity parent) {
        super(parent);
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
                onTurretDocked();
                break;
        }
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        EventBus.get().subscribe(GameEvent.TURRET_DOCKED, this);
        get(GameSceneTouchListenerEntity.class).addSceneTouchListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.get().unSubscribe(GameEvent.TURRET_DOCKED, this);
        get(GameSceneTouchListenerEntity.class).removeSceneTouchListener(this);
    }

    private void onTurretDocked() {
        get(GameSoundsManager.class).getSound(SoundId.PUFF).play();
        increaseInventory();
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
            // play the sound for undocking a turret
            get(GameSoundsManager.class).getSound(SoundId.PUFF).play();
            // Create a turret and set it to be dragging
            TurretEntity turretEntity = get(TurretEntityCreator.class).createTurret((int) touchEvent.getX(), (int) touchEvent.getY());
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
        return !isSpawning && !get(TurretsMutex.class).isDragging() && getIconSprite().contains(touchEvent.getX(), touchEvent.getY()) && hasInventory();
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
    protected GameIconsHostTrayEntity.IconId getIconId() {
        return GameIconsHostTrayEntity.IconId.TURRETS_ICON;
    }

    @Override
    protected float getGameProgressPercentageUnlockThreshold() {
        return TURRETS_DIFFICULTY_UNLOCK_THRESHOLD;
    }

    @Override
    protected int getIconUpgradesQuantity() {
        return NUM_UPGRADES;
    }

    @Override
    protected void onIconUnlocked() {}

    @Override
    protected AndengineColor getUnlockedIconColor() {
        return AndengineColor.GREEN;
    }

    @Override
    protected IOnAreaTouchListener getTouchListener() {
        /** NOOP **/
        return null;
    }

    @Override
    protected TooltipId getIconTooltipId() {
        return TooltipId.TURRET_ICON_TOOLTIP;
    }
}