package com.wack.pop2.walls;

import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameIconsTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.WallsIconUserData;
import com.wack.pop2.icons.BaseInventoryIconEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.walls.WallsConstants.MAX_WALLS_INVENTORY;
import static com.wack.pop2.walls.WallsConstants.WALLS_DIFFICULTY_UNLOCK_THRESHOLD;

public class WallsIconEntity extends BaseInventoryIconEntity implements GameAreaTouchListenerEntity.AreaTouchListener, BaseStateMachine.Listener<WallsStateMachine.State> {

    private WallsStateMachine stateMachine;
    private GameAreaTouchListenerEntity touchListenerEntity;

    public WallsIconEntity(
            WallsStateMachine stateMachine,
            GameIconsTrayEntity gameIconsTrayEntity,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameFontsManager fontsManager,
            GameResources gameResources) {
        super(fontsManager, gameIconsTrayEntity, gameTexturesManager, touchListenerEntity, gameResources);
        this.stateMachine = stateMachine;
        this.touchListenerEntity = touchListenerEntity;
    }

    @Override
    public boolean onTouch(
            TouchEvent touchEvent,
            ITouchArea pTouchArea,
            float pTouchAreaLocalX,
            float pTouchAreaLocalY) {
        if (touchEvent.isActionUp()) {
            if (stateMachine.getCurrentState() == WallsStateMachine.State.UNLOCKED_TOGGLED_OFF) {
                stateMachine.transitionState(WallsStateMachine.State.TOGGLED_ON);
                return true;
            } else if (stateMachine.getCurrentState() == WallsStateMachine.State.TOGGLED_ON) {
                stateMachine.transitionState(WallsStateMachine.State.UNLOCKED_TOGGLED_OFF);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEnterState(WallsStateMachine.State newState) {
        AndengineColor color = AndengineColor.WHITE;
        switch (newState) {
            case LOCKED:
                color = AndengineColor.TRANSPARENT;
                break;
            case UNLOCKED_TOGGLED_OFF:
                color = AndengineColor.WHITE;
                break;
            case TOGGLED_ON:
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
        touchListenerEntity.addAreaTouchListener(WallsIconUserData.class, this);
        stateMachine.addAllStateTransitionListener(this);

        EventBus.get().subscribe(GameEvent.WALL_PLACED, this).subscribe(GameEvent.WALL_DELETED, this);
    }

    @Override
    public void onDestroy() {
        touchListenerEntity.removeAreaTouchListener(WallsIconUserData.class, this);
        stateMachine.removeAllStateTransitionListener(this);

        EventBus.get().unSubscribe(GameEvent.WALL_PLACED, this).unSubscribe(GameEvent.WALL_DELETED, this);
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
        }
    }

    @Override
    protected TextureId getIconTextureId() {
        return TextureId.WALLS_ICON;
    }

    @Override
    protected Class<? extends BaseEntityUserData> getIconUserDataType() {
        return WallsIconUserData.class;
    }

    @Override
    protected BaseEntityUserData getUserData() {
        return new WallsIconUserData();
    }

    @Override
    protected GameIconsTrayEntity.IconId getIconId() {
        return GameIconsTrayEntity.IconId.WALLS_ICON;
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
}
