package com.wack.pop2.nuke;

import androidx.annotation.Nullable;

import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.NukeIconEntityUserData;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.icons.BaseIconEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.statemachine.BaseStateMachine;
import com.wack.pop2.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.gameiconstray.GameIconsHostTrayEntity.IconId.NUKE_ICON;
import static com.wack.pop2.nuke.NukeConstants.NUKE_UNLOCK_THRESHOLD;

public class NukeIconEntity extends BaseIconEntity implements BaseStateMachine.Listener<NukeStateMachine.State> {

    private final ButtonUpTouchListener touchListener = new ButtonUpTouchListener() {
        @Override
        protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
            if (nukeStateMachine.getCurrentState() == NukeStateMachine.State.READY) {
                nukerEntity.startNuke();
                return true;
            }
            return false;
        }
    };

    private NukeStateMachine nukeStateMachine;
    private NukerEntity nukerEntity;

    public NukeIconEntity(
            NukeStateMachine nukeStateMachine,
            NukerEntity nukerEntity,
            GameIconsHostTrayEntity gameIconsTrayEntity,
            GameTexturesManager gameTexturesManager,
            GameResources gameResources) {
        super(gameIconsTrayEntity, gameTexturesManager, gameResources);
        this.nukeStateMachine = nukeStateMachine;
        this.nukerEntity = nukerEntity;
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

    @Override
    protected TextureId getIconTextureId() {
        return TextureId.NUKE_ICON;
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

    @Nullable
    @Override
    protected IOnAreaTouchListener getTouchListener() {
        return touchListener;
    }
}
