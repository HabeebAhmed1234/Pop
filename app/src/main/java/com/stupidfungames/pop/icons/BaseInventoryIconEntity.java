package com.stupidfungames.pop.icons;

import static com.stupidfungames.pop.turrets.TurretsInventoryIconEntity.STARTING_TURRET_INVENTORY;

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

/**
 * Base class for icons that have inventory text on top of them
 */
public abstract class BaseInventoryIconEntity extends BaseUpgradeableIconEntity {

  private static final float INVENTORY_TEXT_MAX_WIDTH_DP = 8;
  private static final float INVENTORY_TEXT_MAX_HEIGHT_DP = 20;

  private Text inventoryText;
  private int inventoryCount = getStartingInventoryCount();

  public BaseInventoryIconEntity(BinderEnity parent) {
    super(parent);
  }

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
        (Integer.toString(STARTING_TURRET_INVENTORY)).length(),
        vertexBufferObjectManager);
    addToScene(iconSprite, inventoryText);

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

    onInventoryChanged();
  }

  protected void onInventoryChanged() {
    inventoryText.setText(Integer.toString(inventoryCount));
    inventoryText.setColor(isUnlocked() ? getUnlockedIconColor() : AndengineColor.TRANSPARENT);
  }

  protected void increaseInventory() {
    inventoryCount++;
    onInventoryChanged();
  }

  public void decreaseInventory() {
    inventoryCount--;
    onInventoryChanged();
  }

  protected int getInventoryCount() {
    return inventoryCount;
  }

  @Override
  protected void onUpgraded(int previousUpgradeLevel, int newUpgradeLevel) {
    increaseInventory(
        (newUpgradeLevel - previousUpgradeLevel) * getInventoryCountIncrementPerUpgrade());
  }

  private void increaseInventory(int increment) {
    inventoryCount += increment;
    onInventoryChanged();
  }

  private void setInventoryCount(int count) {
    inventoryCount = count;
    onInventoryChanged();
  }

  protected abstract int getStartingInventoryCount();

  protected abstract int getInventoryCountIncrementPerUpgrade();
}
