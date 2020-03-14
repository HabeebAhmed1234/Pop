package com.wack.pop2.walls;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;

/**
 * Manages the creation of walls that can catch or redirect bubbles
 */
public class WallsManagerEntity extends BaseEntity{

    private WallsStateMachine stateMachine;
    private WallsCreatorEntity wallsCreatorEntity;
    private WallsDeleteIconsManagerEntity wallsDeleteIconsManagerEntity;
    private WallsDeletionEntity wallsDeletionEntity;
    private WallsIconEntity wallsIconEntity;

    public WallsManagerEntity(
            GameIconsHostTrayEntity gameIconsTrayEntity,
            GameAreaTouchListenerEntity gameAreaTouchListenerEntity,
            GameSceneTouchListenerEntity gameSceneTouchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameFontsManager gameFontsManager,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = new WallsStateMachine();
        this.wallsIconEntity = new WallsIconEntity(stateMachine, gameIconsTrayEntity, gameAreaTouchListenerEntity, gameTexturesManager, gameFontsManager, gameResources);
        this.wallsCreatorEntity = new WallsCreatorEntity(stateMachine, wallsIconEntity, gameSceneTouchListenerEntity, gameTexturesManager, gameIconsTrayEntity, gameResources);
        this.wallsDeleteIconsManagerEntity = new WallsDeleteIconsManagerEntity(stateMachine, gameTexturesManager, gameResources);
        this.wallsDeletionEntity = new WallsDeletionEntity(gameAreaTouchListenerEntity, gameResources);
    }
}
