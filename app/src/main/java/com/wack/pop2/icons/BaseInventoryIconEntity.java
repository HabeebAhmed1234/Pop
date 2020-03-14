package com.wack.pop2.icons;

import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.turret.TurretsConstants.MAX_TURRET_INVENTORY;

/**
 * Base class for icons that have inventory text on top of them
 */
public abstract class BaseInventoryIconEntity extends BaseIconEntity {

    private static final float INVENTORY_TEXT_MAX_WIDTH_PX = 20;
    private static final float INVENTORY_TEXT_MAX_HEIGHT_PX = 50;

    private GameFontsManager fontsManager;

    private Text inventoryText;
    private int inventoryCount = getMaxInventoryCount();

    public BaseInventoryIconEntity(
            GameFontsManager fontsManager,
            GameIconsHostTrayEntity gameIconsTrayEntity,
            GameTexturesManager gameTexturesManager,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameResources gameResources) {
        super(gameIconsTrayEntity, gameTexturesManager, touchListenerEntity, gameResources);
        this.fontsManager = fontsManager;
    }

    protected abstract int getMaxInventoryCount();

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        createInventoryText();
    }

    public boolean hasInventory() {
        return getInventoryCount() > 0;
    }

    private void createInventoryText() {
        Sprite iconSprite = getIconSprite();
        inventoryText = new Text(
                iconSprite.getWidthScaled() / 2 - INVENTORY_TEXT_MAX_WIDTH_PX,
                - INVENTORY_TEXT_MAX_HEIGHT_PX,
                fontsManager.getFont(FontId.TURRET_ICON_FONT),
                Integer.toString(inventoryCount),
                (Integer.toString(MAX_TURRET_INVENTORY)).length(),
                vertexBufferObjectManager);
        iconSprite.attachChild(inventoryText);

        onInventoryChanged();

    }

    private void onInventoryChanged() {
        inventoryText.setText(Integer.toString(inventoryCount));
        inventoryText.setColor(isUnlocked() ? getUnlockedColor() : AndengineColor.TRANSPARENT);
    }

    protected void increaseInventory() {
        inventoryCount++;
        onInventoryChanged();
    }

    protected void decreaseInventory() {
        inventoryCount--;
        onInventoryChanged();
    }

    protected int getInventoryCount() {
        return inventoryCount;
    }
}
