package com.wack.pop2.icons;

import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameIconsTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;

/**
 * Base class for icons that have inventory text on top of them
 */
public abstract class BaseInventoryIconEntity extends BaseIconEntity {

    private static final float INVENTORY_TEXT_MAX_WIDTH_PX = 20;
    private static final float INVENTORY_TEXT_MAX_HEIGHT_PX = 80;
    
    private GameFontsManager fontsManager;

    private int inventoryCount = getMaxInventoryCount();

    public BaseInventoryIconEntity(
            GameIconsTrayEntity gameIconsTrayEntity,
            GameTexturesManager gameTexturesManager,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameResources gameResources) {
        super(gameIconsTrayEntity, gameTexturesManager, touchListenerEntity, gameResources);
    }

    protected abstract int getMaxInventoryCount();
}
