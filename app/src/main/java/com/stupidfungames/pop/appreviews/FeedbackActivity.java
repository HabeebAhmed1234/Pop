package com.stupidfungames.pop.appreviews;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.stupidfungames.pop.R;

public class FeedbackActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main_menu_layout);

    findViewById(R.id.send_btn).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //sendFeedback();
      }
    });
  }
}
