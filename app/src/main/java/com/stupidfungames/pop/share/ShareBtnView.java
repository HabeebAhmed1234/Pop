package com.stupidfungames.pop.share;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * Launches the sharing flow on android to share this app with others.
 */
public class ShareBtnView {

  private final SharingManager sharingManager;

  private final OnClickListener shareToAndroidOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      sharingManager.shareToAndroid();
    }
  };
  private final OnClickListener shareToFbOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      sharingManager.shareToFacebook();
    }
  };

  public ShareBtnView(View shareToAndroidBtn, View shareToFbBtn, ShareHostActivity hostActivity) {
    this.sharingManager = new SharingManager(hostActivity);
    shareToAndroidBtn.setOnClickListener(shareToAndroidOnClickListener);
    shareToFbBtn.setOnClickListener(shareToFbOnClickListener);
  }
}
