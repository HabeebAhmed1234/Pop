package com.stupidfungames.pop.nuke;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;

public class NukeManagerEntity extends BaseEntity {

    public NukeManagerEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {
        binder.bind(NukeStateMachine.class, new NukeStateMachine());
        binder.bind(NukeCooldownManager.class, new NukeCooldownManager(this));
        binder.bind(NukerEntity.class, new NukerEntity(this));
        binder.bind(NukeIconEntity.class, new NukeIconEntity(this));
    }
}
