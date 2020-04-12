package com.stupidfungames.pop.nuke;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;

public class NukeManagerBaseEntity extends BaseEntity {

    public NukeManagerBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {
        binder.bind(NukeStateMachine.class, new NukeStateMachine());
        binder.bind(NukeCooldownManager.class, new NukeCooldownManager(this));
        binder.bind(NukerEntity.class, new NukerEntity(this));
        binder.bind(NukeIconBaseEntity.class, new NukeIconBaseEntity(this));
    }
}