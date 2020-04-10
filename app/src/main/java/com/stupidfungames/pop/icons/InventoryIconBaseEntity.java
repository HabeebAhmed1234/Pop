package com.stupidfungames.pop.icons;

import android.content.Context;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.resources.fonts.FontId;
import com.stupidfungames.pop.resources.fonts.GameFontsManager;

import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.utils.ScreenUtils;
import java.util.HashMap;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.color.AndengineColor;

import static com.stupidfungames.pop.turret.TurretsConstants.MAX_TURRET_INVENTORY;

/**
 * Base class for icons that have inventory text on top of them
 */
public abstract class InventoryIconBaseEntity extends IconBaseEntity {

    private static final float INVENTORY_TEXT_MAX_WIDTH_DP = 8;
    private static final float INVENTORY_TEXT_MAX_HEIGHT_DP = 16;

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
            0,
            0,
            get(GameFontsManager.class).getFont(FontId.INVENTORY_ICON_FONT),
            Integer.toString(inventoryCount),
            (Integer.toString(MAX_TURRET_INVENTORY)).length(),
            vertexBufferObjectManager);
        iconSprite.attachChild(inventoryText);

        onInventoryChanged();
    }

    @Override
    protected void addIconToTray() {
        super.addIconToTray();

        // The icon size has now been set so we can correctly set the position of the inventory tex
        // In the icon
        Sprite iconSprite = getIconSprite();
        inventoryText.setX(
            iconSprite.getWidthScaled() / 2 -
                ScreenUtils.dpToPx(INVENTORY_TEXT_MAX_WIDTH_DP, get(Context.class)));
        inventoryText.setY(-ScreenUtils.dpToPx(INVENTORY_TEXT_MAX_HEIGHT_DP, get(Context.class)));
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
