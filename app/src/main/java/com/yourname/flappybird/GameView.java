package com.yourname.flappybird;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.InputFilter;
import android.view.ViewGroup;
import android.view.Gravity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private Paint paint;
    private Bird bird;
    private ArrayList<Pipe> pipes;
    private Random random;
    private boolean gameRunning;
    private boolean gameOver;
    private int score;
    private boolean newHighScore;
    private int screenWidth, screenHeight;
    private long lastPipeTime;
    private int pipeSpacing; // Will be set to screenWidth / 2
    private int fixedGapSize; // Will be set to screenHeight / 4
    private float currentGapCenter;
    
    // High score management
    private HighScoreManager highScoreManager;
    private boolean showHighScoreTable = false;
    private boolean waitingForInitials = false;
    private boolean initialsEntered = false;
    private int newHighScoreRank = -1;
    private String playerInitials = "";
    
    // UI Elements for initial entry
    private Activity parentActivity;
    private LinearLayout initialEntryLayout;
    private EditText initialEditText;
    private boolean uiElementsCreated = false;

    // Music player
    private MediaPlayer mediaPlayer;
    private Context context;

    // Sound effects
    private SoundPool soundPool;
    private int coinPickupSoundId;

    // Settings
    private SharedPreferences sharedPreferences;

    public GameView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);

        paint = new Paint();
        paint.setAntiAlias(true);

        random = new Random();
        pipes = new ArrayList<>();
        gameRunning = false;
        gameOver = false;
        score = 0;
        newHighScore = false;
        lastPipeTime = 0;
        currentGapCenter = 0;

        // Initialize settings
        sharedPreferences = context.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);

        // Initialize high score manager
        highScoreManager = new HighScoreManager(context);

        // Get parent activity for UI manipulation
        if (context instanceof Activity) {
            parentActivity = (Activity) context;
        }

        // Initialize sound effects
        initializeSoundEffects();

        setFocusable(true);
    }

    private void initializeSoundEffects() {
        try {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();

            AssetFileDescriptor afd = context.getAssets().openFd("sounds/sfx/player/coin_pickup.wav");
            coinPickupSoundId = soundPool.load(afd, 1);
            afd.close();

            android.util.Log.d("FlappyBird", "Sound effects initialized successfully");
        } catch (IOException e) {
            android.util.Log.e("FlappyBird", "Failed to initialize sound effects", e);
            e.printStackTrace();
        }
    }

    private void initializeMusic() {
        if (mediaPlayer != null) {
            return; // Already initialized
        }

        try {
            mediaPlayer = new MediaPlayer();
            AssetFileDescriptor afd = context.getAssets().openFd("sounds/music/main_theme.mp3");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true); // Loop continuously
            mediaPlayer.setVolume(0.7f, 0.7f); // Set volume to 70%
            android.util.Log.d("FlappyBird", "Music initialized successfully");
        } catch (IOException e) {
            android.util.Log.e("FlappyBird", "Failed to initialize music", e);
            e.printStackTrace();
        }
    }

    private void startMusic() {
        // Check if music is enabled in settings
        boolean musicEnabled = sharedPreferences.getBoolean(SettingsActivity.MUSIC_ENABLED, true);
        if (!musicEnabled) {
            return;
        }

        initializeMusic(); // Lazy initialization

        if (mediaPlayer != null) {
            try {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    android.util.Log.d("FlappyBird", "Music started");
                }
            } catch (Exception e) {
                android.util.Log.e("FlappyBird", "Failed to start music", e);
                e.printStackTrace();
            }
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0); // Reset to beginning
                    android.util.Log.d("FlappyBird", "Music stopped");
                }
            } catch (Exception e) {
                android.util.Log.e("FlappyBird", "Failed to stop music", e);
                e.printStackTrace();
            }
        }
    }

    private void playScoreSound() {
        // Check if SFX is enabled in settings
        boolean sfxEnabled = sharedPreferences.getBoolean(SettingsActivity.SFX_ENABLED, true);
        if (!sfxEnabled) {
            return;
        }

        if (soundPool != null && coinPickupSoundId != 0) {
            try {
                soundPool.play(coinPickupSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
                android.util.Log.d("FlappyBird", "Score sound played");
            } catch (Exception e) {
                android.util.Log.e("FlappyBird", "Failed to play score sound", e);
                e.printStackTrace();
            }
        }
    }

    private void createInitialEntryUI() {
        if (uiElementsCreated || parentActivity == null) return;
        
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Create overlay layout
                initialEntryLayout = new LinearLayout(parentActivity);
                initialEntryLayout.setOrientation(LinearLayout.VERTICAL);
                initialEntryLayout.setGravity(Gravity.CENTER);
                initialEntryLayout.setBackgroundColor(Color.argb(200, 0, 0, 0));
                
                // Title
                TextView titleText = new TextView(parentActivity);
                titleText.setText("🎉 HIGH SCORE #" + newHighScoreRank + "! 🎉");
                titleText.setTextColor(Color.YELLOW);
                titleText.setTextSize(24);
                titleText.setGravity(Gravity.CENTER);
                titleText.setPadding(20, 40, 20, 20);
                
                // Label
                TextView labelText = new TextView(parentActivity);
                labelText.setText("Enter your name:");
                labelText.setTextColor(Color.WHITE);
                labelText.setTextSize(18);
                labelText.setGravity(Gravity.CENTER);
                labelText.setPadding(20, 0, 20, 10);
                
                // EditText for initials
                initialEditText = new EditText(parentActivity);
                initialEditText.setHint("ABC");
                initialEditText.setTextColor(Color.WHITE);
                initialEditText.setHintTextColor(Color.LTGRAY);
                initialEditText.setTextSize(24);
                initialEditText.setGravity(Gravity.CENTER);
                initialEditText.setBackgroundColor(Color.argb(150, 255, 255, 255));
                initialEditText.setPadding(20, 15, 20, 15);
                
                // Limit to 3 characters and uppercase
                initialEditText.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(3),
                    new InputFilter.AllCaps()
                });
                
                LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                    300, LinearLayout.LayoutParams.WRAP_CONTENT);
                editParams.setMargins(40, 0, 40, 20);
                
                // Instructions
                TextView instructText = new TextView(parentActivity);
                instructText.setText("Tap screen when done");
                instructText.setTextColor(Color.LTGRAY);
                instructText.setTextSize(14);
                instructText.setGravity(Gravity.CENTER);
                instructText.setPadding(20, 10, 20, 40);
                
                // Add views to layout
                initialEntryLayout.addView(titleText);
                initialEntryLayout.addView(labelText);
                initialEntryLayout.addView(initialEditText, editParams);
                initialEntryLayout.addView(instructText);
                
                // Add to parent layout
                ViewGroup parentLayout = (ViewGroup) parentActivity.findViewById(android.R.id.content);
                parentLayout.addView(initialEntryLayout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
                
                // Show keyboard
                initialEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(initialEditText, InputMethodManager.SHOW_IMPLICIT);
                }
                
                uiElementsCreated = true;
            }
        });
    }

    private void hideInitialEntryUI() {
        if (!uiElementsCreated || parentActivity == null) return;
        
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Hide keyboard
                if (initialEditText != null) {
                    InputMethodManager imm = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(initialEditText.getWindowToken(), 0);
                    }
                }
                
                // Remove UI elements
                if (initialEntryLayout != null) {
                    ViewGroup parentLayout = (ViewGroup) parentActivity.findViewById(android.R.id.content);
                    parentLayout.removeView(initialEntryLayout);
                }
                
                uiElementsCreated = false;
                initialEntryLayout = null;
                initialEditText = null;
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();

        // Initialize pipe spacing and gap size based on screen dimensions
        pipeSpacing = (screenWidth * 3) / 2; // Decreased by 20% from previous value
        fixedGapSize = screenHeight / 4;

        // Initialize bird
        bird = new Bird(screenWidth / 4, screenHeight / 2);

        // Initialize gap center to screen center
        currentGapCenter = screenHeight / 2;

        gameThread = new GameThread(getHolder());
        gameThread.setRunning(true);
        gameThread.start();

        gameRunning = true;

        // Start music when game starts
        startMusic();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes if needed
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        if (gameThread != null) {
            gameThread.setRunning(false);
            while (retry) {
                try {
                    gameThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    // Will try again
                }
            }
        }

        // Stop and release music player
        stopMusic();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Release sound pool
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        // Clean up UI
        hideInitialEntryUI();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (waitingForInitials && !initialsEntered) {
                // Handle initial entry completion
                handleInitialEntry();
            } else if (showHighScoreTable) {
                // Restart game from high score table
                restartGame();
            } else if (gameOver && !newHighScore) {
                // Check if tap is in bottom third of screen (menu button area)
                if (event.getY() > screenHeight * 2 / 3) {
                    // Return to main menu
                    if (parentActivity != null) {
                        parentActivity.finish();
                    }
                } else {
                    // Restart game
                    restartGame();
                }
            } else if (gameRunning) {
                // Make bird jump
                bird.jump();
            }
        }
        return true;
    }

    private void handleInitialEntry() {
        if (initialEditText != null) {
            playerInitials = initialEditText.getText().toString().trim();
            if (playerInitials.isEmpty()) {
                playerInitials = "AAA"; // Default if empty
            }
            // Pad with A's if less than 3 characters
            while (playerInitials.length() < 3) {
                playerInitials += "A";
            }
        } else {
            playerInitials = "AAA";
        }
        
        // Add to high score table
        highScoreManager.addHighScore(playerInitials, score);
        
        // Update state
        waitingForInitials = false;
        initialsEntered = true;
        showHighScoreTable = true;
        
        // Hide the input UI
        hideInitialEntryUI();
    }

    private void update() {
        if (!gameRunning || gameOver) return;
        
        // Update bird
        bird.update();
        
        // Check if bird hit ground or ceiling
        if (bird.getY() > screenHeight - bird.getRadius() || bird.getY() < bird.getRadius()) {
            gameOver();
            return;
        }
        
        // Add new pipes at fixed spacing
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPipeTime > pipeSpacing) {
            addPipe();
            lastPipeTime = currentTime;
        }
        
        // Update pipes
        for (int i = pipes.size() - 1; i >= 0; i--) {
            Pipe pipe = pipes.get(i);
            pipe.update();
            
            // Remove pipes that are off screen
            if (pipe.getX() + pipe.getWidth() < 0) {
                pipes.remove(i);
                continue;
            }
            
            // Check collision
            if (bird.collidesWith(pipe)) {
                gameOver();
                return;
            }
            
            // Check if bird passed pipe (score)
            if (!pipe.isScored() && pipe.getX() + pipe.getWidth() < bird.getX()) {
                pipe.setScored(true);
                score++;
                playScoreSound();
            }
        }
    }

    private void addPipe() {
        // Always use standard pipes (top and bottom) with fixed gap size
        // Check if there's enough space from the last pipe to prevent overlap
        if (!pipes.isEmpty()) {
            Pipe lastPipe = pipes.get(pipes.size() - 1);
            float minSafeDistance = lastPipe.getWidth() + 100; // Pipe width + safety margin
            if (screenWidth - lastPipe.getX() < minSafeDistance) {
                return; // Don't add pipe yet, too close to last one
            }
        }

        // Always create standard pipes with both top and bottom
        addStandardPipe(fixedGapSize);
    }

    private void addStandardPipe(int gap) {
        // Create varied gap positions with fixed gap size
        float minGapCenter = gap / 2 + 150;
        float maxGapCenter = screenHeight - gap / 2 - 150;

        // 20% chance to place gap near top or bottom
        if (random.nextFloat() < 0.2f) {
            // Place gap near extreme positions
            if (random.nextBoolean()) {
                // Near top
                currentGapCenter = gap / 2 + 100;
            } else {
                // Near bottom
                currentGapCenter = screenHeight - gap / 2 - 100;
            }
        } else {
            // Normal variation in vertical positioning
            float maxChange = 200;
            float targetChange = (random.nextFloat() - 0.5f) * 2 * maxChange;
            currentGapCenter += targetChange;

            if (currentGapCenter < minGapCenter) {
                currentGapCenter = minGapCenter;
            } else if (currentGapCenter > maxGapCenter) {
                currentGapCenter = maxGapCenter;
            }
        }

        int topHeight = (int)(currentGapCenter - gap / 2);

        // Validate that we have a proper gap
        if (topHeight < 0) {
            topHeight = 0;
        }
        if (topHeight + gap > screenHeight) {
            topHeight = screenHeight - gap;
        }

        pipes.add(new Pipe(screenWidth, topHeight, gap));
    }

    private void addBottomOnlyPipe(int gap) {
        // Only bottom pipe with larger gap above
        int bottomPipeHeight = random.nextInt(screenHeight / 3) + 200;
        pipes.add(new Pipe(screenWidth, 0, bottomPipeHeight, true)); // Bottom-only constructor
    }

    private void addTopOnlyPipe(int gap) {
        // Only top pipe with larger gap below
        int topPipeHeight = random.nextInt(screenHeight / 3) + 200;
        pipes.add(new Pipe(screenWidth, topPipeHeight, gap, true, false)); // hasTop=true, hasBottom=false
    }

    private void addStaggeredPipes(int gap) {
        // For staggered pipes, just add a standard pipe and let the normal timing create the stagger
        // This prevents overlap issues from trying to add multiple pipes at once
        addStandardPipe(gap);
    }

    private void gameOver() {
        gameOver = true;

        // Stop music when game ends
        stopMusic();

        // Check if this is a high score
        if (highScoreManager.isHighScore(score)) {
            newHighScore = true;
            newHighScoreRank = highScoreManager.getHighScoreRank(score);
            waitingForInitials = true;
            initialsEntered = false;

            // Create initial entry UI
            createInitialEntryUI();
        }
    }

    private void restartGame() {
        // Clean up any UI elements
        hideInitialEntryUI();

        bird = new Bird(screenWidth / 4, screenHeight / 2);
        pipes.clear();
        score = 0;
        newHighScore = false;
        waitingForInitials = false;
        initialsEntered = false;
        showHighScoreTable = false;
        gameOver = false;
        gameRunning = true;
        lastPipeTime = 0;
        currentGapCenter = screenHeight / 2;
        playerInitials = "";
        uiElementsCreated = false;

        // Restart music when game restarts
        startMusic();
    }

    private void drawGame(Canvas canvas) {
        if (canvas == null) return;
        
        // Clear screen with sky blue background
        canvas.drawColor(Color.rgb(135, 206, 235));
        
        if (showHighScoreTable) {
            drawHighScoreTable(canvas);
            return;
        }
        
        // Draw pipes
        for (Pipe pipe : pipes) {
            pipe.draw(canvas, paint);
        }
        
        // Draw bird
        bird.draw(canvas, paint);
        
        // Draw current score
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Score: " + score, 50, 100, paint);
        
        // Draw high score
        paint.setTextSize(40);
        canvas.drawText("Best: " + highScoreManager.getTopScore(), 50, 150, paint);
        
        // Draw game over screen (but not when waiting for initials)
        if (gameOver && !waitingForInitials) {
            drawGameOverScreen(canvas);
        }
    }

    private void drawGameOverScreen(Canvas canvas) {
        // Semi-transparent overlay
        paint.setColor(Color.argb(150, 0, 0, 0));
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

        // Game over text
        paint.setColor(Color.WHITE);
        paint.setTextSize(80);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("GAME OVER", screenWidth / 2, screenHeight / 2 - 150, paint);

        // Final score
        paint.setTextSize(50);
        canvas.drawText("Score: " + score, screenWidth / 2, screenHeight / 2 - 80, paint);

        // Instructions
        paint.setColor(Color.YELLOW);
        paint.setTextSize(32);
        canvas.drawText("Tap to Restart", screenWidth / 2, screenHeight / 2 + 20, paint);

        paint.setColor(Color.LTGRAY);
        paint.setTextSize(28);
        canvas.drawText("or tap here for Menu", screenWidth / 2, screenHeight - 150, paint);

        // Draw arrow pointing down
        paint.setTextSize(40);
        canvas.drawText("↓", screenWidth / 2, screenHeight - 100, paint);
    }

    private void drawHighScoreTable(Canvas canvas) {
        // Dark background
        canvas.drawColor(Color.rgb(20, 20, 40));
        
        // Title
        paint.setColor(Color.YELLOW);
        paint.setTextSize(70);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("HIGH SCORES", screenWidth / 2, 120, paint);
        
        // Draw high score list
        List<HighScore> scores = highScoreManager.getHighScores();
        paint.setTextSize(50);
        int startY = 220;
        int lineHeight = 80;
        
        for (int i = 0; i < scores.size(); i++) {
            HighScore highScore = scores.get(i);
            int y = startY + (i * lineHeight);
            
            // Highlight new entry
            if (initialsEntered && playerInitials.equals(highScore.getInitials()) && 
                score == highScore.getScore()) {
                paint.setColor(Color.YELLOW);
            } else {
                paint.setColor(Color.WHITE);
            }
            
            // Rank
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText((i + 1) + ".", 100, y, paint);
            
            // Initials
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(highScore.getInitials(), 180, y, paint);
            
            // Score
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.valueOf(highScore.getScore()), screenWidth - 100, y, paint);
        }
        
        // Instructions
        paint.setColor(Color.LTGRAY);
        paint.setTextSize(35);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Tap to restart game", screenWidth / 2, screenHeight - 100, paint);
    }

    public void pause() {
        gameRunning = false;
    }

    public void resume() {
        if (!gameOver) {
            gameRunning = true;
        }
    }

    private class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private boolean running;
        private static final int FPS = 60;
        private static final long frameTime = 1000 / FPS;

        public GameThread(SurfaceHolder holder) {
            surfaceHolder = holder;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            long startTime;
            long timeMillis;
            long waitTime;
            
            while (running) {
                startTime = System.currentTimeMillis();
                Canvas canvas = null;
                
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        update();
                        drawGame(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                
                timeMillis = System.currentTimeMillis() - startTime;
                waitTime = frameTime - timeMillis;
                
                try {
                    if (waitTime > 0) {
                        sleep(waitTime);
                    }
                } catch (InterruptedException e) {
                    // Handle interruption
                }
            }
        }
    }
}