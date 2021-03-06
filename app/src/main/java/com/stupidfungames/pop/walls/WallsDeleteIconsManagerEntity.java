package com.stupidfungames.pop.walls;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.entitymatchers.WallsEntityMatcher;
import com.stupidfungames.pop.fixturedefdata.WallEntityUserData;
import com.stupidfungames.pop.statemachine.BaseStateMachine;

import org.andengine.entity.IEntity;

import java.util.List;

/**
 * Manages the creation and visibility of all the wall delete icons on the map
 */
public class WallsDeleteIconsManagerEntity extends BaseEntity implements BaseStateMachine.Listener<WallsStateMachine.State> {

    public WallsDeleteIconsManagerEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        get(WallsStateMachine.class).addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        get(WallsStateMachine.class).removeAllStateTransitionListener(this);
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
