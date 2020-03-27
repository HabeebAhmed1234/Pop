package com.wack.pop2.nuke;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.bubblepopper.BufferedBubblePopperEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.tooltips.GameTooltipsEntity;

public class NukeManagerEntity extends BaseEntity {

    private NukeStateMachine nukeStateMachine;
    private NukeCooldownManager nukeCooldownManager;
    private NukerEntity nukerEntity;
    private NukeIconEntity nukeIconEntity;

    public NukeManagerEntity(
            BufferedBubblePopperEntity bubblePopperEntity,
            GameSoundsManager soundsManager,
            GameIconsHostTrayEntity gameIconsTrayEntity,
            GameTooltipsEntity gameTooltips,
            GameTexturesManager texturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.nukeStateMachine = new NukeStateMachine();
        this.nukeCooldownManager = new NukeCooldownManager(nukeStateMachine, gameResources);
        this.nukerEntity = new NukerEntity(nukeStateMachine, bubblePopperEntity, soundsManager, gameResources);
        this.nukeIconEntity = new NukeIconEntity(nukeStateMachine, nukerEntity, gameIconsTrayEntity, gameTooltips, texturesManager, gameResources);
    }
}
