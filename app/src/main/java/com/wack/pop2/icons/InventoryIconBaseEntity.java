package com.wack.pop2.icons;

import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;

import com.wack.pop2.savegame.SaveGame;
import java.util.HashMap;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.turret.TurretsConstants.MAX_TURRET_INVENTORY;

/**
 * Base class for icons that have inventory text on top of them
 */
public abstract class InventoryIconBaseEntity extends IconBaseEntity {

    private static final float INVENTORY_TEXT_MAX_WIDTH_PX = 20;
    private static final float INVENTORY_TEXT_MAX_HEIGHT_PX = 50;

    private Text inventoryText;
    private int inventoryCount = getMaxInventoryCount();

    public InventoryIconBaseEntity(BinderEnity parent) {
        super(parent);
    }

    protected abstract int getMaxInventoryCount();

    @Override
    public void onLoadGame(SaveGame saveGame) {
        super.onLoadGame(saveGame);
        if (saveGame.gameIconInventories == null) {
            return;
        }
        IconId id = getIconId();
        if (saveGame.gameIconInventories.containsKey(id)) {
            setInventoryCount(saveGame.gameIconInventories.get(id));
        }
    }

    @Override
    public void onSaveGame(SaveGame saveGame) {
        super.onSaveGame(saveGame);
        if (saveGame.gameIconInventories == null) {
            saveGame.gameIconInventories = new HashMap<>();
        }
        saveGame.gameIconInventories.put(getIconId(), inventoryCount);
    }

    public boolean hasInventory() {
        return getInventoryCount() > 0;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();

        Sprite iconSprite = getIconSprite();
        inventoryText = new Text(
            iconSprite.getWidthScaled() / 2 - INVENTORY_TEXT_MAX_WIDTH_PX,
            - INVENTORY_TEXT_MAX_HEIGHT_PX,
            get(GameFontsManager.class).getFont(FontId.INVENTORY_ICON_FONT),
            Integer.toString(inventoryCount),
            (Integer.toString(MAX_TURRET_INVENTORY)).length(),
            vertexBufferObjectManager);
        iconSprite.attachChild(inventoryText);

        onInventoryChanged();
    }

    private void onInventoryChanged() {
        inventoryText.setText(Integer.toString(inventoryCount));
        inventoryText.setColor(isUnlocked() ? getUnlockedIconColor() : AndengineColor.TRANSPARENT);
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

    private void setInventoryCount(int count) {
        inventoryCount = count;
        onInventoryChanged();
    }
}
