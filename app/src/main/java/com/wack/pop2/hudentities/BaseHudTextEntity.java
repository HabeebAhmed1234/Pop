package com.wack.pop2.hudentities;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;

import org.andengine.entity.text.Text;
import org.andengine.util.color.AndengineColor;

public abstract class BaseHudTextEntity extends BaseEntity {

    private GameFontsManager fontsManager;
    private Text text;

    public BaseHudTextEntity(GameFontsManager fontsManager, GameResources gameResources) {
        super(gameResources);
        this.fontsManager = fontsManager;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();

        int[] position = getTextPosition();

        text = new Text(position[0], position[1], fontsManager.getFont(FontId.SCORE_TICKER_FONT), "", getMaxStringLength(), vertexBufferObjectManager);
        text.setColor(getTextColor());
        updateText(getInitialText());
        scene.attachChild(text);
    }

    protected void updateText(String text) {
        this.text.setText(text);
    }

    abstract String getInitialText();
    abstract int[] getTextPosition();
    abstract int getMaxStringLength();
    abstract AndengineColor getTextColor();
}
