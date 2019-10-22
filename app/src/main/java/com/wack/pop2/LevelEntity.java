package com.wack.pop2;

import com.wack.pop2.gamesingletons.PhysicsWorldSingleton;
import com.wack.pop2.gamesingletons.SceneSingleton;
import com.wack.pop2.gamesingletons.VertBuffSingleton;
import com.wack.pop2.physics.PhysicsFactory;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * This class creates the level with all of its physics entities. This includes the walls and
 * other obstacles
 */
public class LevelEntity extends BaseEntity {

    private final int levelWidthPx;
    private final int levelHeightPx;

    public LevelEntity(int levelWidthPx, int levelHeightPx, GameResources gameResources) {
        super(gameResources);
        this.levelWidthPx = levelWidthPx;
        this.levelHeightPx = levelHeightPx;
    }

    @Override
    public void onCreateScene() {
        createLevel();
    }

    private void createLevel() {
        final Rectangle left = new Rectangle(0, 0, 2, levelHeightPx, VertBuffSingleton.get());
        final Rectangle right = new Rectangle(levelWidthPx - 2, 0, 2, levelHeightPx, VertBuffSingleton.get());
        left.setAlpha(0);
        right.setAlpha(0);
        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

        PhysicsFactory.createBoxBody(PhysicsWorldSingleton.instanceOf(), left, BodyType.STATIC, wallFixtureDef);
        PhysicsFactory.createBoxBody(PhysicsWorldSingleton.instanceOf(), right, BodyType.STATIC, wallFixtureDef);

        SceneSingleton.instanceOf().attachChild(left);
        SceneSingleton.instanceOf().attachChild(right);

        // set background color
        SceneSingleton.instanceOf().setBackground(new Background(AndengineColor.WHITE));
    }
}
