package com.yourname.flappybird;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox musicCheckBox;
    private CheckBox sfxCheckBox;
    private SharedPreferences sharedPreferences;

    public static final String PREFS_NAME = "FlappyBirdSettings";
    public static final String MUSIC_ENABLED = "music_enabled";
    public static final String SFX_ENABLED = "sfx_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Create main layout
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.setBackgroundColor(Color.rgb(135, 206, 235)); // Sky blue background
        mainLayout.setPadding(60, 80, 60, 80);

        // Title
        TextView titleText = new TextView(this);
        titleText.setText("SETTINGS");
        titleText.setTextColor(Color.WHITE);
        titleText.setTextSize(36);
        titleText.setGravity(Gravity.CENTER);
        titleText.setPadding(0, 0, 0, 60);
        mainLayout.addView(titleText);

        // Music checkbox
        musicCheckBox = new CheckBox(this);
        musicCheckBox.setText("Music");
        musicCheckBox.setTextSize(24);
        musicCheckBox.setTextColor(Color.WHITE);
        musicCheckBox.setChecked(sharedPreferences.getBoolean(MUSIC_ENABLED, true));
        musicCheckBox.setPadding(0, 30, 0, 30);
        musicCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(MUSIC_ENABLED, isChecked).apply();
        });
        mainLayout.addView(musicCheckBox);

        // SFX checkbox
        sfxCheckBox = new CheckBox(this);
        sfxCheckBox.setText("Sound Effects");
        sfxCheckBox.setTextSize(24);
        sfxCheckBox.setTextColor(Color.WHITE);
        sfxCheckBox.setChecked(sharedPreferences.getBoolean(SFX_ENABLED, true));
        sfxCheckBox.setPadding(0, 30, 0, 30);
        sfxCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(SFX_ENABLED, isChecked).apply();
        });
        mainLayout.addView(sfxCheckBox);

        // Back button
        Button backButton = new Button(this);
        backButton.setText("BACK");
        backButton.setTextSize(24);
        backButton.setTextColor(Color.WHITE);
        backButton.setBackgroundColor(Color.rgb(0, 180, 0)); // Green
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                400, 120);
        buttonParams.setMargins(0, 60, 0, 0);
        mainLayout.addView(backButton, buttonParams);

        setContentView(mainLayout);
    }
}
