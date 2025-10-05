package com.yourname.flappybird;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create main layout
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setBackgroundColor(Color.rgb(135, 206, 235)); // Sky blue background
        mainLayout.setPadding(60, 60, 60, 60);

        // Create Play button
        Button playButton = createStyledButton("PLAY");
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        // Create High Scores button
        Button highScoresButton = createStyledButton("HIGH SCORES");
        highScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HighScoresActivity.class);
                startActivity(intent);
            }
        });

        // Create Settings button
        Button settingsButton = createStyledButton("SETTINGS");
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Add buttons to layout with spacing
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                600, 150);
        buttonParams.setMargins(0, 30, 0, 30);

        mainLayout.addView(playButton, buttonParams);
        mainLayout.addView(highScoresButton, buttonParams);
        mainLayout.addView(settingsButton, buttonParams);

        setContentView(mainLayout);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(24);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.rgb(0, 180, 0)); // Green like pipes
        button.setAllCaps(true);
        return button;
    }
}
