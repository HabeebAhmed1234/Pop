package com.wack.pop2.turret;

import android.content.Context;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.tooltips.GameTooltipsEntity;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import static com.wack.pop2.utils.GeometryUtils.initSpriteDimens;
import static com.wack.pop2.utils.GeometryUtils.initSpriteDimensCenterPos;

public class TurretEntityCreator extends BaseEntity {

    private static final int TURRET_BODY_SIZE_DP = 88;

    private static final int TURRET_CANNON_HEIGHT_DP = 12;
    private static final int TURRET_CANNON_LENGTH_DP = 32;

    public TurretEntityCreator(BinderEnity parent) {
        super(parent);
    }

    public TurretBaseEntity createTurret(int centerX, int centerY) {
        GameTexturesManager texturesManager = get(GameTexturesManager.class);
        ITextureRegion turretBodyTexture = texturesManager.getTextureRegion(TextureId.BALL);
        ITextureRegion turretCannonTexture = texturesManager.getTextureRegion(TextureId.LINE);

        Sprite turretBodySprite = new Sprite(
                0,
                0,
                turretBodyTexture,
                vertexBufferObjectManager);

        initSpriteDimensCenterPos(get(Context.class), turretBodySprite, centerX, centerY, TURRET_BODY_SIZE_DP);

        addToScene(turretBodySprite);

        final Sprite turretCannonSprite = new Sprite(
                0,
                0,
                turretCannonTexture,
                vertexBufferObjectManager);

        int cannonHeightPx = ScreenUtils.dpToPx(TURRET_CANNON_HEIGHT_DP, get(Context.class));

        initSpriteDimens(
                turretCannonSprite,
                (int) turretBodySprite.getWidth() / 2,
                (int) turretBodySprite.getHeight() / 2 - cannonHeightPx / 2,
                ScreenUtils.dpToPx(TURRET_CANNON_LENGTH_DP, get(Context.class)),
                cannonHeightPx);

        turretBodySprite.attachChild(turretCannonSprite);
        turretCannonSprite.setRotationCenter(0f, cannonHeightPx / 2);

        return new TurretBaseEntity(turretBodySprite, turretCannonSprite, this);
    }
}
