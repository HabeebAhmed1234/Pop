package com.stupidfungames.pop.hudentities;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import com.stupidfungames.pop.resources.fonts.FontId;
import com.stupidfungames.pop.resources.fonts.GameFontsManager;
import org.andengine.entity.text.Text;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.common.Vec2;

public abstract class HudTextBaseEntity extends BaseEntity {

  private final boolean keepPositionUpdated;
  private Text text;

  public HudTextBaseEntity(BinderEnity parent) {
    super(parent);
    this.keepPositionUpdated = false;
  }

  public HudTextBaseEntity(BinderEnity parent, boolean keepPositionUpdated) {
    super(parent);
    this.keepPositionUpdated = keepPositionUpdated;
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    text = new Text(0, 0,
        get(GameFontsManager.class).getFont(FontId.SCORE_TICKER_FONT), "", getMaxStringLength(),
        vertexBufferObjectManager);
    updateTextPosition();
    text.setColor(getInitialTextColor());
    updateText(getInitialText());
    scene.attachChild(text);
  }

  public float getTextRightEdgePx() {
    return text.getX() + text.getWidthScaled();
  }

  public void updateText(String text) {
    this.text.setText(text);
    if (keepPositionUpdated) {
      updateTextPosition();
    }
  }

  public void updateColor(AndengineColor color) {
    text.setColor(color);
  }

  protected AndengineColor currentTextColor() {
    return text.getColor();
  }

  protected Vec2 getTextCenter() {
    return Vec2Pool.obtain(text.getX() + text.getWidth() / 2, text.getY() + text.getHeight() / 2);
  }

  private void updateTextPosition() {
    int[] position = getTextPositionPx(new int[]{(int) text.getWidth(), (int) text.getHeight()});
    text.setPosition(position[0], position[1]);
  }

  abstract String getInitialText();

  abstract int[] getTextPositionPx(int[] textDimensPx);

  abstract int getMaxStringLength();

  abstract AndengineColor getInitialTextColor();
}
