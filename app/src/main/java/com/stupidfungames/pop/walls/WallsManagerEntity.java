package com.stupidfungames.pop.walls;

import static com.stupidfungames.pop.eventbus.GameEvent.CANCEL_WALL_PLACEMENT;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventBus.Subscriber;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;

/**
 * Manages the creation of walls that can catch or redirect bubbles
 */
public class WallsManagerEntity extends BaseEntity implements Subscriber {

  public WallsManagerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    binder.bind(WallsStateMachine.class, new WallsStateMachine());
    binder.bind(WallsInventoryIconEntity.class, new WallsInventoryIconEntity(this));
    binder
        .bind(WallsDeletionHandlerFactoryEntity.class, new WallsDeletionHandlerFactoryEntity(this));
    binder.bind(WallsCreatorEntity.class, new WallsCreatorEntity(this));
    binder.bind(WallsDeleteIconsManagerEntity.class, new WallsDeleteIconsManagerEntity(this));
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    EventBus.get().subscribe(CANCEL_WALL_PLACEMENT, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get().unSubscribe(CANCEL_WALL_PLACEMENT, this);
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    if (event == CANCEL_WALL_PLACEMENT) {
      cancelWallPlacement();
    }
  }

  public void cancelWallPlacement() {
    get(WallsInventoryIconEntity.class).toggleOff();
    get(WallsCreatorEntity.class).cancelWallPlacement();
  }
}
