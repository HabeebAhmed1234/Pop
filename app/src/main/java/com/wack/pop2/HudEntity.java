package com.wack.pop2;

import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

/**
 * Entity that contains
 */
public class HudEntity extends BaseEntity {

    private GameFontsManager fontsManager;
    private GameTexturesManager texturesManager;

    private Text scoreText;
    private Text timerText;

    HudEntity(GameFontsManager fontsManager, GameTexturesManager texturesManager, GameResources gameResources) {
        super(gameResources);
        this.fontsManager = fontsManager;
        this.texturesManager = texturesManager;
    }

    @Override
    public void onCreateScene() {
        createScore();
        createTimer();
    }

    private void createScore() {
        scoreText = new Text(20, 20, fontsManager.getFont(FontId.SCORE_TICKER_FONT), "Score: - - - - -", "Score: XXXXX".length(), vertexBufferObjectManager);

        //set score background
        Sprite scorebackground = new Sprite(0, 0, texturesManager.getTextureRegion(TextureId.SCORE_BACKGROUND), vertexBufferObjectManager);
        scorebackground.setHeight(scoreText.getHeight() + 40f);
        scorebackground.setWidth(scoreText.getWidth() + 40f);
        scorebackground.setY(ScreenUtils.getSreenSize().height-scorebackground.getHeight());
        scoreText.setY(scorebackground.getY()+20);
        scoreText.setColor(1,0,0);

        scene.attachChild(scorebackground);
        scene.attachChild(scoreText);
    }

    private void createTimer() {
        timerText = new Text(20,20, fontsManager.getFont(FontId.SCORE_TICKER_FONT), "Time: 120", "Time: 000".length(), vertexBufferObjectManager);
        Sprite background = new Sprite(0, 0, texturesManager.getTextureRegion(TextureId.SCORE_BACKGROUND), vertexBufferObjectManager));
        background.setHeight(timerText.getHeight()+40f);
        background.setWidth(timerText.getWidth()+40f);
        background.setY(0);
        timerText.setY(background.getY()+20);
        timerText.setColor(1,0,0);

        scene.attachChild(background);
        scene.attachChild(timerText);
    }
}