package com.wack.pop2.tooltips;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.wack.pop2.R;
import com.wack.pop2.tooltips.androidtooltip.ClosePolicy;
import com.wack.pop2.tooltips.androidtooltip.Tooltip;

import org.andengine.util.time.TimeConstants;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class GameTooltipActivity extends Activity {

    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_IS_ANCHORED = "is_anchored";
    public static final String EXTRA_ANCHOR_X = "anchor_x";
    public static final String EXTRA_ANCHOR_Y = "anchor_y";

    private static final int SHOW_DURATION = TimeConstants.MILLISECONDS_PER_SECOND * 60;

    public static Intent forAnchoredTooltip(String text, int x, int y, Context context) {
        Intent intent = new Intent(context, GameTooltipActivity.class);
        intent.putExtra(EXTRA_TEXT, text);
        intent.putExtra(EXTRA_IS_ANCHORED, true);
        Log.d("asdasd", "showing tooltip with anchor at " + x + ", "+y);
        intent.putExtra(EXTRA_ANCHOR_X, x);
        intent.putExtra(EXTRA_ANCHOR_Y, y);
        return intent;
    }

    public static Intent forUnAnchoredTooltip(String text, Context context) {
        Intent intent = new Intent(context, GameTooltipActivity.class);
        intent.putExtra(EXTRA_TEXT, text);
        intent.putExtra(EXTRA_IS_ANCHORED, false);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_tooltip_activity);

        showTooltip(getIntent());
    }

    private void showTooltip(Intent intent) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(10, 10);

        Bundle extras = intent.getExtras();
        if (extras.getBoolean(EXTRA_IS_ANCHORED)) {
            params.leftMargin = extras.getInt(EXTRA_ANCHOR_X);
            params.topMargin = extras.getInt(EXTRA_ANCHOR_Y);
        } else {
            params.leftMargin = getResources().getDisplayMetrics().widthPixels / 2;
            params.topMargin = getResources().getDisplayMetrics().heightPixels / 2;
        }

        final View anchor = findViewById(R.id.tooltip_anchor_view);
        anchor.setLayoutParams(params);
        anchor.invalidate();

        final Tooltip tooltip = new Tooltip.Builder(this)
                .anchor(anchor, 0, 0, true)
                .text(extras.getString(EXTRA_TEXT))
                .styleId(R.style.ToolTipLayoutDefaultStyle)
                .typeface(ResourcesCompat.getFont(this, R.font.neon))
                .maxWidth((int)(getResources().getDisplayMetrics().widthPixels * 0.8f))
                .arrow(true)
                .closePolicy(new ClosePolicy.Builder().inside(true).outside(false).consume(true).build())
                .showDuration(SHOW_DURATION)
                .overlay(true)
                .create();
        tooltip.doOnHidden(new Function1<Tooltip, Unit>() {
            @Override
            public Unit invoke(Tooltip tooltip) {
                finish();
                overridePendingTransition(0, 0);
                return null;
            }
        });
        anchor.post(new Runnable() {
            @Override
            public void run() {
                tooltip.show(anchor, Tooltip.Gravity.LEFT, true);
            }
        });
    }
}
