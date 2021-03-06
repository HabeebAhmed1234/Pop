package com.stupidfungames.pop.icons;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.resources.fonts.FontId;
import com.stupidfungames.pop.resources.fonts.GameFontsManager;
import com.stupidfungames.pop.savegame.SaveGame;
import java.util.HashMap;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.color.AndengineColor;

/**
 * Base class for icons that have inventory text on top of them
 */
public abstract class BaseInventoryIconEntity extends BaseUpgradeableIconEntity {

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
        2,
        vertexBufferObjectManager);
    addToScene(iconSprite, inventoryText);

    onInventoryChanged();
  }

  @Override
  protected void addIconToTray() {
    super.addIconToTray();
    onInventoryChanged();
  }

  protected void onInventoryChanged() {
    inventoryText.setText(Integer.toString(inventoryCount));
    inventoryText.setColor(isUnlocked() ? getUnlockedIconColor() : AndengineColor.TRANSPARENT);
    updateInventoryTextPosition();
  }

  private void updateInventoryTextPosition() {
    // The icon size has now been set so we can correctly set the position of the inventory tex
    // In the icon
    Sprite iconSprite = getIconSprite();
    // slightly offset the text to the left since numbers are right weighted.
    inventoryText
        .setX((iconSprite.getWidth() / 2) * (inventoryCount == 1 ? 0.9f : 1)
            - inventoryText.getWidth() / 2);
    inventoryText.setY(-(inventoryText.getHeight() * 0.75f));
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
