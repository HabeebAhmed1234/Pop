package com.stupidfungames.pop.walls;

import androidx.annotation.Nullable;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.icons.InventoryIconBaseEntity;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.statemachine.BaseStateMachine;
import com.stupidfungames.pop.tooltips.TooltipId;
import com.stupidfungames.pop.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.stupidfungames.pop.GameConstants.WALLS_DIFFICULTY_UNLOCK_THRESHOLD;
import static com.stupidfungames.pop.walls.WallsConstants.MAX_WALLS_INVENTORY;

public class WallsIconEntity extends InventoryIconBaseEntity implements BaseStateMachine.Listener<WallsStateMachine.State> {

    private static final int NUM_UPGRADES = 3;

    public WallsIconEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onEnterState(WallsStateMachine.State newState) {
        AndengineColor color = AndengineColor.WHITE;
        GameSoundsManager soundsManager = get(GameSoundsManager.class);
        switch (newState) {
            case LOCKED:
                color = AndengineColor.TRANSPARENT;
                break;
            case UNLOCKED_TOGGLED_OFF:
                soundsManager.getSound(SoundId.CLICK_DOWN).play();
                color = AndengineColor.WHITE;
                break;
            case TOGGLED_ON:
                soundsManager.getSound(SoundId.CLICK_UP).play();
                color = AndengineColor.GREEN;
                break;
        }
        setIconColor(color);
    }

    @Override
    protected int getMaxInventoryCount() {
        return MAX_WALLS_INVENTORY;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();

        get(WallsStateMachine.class).addAllStateTransitionListener(this);

        EventBus.get()
                .subscribe(GameEvent.WALL_PLACED, this)
                .subscribe(GameEvent.WALL_DELETED, this)
                .subscribe(GameEvent.GAME_ICONS_TRAY_CLOSED, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        get(WallsStateMachine.class).removeAllStateTransitionListener(this);

        EventBus.get()
                .unSubscribe(GameEvent.WALL_PLACED, this)
                .unSubscribe(GameEvent.WALL_DELETED, this)
                .unSubscribe(GameEvent.GAME_ICONS_TRAY_CLOSED, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        super.onEvent(event, payload);

        switch (event) {
            case WALL_PLACED:
                decreaseInventory();
                break;
            case WALL_DELETED:
                increaseInventory();
                break;
            case GAME_ICONS_TRAY_CLOSED:
                toggleOff();
                break;
        }
    }

    @Override
    protected TextureId getIconTextureId() {
        return TextureId.WALLS_ICON;
    }

    @Override
    protected GameIconsHostTrayEntity.IconId getIconId() {
        return GameIconsHostTrayEntity.IconId.WALLS_ICON;
    }

    @Override
    protected float getGameProgressPercentageUnlockThreshold() {
        return WALLS_DIFFICULTY_UNLOCK_THRESHOLD;
    }

    @Override
    protected int getIconUpgradesQuantity() {
        return NUM_UPGRADES;
    }

    @Override
    protected void onIconUnlocked() {
        get(WallsStateMachine.class).transitionState(WallsStateMachine.State.UNLOCKED_TOGGLED_OFF);
    }

    @Override
    protected AndengineColor getUnlockedIconColor() {
        return AndengineColor.GREEN;
    }

    @Nullable
    @Override
    protected IOnAreaTouchListener getTouchListener() {
        return new ButtonUpTouchListener() {
            @Override
            protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                WallsStateMachine stateMachine = get(WallsStateMachine.class);
                if (stateMachine.getCurrentState() == WallsStateMachine.State.UNLOCKED_TOGGLED_OFF) {
                    toggleOn();
                    return true;
                } else if (stateMachine.getCurrentState() == WallsStateMachine.State.TOGGLED_ON) {
                    toggleOff();
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    protected TooltipId getIconTooltipId() {
        return TooltipId.WALLS_ICON_TOOLTIP;
    }

    private void toggleOn() {
        WallsStateMachine stateMachine = get(WallsStateMachine.class);
        if (stateMachine.getCurrentState() == WallsStateMachine.State.UNLOCKED_TOGGLED_OFF) {
            stateMachine.transitionState(WallsStateMachine.State.TOGGLED_ON);
        }
    }

    private void toggleOff() {
        WallsStateMachine stateMachine = get(WallsStateMachine.class);
        if (stateMachine.getCurrentState() == WallsStateMachine.State.TOGGLED_ON) {
            stateMachine.transitionState(WallsStateMachine.State.UNLOCKED_TOGGLED_OFF);
        }
    }
}
