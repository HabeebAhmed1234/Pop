package com.stupidfungames.pop;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Launches the sharing flow on android to share this app with others.
 */
public class ShareBtnView implements OnClickListener {

  private final HostActivity hostActivity;

  public ShareBtnView(View shareBtn, HostActivity hostActivity) {
    this.hostActivity = hostActivity;
    shareBtn.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    sharingIntent.setType("text/*");
    sharingIntent.putExtra(Intent.EXTRA_TEXT,
        hostActivity.getContext().getString(R.string.game_share_string));
    hostActivity.startActivity(Intent.createChooser(sharingIntent, "Share Pop! with"));
  }
}
