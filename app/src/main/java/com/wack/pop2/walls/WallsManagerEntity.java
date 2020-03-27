package com.wack.pop2.walls;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.tooltips.GameTooltipsEntity;

/**
 * Manages the creation of walls that can catch or redirect bubbles
 */
public class WallsManagerEntity extends BaseEntity{

    private WallsStateMachine stateMachine;
    private WallsCreatorEntity wallsCreatorEntity;
    private WallsDeleteIconsManagerEntity wallsDeleteIconsManagerEntity;
    private WallsDeletionHandlerFactoryEntity wallsDeletionEntity;
    private WallsIconEntity wallsIconEntity;

    public WallsManagerEntity(
            GameIconsHostTrayEntity gameIconsTrayEntity,
            GameSceneTouchListenerEntity gameSceneTouchListenerEntity,
            GameSoundsManager soundsManager,
            GameTooltipsEntity gameTooltips,
            GameTexturesManager gameTexturesManager,
            GameFontsManager gameFontsManager,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = new WallsStateMachine();
        this.wallsIconEntity = new WallsIconEntity(stateMachine, gameIconsTrayEntity, soundsManager, gameTooltips, gameTexturesManager, gameFontsManager, gameResources);
        this.wallsDeletionEntity = new WallsDeletionHandlerFactoryEntity(soundsManager, gameResources);
        this.wallsCreatorEntity = new WallsCreatorEntity(stateMachine, wallsIconEntity, wallsDeletionEntity, gameSceneTouchListenerEntity, gameTexturesManager, gameIconsTrayEntity, soundsManager, gameResources);
        this.wallsDeleteIconsManagerEntity = new WallsDeleteIconsManagerEntity(stateMachine, gameTexturesManager, gameResources);
    }
}
