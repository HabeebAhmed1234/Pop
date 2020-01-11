package com.wack.pop2.walls;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameIconsTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;

/**
 * Manages the creation of walls that can catch or redirect bubbles
 */
public class WallsManager extends BaseEntity{

    private WallsStateMachine stateMachine;
    private WallsCreatorEntity wallsCreatorEntity;
    private WallsIconEntity wallsIconEntity;

    public WallsManager(
            GameIconsTrayEntity gameIconsTrayEntity,
            GameAreaTouchListenerEntity gameAreaTouchListenerEntity,
            GameSceneTouchListenerEntity touchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = new WallsStateMachine();
        this.wallsCreatorEntity = new WallsCreatorEntity(stateMachine, touchListenerEntity, gameTexturesManager, gameResources);
        this.wallsIconEntity = new WallsIconEntity(stateMachine, gameIconsTrayEntity, gameAreaTouchListenerEntity, gameTexturesManager, gameResources);
    }
}
