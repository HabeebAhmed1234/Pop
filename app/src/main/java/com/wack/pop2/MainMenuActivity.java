package com.wack.pop2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

public class MainMenuActivity extends Activity {

    private ValueAnimator logoAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        findViewById(R.id.new_game_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });

        findViewById(R.id.options_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOptionsMenu();
            }
        });

        findViewById(R.id.quit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitGame();
            }
        });

        animateLogo();
    }

    private void startNewGame() {
        Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
        Bundle b = new Bundle();
        b.putInt("Difficulty", ((GlobalVariables)getApplicationContext()).getDifficulty());
        intent.putExtras(b);
        startActivity(intent);
        this.finish();
    }

    private void startOptionsMenu() {
	    MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, OptionsMenu.class));
    }

    private void  quitGame() {
        this.finish();
    }

    private void animateLogo() {
        final View logo = findViewById(R.id.logo);
        logoAnimator = ObjectAnimator.ofFloat(1,0.95f, 1);
        logoAnimator.setDuration(1000);
        logoAnimator.start();
        logoAnimator.setRepeatMode(ValueAnimator.RESTART);

        logoAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                logo.setScaleX((float) animation.getAnimatedValue());
                logo.setScaleY((float) animation.getAnimatedValue());
            }
        });
        logoAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animation.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logoAnimator.end();
    }
}