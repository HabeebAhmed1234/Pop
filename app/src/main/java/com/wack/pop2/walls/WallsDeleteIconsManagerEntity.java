package com.wack.pop2.walls;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.entitymatchers.WallsEntityMatcher;
import com.wack.pop2.fixturedefdata.WallEntityUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.entity.IEntity;

import java.util.List;

/**
 * Manages the creation and visibility of all the wall delete icons on the map
 */
public class WallsDeleteIconsManagerEntity extends BaseEntity implements BaseStateMachine.Listener<WallsStateMachine.State> {

    private WallsStateMachine stateMachine;
    private GameTexturesManager texturesManager;

    public WallsDeleteIconsManagerEntity(
            WallsStateMachine stateMachine,
            GameTexturesManager texturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
        this.texturesManager = texturesManager;
    }

    @Override
    public void onCreateScene() {
        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        stateMachine.removeAllStateTransitionListener(this);
    }

    @Override
    public void onEnterState(WallsStateMachine.State newState) {
        switch (newState) {
            case TOGGLED_ON:
                setWallDeleteIconsVisibility(true);
                break;
            case UNLOCKED_TOGGLED_OFF:
            case LOCKED:
                setWallDeleteIconsVisibility(false);
                break;
        }
    }

    private void setWallDeleteIconsVisibility(boolean visibile) {
        List<IEntity> walls = getAllWalls();

        for (IEntity wallEntity : walls) {
            WallEntityUserData userData = (WallEntityUserData) wallEntity.getUserData();
            if (userData.wallDeleteIcon != null) {
                userData.wallDeleteIcon.setVisible(visibile);
            }
        }
    }

    private List<IEntity> getAllWalls() {
        return scene.query(new WallsEntityMatcher());
    }
}
