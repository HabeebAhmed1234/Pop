package com.stupidfungames.pop.tooltips;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.tooltips.androidtooltip.ClosePolicy;
import com.stupidfungames.pop.tooltips.androidtooltip.Tooltip;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.andengine.util.ActivityUtils;
import org.andengine.util.time.TimeConstants;

public class GameTooltipActivity extends Activity {

  public static final String EXTRA_PARAMS = "activityParams";
  private static final int SHOW_DURATION = TimeConstants.MILLISECONDS_PER_SECOND * 60;

  private GameTooltipActivityParams activityParams;
  private final TooltipTexts tooltipTexts = new TooltipTexts();

  public static Intent newIntent(GameTooltipActivityParams params, Context context) {
    Intent intent = new Intent(context, GameTooltipActivity.class);
    intent.putExtra(EXTRA_PARAMS, params);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    ActivityUtils.requestFullscreen(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.game_tooltip_activity);
    activityParams = (GameTooltipActivityParams) getIntent().getSerializableExtra(EXTRA_PARAMS);
    showTooltip();
  }

  private void showTooltip() {
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(10, 10);
    boolean isAnchored = activityParams.anchorX != -1 && activityParams.anchorY != -1;
    if (isAnchored) {
      params.leftMargin = activityParams.anchorX;
      params.topMargin = activityParams.anchorY;
    } else {
      params.leftMargin = getResources().getDisplayMetrics().widthPixels / 2;
      params.topMargin = getResources().getDisplayMetrics().heightPixels / 2;
    }

    final View anchor = findViewById(R.id.tooltip_anchor_view);
    anchor.setLayoutParams(params);
    anchor.invalidate();

    final Tooltip tooltip = new Tooltip.Builder(this)
        .anchor(anchor, 0, 0, true)
        .text(tooltipTexts.getTooltipText(activityParams.tooltipId))
        .styleId(R.style.ToolTipLayoutDefaultStyle)
        .typeface(ResourcesCompat.getFont(this, R.font.neon))
        .maxWidth((int) (getResources().getDisplayMetrics().widthPixels * 0.8f))
        .arrow(isAnchored)
        .closePolicy(new ClosePolicy.Builder().inside(true).outside(false).consume(true).build())
        .showDuration(SHOW_DURATION)
        .overlay(isAnchored).create();

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
