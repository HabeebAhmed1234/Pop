package com.wack.pop2.walls;

import androidx.annotation.Nullable;

import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.icons.BaseInventoryIconEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.statemachine.BaseStateMachine;
import com.wack.pop2.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.walls.WallsConstants.MAX_WALLS_INVENTORY;
import static com.wack.pop2.walls.WallsConstants.WALLS_DIFFICULTY_UNLOCK_THRESHOLD;

public class WallsIconEntity extends BaseInventoryIconEntity implements BaseStateMachine.Listener<WallsStateMachine.State> {

    private final ButtonUpTouchListener touchListener = new ButtonUpTouchListener() {
        @Override
        protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
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

    private WallsStateMachine stateMachine;
    private GameSoundsManager gameSoundsManager;

    public WallsIconEntity(
            WallsStateMachine stateMachine,
            GameIconsHostTrayEntity gameIconsTrayEntity,
            GameSoundsManager gameSoundsManager,
            GameTexturesManager gameTexturesManager,
            GameFontsManager fontsManager,
            GameResources gameResources) {
        super(fontsManager, gameIconsTrayEntity, gameTexturesManager, gameResources);
        this.stateMachine = stateMachine;
        this.gameSoundsManager = gameSoundsManager;
    }

    @Override
    public void onEnterState(WallsStateMachine.State newState) {
        AndengineColor color = AndengineColor.WHITE;
        switch (newState) {
            case LOCKED:
                color = AndengineColor.TRANSPARENT;
                break;
            case UNLOCKED_TOGGLED_OFF:
                gameSoundsManager.getSound(SoundId.CLICK_DOWN).play();
                color = AndengineColor.WHITE;
                break;
            case TOGGLED_ON:
                gameSoundsManager.getSound(SoundId.CLICK_UP).play();
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
        stateMachine.addAllStateTransitionListener(this);

        EventBus.get()
                .subscribe(GameEvent.WALL_PLACED, this)
                .subscribe(GameEvent.WALL_DELETED, this)
                .subscribe(GameEvent.GAME_ICONS_TRAY_CLOSE, this);
    }

    @Override
    public void onDestroy() {
        stateMachine.removeAllStateTransitionListener(this);

        EventBus.get()
                .unSubscribe(GameEvent.WALL_PLACED, this)
                .unSubscribe(GameEvent.WALL_DELETED, this)
                .unSubscribe(GameEvent.GAME_ICONS_TRAY_CLOSE, this);
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
            case GAME_ICONS_TRAY_CLOSE:
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
    protected float getDifficultyIntervalUnlockThreshold() {
        return WALLS_DIFFICULTY_UNLOCK_THRESHOLD;
    }

    @Override
    protected void onIconUnlocked() {
        stateMachine.transitionState(WallsStateMachine.State.UNLOCKED_TOGGLED_OFF);
    }

    @Override
    protected AndengineColor getUnlockedColor() {
        return AndengineColor.GREEN;
    }

    @Nullable
    @Override
    protected IOnAreaTouchListener getTouchListener() {
        return touchListener;
    }

    private void toggleOn() {
        if (stateMachine.getCurrentState() == WallsStateMachine.State.UNLOCKED_TOGGLED_OFF) {
            stateMachine.transitionState(WallsStateMachine.State.TOGGLED_ON);
        }
    }

    private void toggleOff() {
        if (stateMachine.getCurrentState() == WallsStateMachine.State.TOGGLED_ON) {
            stateMachine.transitionState(WallsStateMachine.State.UNLOCKED_TOGGLED_OFF);
        }
    }
}
