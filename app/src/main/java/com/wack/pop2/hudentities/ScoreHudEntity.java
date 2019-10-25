package com.wack.pop2.hudentities;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.ScreenUtils;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

/**
 * Entity that contains score hud panel
 */
public class ScoreHudEntity extends BaseEntity {

    private GameFontsManager fontsManager;
    private GameTexturesManager texturesManager;

    private Text scoreText;

    ScoreHudEntity(GameFontsManager fontsManager, GameTexturesManager texturesManager, GameResources gameResources) {
        super(gameResources);
        this.fontsManager = fontsManager;
        this.texturesManager = texturesManager;
    }

    @Override
    public void onCreateScene() {
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
}