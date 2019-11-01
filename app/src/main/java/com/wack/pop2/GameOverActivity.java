package com.wack.pop2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

	public static final String SCORE_EXTRA = "score_extra";

	public static Intent newIntent(Context context, int score) {
		Intent intent = new Intent(context, GameOverActivity.class);
		Bundle b = new Bundle();
		b.putInt(GameOverActivity.SCORE_EXTRA, score);
		intent.putExtras(b);
		return intent;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_over_layout);

		findViewById(R.id.new_game_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startNewGame();
			}
		});

		findViewById(R.id.quit_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				quitGame();
			}
		});

		((TextView) findViewById(R.id.score_text)).setText(Integer.toString(getIntent().getIntExtra(SCORE_EXTRA, 0)));
	}

	private void startNewGame() {
		startActivity(GameActivity.newIntent(this));
		finish();
	}

	private void  quitGame() {
		this.finish();
	}
}