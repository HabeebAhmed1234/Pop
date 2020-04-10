package com.stupidfungames.pop.walls;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;

/**
 * Manages the creation of walls that can catch or redirect bubbles
 */
public class WallsManagerBaseEntity extends BaseEntity {

    public WallsManagerBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {
        binder.bind(WallsStateMachine.class, new WallsStateMachine());
        binder.bind(WallsIconEntity.class, new WallsIconEntity(this));
        binder.bind(WallsDeletionHandlerFactoryEntity.class, new WallsDeletionHandlerFactoryEntity(this));
        binder.bind(WallsCreatorEntity.class, new WallsCreatorEntity(this));
        binder.bind(WallsDeleteIconsManagerEntity.class, new WallsDeleteIconsManagerEntity(this));
    }
}
