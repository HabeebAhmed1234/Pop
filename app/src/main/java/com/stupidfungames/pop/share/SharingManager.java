package com.stupidfungames.pop.share;

import static com.stupidfungames.pop.analytics.Events.SHARE_TO_FB_CANCELLED;
import static com.stupidfungames.pop.analytics.Events.SHARE_TO_FB_ERROR;
import static com.stupidfungames.pop.analytics.Events.SHARE_TO_FB_START;
import static com.stupidfungames.pop.analytics.Events.SHARE_TO_FB_SUCCESS;
import static com.stupidfungames.pop.analytics.Events.SHARE_TO_OTHER;

import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer.Result;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.analytics.Logger;

public class SharingManager {

  private final ShareHostActivity hostActivity;

  private CallbackManager callbackManager;

  public SharingManager(ShareHostActivity hostActivity) {
    this.hostActivity = hostActivity;
  }

  /**
   * Shares to fb and returns a future for when the share is complete
   */
  public ListenableFuture<Boolean> shareToFacebook() {
    if (ShareDialog.canShow(ShareLinkContent.class)) {
      ShareLinkContent content = new ShareLinkContent.Builder()
          .setContentUrl(Uri.parse(hostActivity.getString(R.string.game_share_link)))
          .setShareHashtag(new ShareHashtag.Builder()
              .setHashtag(hostActivity.getString(R.string.game_share_hashtag)).build())
          .build();

      ShareDialog shareDialog = new ShareDialog(hostActivity.getActivity());

      callbackManager = CallbackManager.Factory.create();

      final SettableFuture<Boolean> shareSuccess = SettableFuture.create();
      shareDialog.registerCallback(callbackManager, new FacebookCallback<Result>() {
        @Override
        public void onSuccess(Result result) {
          shareSuccess.set(true);
          Logger.logSelect(hostActivity.getContext(), SHARE_TO_FB_SUCCESS);
        }

        @Override
        public void onCancel() {
          shareSuccess.set(false);
          Logger.logSelect(hostActivity.getContext(), SHARE_TO_FB_CANCELLED);
        }

        @Override
        public void onError(FacebookException error) {
          shareSuccess.set(false);
          Logger.logSelect(hostActivity.getContext(), SHARE_TO_FB_ERROR);
        }
      });

      Logger.logSelect(hostActivity.getContext(), SHARE_TO_FB_START);
      shareDialog.show(content);
      return shareSuccess;
    } else {
      return shareToAndroid();
    }
  }

  public ListenableFuture<Boolean> shareToAndroid() {
    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    sharingIntent.setType("text/*");
    sharingIntent.putExtra(Intent.EXTRA_TEXT,
        hostActivity.getString(R.string.game_share_string));

    final SettableFuture<Boolean> shareSuccess = SettableFuture.create();
    hostActivity
        .prepareCall(new StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
          @Override
          public void onActivityResult(ActivityResult result) {
            shareSuccess.set(true);
            Logger.logSelect(hostActivity.getContext(), SHARE_TO_OTHER);
          }
        }).launch(sharingIntent);
    return shareSuccess;
  }

  public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    if (callbackManager != null) {
      callbackManager.onActivityResult(requestCode, resultCode, data);
    }
  }
}
