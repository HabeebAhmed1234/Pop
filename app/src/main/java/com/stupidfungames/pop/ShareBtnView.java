package com.stupidfungames.pop;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Launches the sharing flow on android to share this app with others.
 */
public class ShareBtnView {

  private final ShareHostActivity hostActivity;
  private final OnClickListener shareToAndroidOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      shareToAndroid();
    }
  };
  private final OnClickListener shareToFbOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      shareToFacebook();
    }
  };

  public ShareBtnView(View shareToAndroidBtn, View shareToFbBtn, ShareHostActivity hostActivity) {
    this.hostActivity = hostActivity;
    shareToAndroidBtn.setOnClickListener(shareToAndroidOnClickListener);
    shareToFbBtn.setOnClickListener(shareToFbOnClickListener);
  }

  private void shareToFacebook() {
    if (ShareDialog.canShow(ShareLinkContent.class)) {
      ShareLinkContent content = new ShareLinkContent.Builder()
          .setContentUrl(Uri.parse(hostActivity.getString(R.string.game_share_link)))
          .setShareHashtag(new ShareHashtag.Builder()
              .setHashtag(hostActivity.getString(R.string.game_share_hashtag)).build())
          .build();
      ShareDialog.show(hostActivity.getActivity(), content);
    } else {
      shareToAndroid();
    }
  }

  private void shareToAndroid() {
    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    sharingIntent.setType("text/*");
    sharingIntent.putExtra(Intent.EXTRA_TEXT,
        hostActivity.getString(R.string.game_share_string));
    hostActivity.startActivity(Intent
        .createChooser(sharingIntent, hostActivity.getString(R.string.game_share_string_title)));
  }
}
