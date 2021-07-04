package com.stupidfungames.pop.sword;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;

public class SwordManagerEntity extends BaseEntity {

  public SwordManagerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    binder.bind(SwordStateMachine.class, new SwordStateMachine());
    binder.bind(SwordChargeManager.class, new SwordChargeManager(this));
    binder.bind(SwordAnimationManager.class, new SwordAnimationManager(this));
    binder.bind(Sword.class, new Sword(this));
    binder.bind(SwordIcon.class, new SwordIcon(this));
  }
}
