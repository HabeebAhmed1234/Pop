package com.wack.pop2.nuke;

import com.wack.pop2.areatouch.GameAreaTouchListenerEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.NukeIconEntityUserData;
import com.wack.pop2.icons.BaseIconEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.gameiconstray.GameIconsHostTrayEntity.IconId.NUKE_ICON;
import static com.wack.pop2.nuke.NukeConstants.NUKE_UNLOCK_THRESHOLD;

public class NukeIconEntity extends BaseIconEntity implements BaseStateMachine.Listener<NukeStateMachine.State> {

    private NukeStateMachine nukeStateMachine;
    private NukerEntity nukerEntity;

    public NukeIconEntity(
            NukeStateMachine nukeStateMachine,
            NukerEntity nukerEntity,
            GameIconsHostTrayEntity gameIconsTrayEntity,
            GameTexturesManager gameTexturesManager,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameResources gameResources) {
        super(gameIconsTrayEntity, gameTexturesManager, touchListenerEntity, gameResources);
        this.nukeStateMachine = nukeStateMachine;
        this.nukerEntity = nukerEntity;
    }

    @Override
    protected TextureId getIconTextureId() {
        return TextureId.NUKE_ICON;
    }

    @Override
    protected Class<? extends BaseEntityUserData> getIconUserDataType() {
        return NukeIconEntityUserData.class;
    }

    @Override
    protected BaseEntityUserData getUserData() {
        return new NukeIconEntityUserData();
    }

    @Override
    protected GameIconsHostTrayEntity.IconId getIconId() {
        return NUKE_ICON;
    }

    @Override
    protected float getDifficultyIntervalUnlockThreshold() {
        return NUKE_UNLOCK_THRESHOLD;
    }

    @Override
    protected void onIconUnlocked() {
        nukeStateMachine.transitionState(NukeStateMachine.State.READY);
    }

    @Override
    protected AndengineColor getUnlockedColor() {
        return AndengineColor.GREEN;
    }

    @Override
    public boolean onTouch(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if (nukeStateMachine.getCurrentState() == NukeStateMachine.State.READY && pSceneTouchEvent.isActionUp()) {
            nukerEntity.startNuke();
            return true;
        }
        return false;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        nukeStateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nukeStateMachine.removeAllStateTransitionListener(this);
    }

    @Override
    public void onEnterState(NukeStateMachine.State newState) {
        AndengineColor color = AndengineColor.TRANSPARENT;

        switch (newState) {
            case LOCKED:
                color = AndengineColor.TRANSPARENT;
                break;
            case READY:
                color = AndengineColor.GREEN;
                break;
            case NUKING:
                color = AndengineColor.YELLOW;
                break;
            case COOLDOWN:
                color = AndengineColor.RED;
                break;
        }

        setIconColor(color);
    }
}
