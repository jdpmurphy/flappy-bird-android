package com.yourname.flappybird;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

public class HighScoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create main layout
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.setBackgroundColor(Color.rgb(20, 20, 40)); // Dark background
        mainLayout.setPadding(60, 80, 60, 80);

        // Title
        TextView titleText = new TextView(this);
        titleText.setText("HIGH SCORES");
        titleText.setTextColor(Color.YELLOW);
        titleText.setTextSize(36);
        titleText.setGravity(Gravity.CENTER);
        titleText.setPadding(0, 0, 0, 60);
        mainLayout.addView(titleText);

        // Get high scores
        HighScoreManager highScoreManager = new HighScoreManager(this);
        List<HighScore> scores = highScoreManager.getHighScores();

        // Display scores
        for (int i = 0; i < scores.size(); i++) {
            HighScore score = scores.get(i);

            LinearLayout scoreRow = new LinearLayout(this);
            scoreRow.setOrientation(LinearLayout.HORIZONTAL);
            scoreRow.setGravity(Gravity.CENTER);
            scoreRow.setPadding(0, 15, 0, 15);

            // Rank
            TextView rankText = new TextView(this);
            rankText.setText((i + 1) + ".");
            rankText.setTextColor(Color.WHITE);
            rankText.setTextSize(28);
            rankText.setWidth(100);
            scoreRow.addView(rankText);

            // Initials
            TextView initialsText = new TextView(this);
            initialsText.setText(score.getInitials());
            initialsText.setTextColor(Color.WHITE);
            initialsText.setTextSize(28);
            initialsText.setWidth(200);
            scoreRow.addView(initialsText);

            // Score
            TextView scoreText = new TextView(this);
            scoreText.setText(String.valueOf(score.getScore()));
            scoreText.setTextColor(Color.WHITE);
            scoreText.setTextSize(28);
            scoreText.setGravity(Gravity.END);
            scoreRow.addView(scoreText);

            mainLayout.addView(scoreRow);
        }

        // Instructions
        TextView instructText = new TextView(this);
        instructText.setText("Tap anywhere to return to menu");
        instructText.setTextColor(Color.LTGRAY);
        instructText.setTextSize(18);
        instructText.setGravity(Gravity.CENTER);
        instructText.setPadding(0, 80, 0, 0);
        mainLayout.addView(instructText);

        // Set click listener to return to menu
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    finish();
                    return true;
                }
                return false;
            }
        });

        setContentView(mainLayout);
    }
}
