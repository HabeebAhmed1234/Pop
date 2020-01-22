package com.wack.pop2.nuke;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.BubblePopperEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameIconsTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;

public class NukeManagerEntity extends BaseEntity {

    private NukerEntity nukerEntity;
    private NukeIconEntity nukeIconEntity;

    public NukeManagerEntity(
            BubblePopperEntity bubblePopperEntity,
            GameIconsTrayEntity gameIconsTrayEntity,
            GameTexturesManager texturesManager,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameResources gameResources) {
        super(gameResources);
        this.nukerEntity = new NukerEntity(bubblePopperEntity, gameResources);
        this.nukeIconEntity = new NukeIconEntity(nukerEntity, gameIconsTrayEntity, texturesManager, touchListenerEntity, gameResources);
    }
}
