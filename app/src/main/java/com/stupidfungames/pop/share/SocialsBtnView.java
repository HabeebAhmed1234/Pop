package com.stupidfungames.pop.share;

import android.view.View;
import android.view.View.OnClickListener;
import com.stupidfungames.pop.appreviews.AppReviewUtil;

/**
 * Launches the sharing flow on android to share this app with others. As well as review flow.
 */
public class SocialsBtnView {

  private final SharingManager sharingManager;
  private final ShareHostActivity hostActivity;

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
  private final OnClickListener reviewOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      AppReviewUtil.sendUserToPlayStore(hostActivity.getContext());
    }
  };

  public SocialsBtnView(View shareToAndroidBtn, View shareToFbBtn, View reviewBtn,
      ShareHostActivity hostActivity) {
    this.hostActivity = hostActivity;
    this.sharingManager = new SharingManager(hostActivity);
    shareToAndroidBtn.setOnClickListener(shareToAndroidOnClickListener);
    shareToFbBtn.setOnClickListener(shareToFbOnClickListener);
    reviewBtn.setOnClickListener(reviewOnClickListener);
  }
}
