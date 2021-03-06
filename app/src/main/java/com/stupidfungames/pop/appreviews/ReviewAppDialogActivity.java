package com.stupidfungames.pop.appreviews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.annotation.Nullable;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.dialog.GameNeonDialogActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Asks the user if they like using the app. If so then redirects them to the play store to review.
 * else allows them to send feedback.
 */
public class ReviewAppDialogActivity extends GameNeonDialogActivity {

  public static Intent newIntent(Context context) {
    return new Intent(context, ReviewAppDialogActivity.class);
  }


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected int getTitleResId() {
    return R.string.review_app_dialog_title;
  }

  @Override
  protected List<ButtonModel> getButtonModels() {
    return new ArrayList<>(
        Arrays.asList(
            new ButtonModel(R.string.review_app_btn_text, new OnClickListener() {
              @Override
              public void onClick(View v) {
                AppReviewUtil.userLeftReviewOrFeedback(ReviewAppDialogActivity.this);
                AppReviewUtil.sendUserToPlayStore(ReviewAppDialogActivity.this);
              }
            }), new ButtonModel(R.string.send_feedback_btn_text, new OnClickListener() {
              @Override
              public void onClick(View v) {
                AppReviewUtil.userLeftReviewOrFeedback(ReviewAppDialogActivity.this);
                sendFeedback();
              }
            }), new ButtonModel(R.string.dismiss, new OnClickListener() {
              @Override
              public void onClick(View v) {
                finish();
              }
            }), new ButtonModel(R.string.dont_show_again, new OnClickListener() {
              @Override
              public void onClick(View v) {
                AppReviewUtil.dontShowAgain(ReviewAppDialogActivity.this);
                finish();
              }
            })));
  }

  private void sendFeedback() {
    Intent Email = new Intent(Intent.ACTION_SEND);
    Email.setType("text/email");
    Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"stupidfungameszz@gmail.com"});
    Email.putExtra(Intent.EXTRA_SUBJECT, "[Cyberpop] Feedback");
    Email.putExtra(Intent.EXTRA_TEXT, "The following is my feedback for Cyberpop:");
    startActivity(Intent.createChooser(Email, "Send Feedback:"));
  }
}
