package com.yourname.flappybird;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
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
    private int highScore;
    private boolean newHighScore;
    private int screenWidth, screenHeight;
    private long lastPipeTime;
    private static final int PIPE_SPACING = 600;
    private static final int PIPE_GAP = 350;
    private float currentGapCenter;
    
    // SharedPreferences for high score persistence
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "FlappyBirdPrefs";
    private static final String HIGH_SCORE_KEY = "high_score";

    public GameView(Context context) {
        super(context);
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
        
        // Initialize SharedPreferences and load high score
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt(HIGH_SCORE_KEY, 0);
        
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();
        
        // Initialize bird
        bird = new Bird(screenWidth / 4, screenHeight / 2);
        
        // Initialize gap center to screen center
        currentGapCenter = screenHeight / 2;
        
        gameThread = new GameThread(getHolder());
        gameThread.setRunning(true);
        gameThread.start();
        
        gameRunning = true;
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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (gameOver) {
                // Restart game
                restartGame();
            } else if (gameRunning) {
                // Make bird jump
                bird.jump();
            }
        }
        return true;
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
        
        // Add new pipes
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPipeTime > PIPE_SPACING) {
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
                
                // Check for new high score
                if (score > highScore) {
                    highScore = score;
                    newHighScore = true;
                    saveHighScore();
                }
            }
        }
    }

    private void addPipe() {
        // Create smoother gap transitions
        float maxChange = 80;
        float minGapCenter = PIPE_GAP / 2 + 100;
        float maxGapCenter = screenHeight - PIPE_GAP / 2 - 100;
        
        float targetChange = (random.nextFloat() - 0.5f) * 2 * maxChange;
        currentGapCenter += targetChange;
        
        if (currentGapCenter < minGapCenter) {
            currentGapCenter = minGapCenter;
        } else if (currentGapCenter > maxGapCenter) {
            currentGapCenter = maxGapCenter;
        }
        
        int topHeight = (int)(currentGapCenter - PIPE_GAP / 2);
        pipes.add(new Pipe(screenWidth, topHeight, PIPE_GAP));
    }

    private void gameOver() {
        gameOver = true;
    }

    private void restartGame() {
        bird = new Bird(screenWidth / 4, screenHeight / 2);
        pipes.clear();
        score = 0;
        newHighScore = false;
        gameOver = false;
        gameRunning = true;
        lastPipeTime = 0;
        currentGapCenter = screenHeight / 2;
    }

    private void saveHighScore() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(HIGH_SCORE_KEY, highScore);
        editor.apply(); // Use apply() for asynchronous saving
    }

    private void drawGame(Canvas canvas) {
        if (canvas == null) return;
        
        // Clear screen with sky blue background
        canvas.drawColor(Color.rgb(135, 206, 235));
        
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
        canvas.drawText("Best: " + highScore, 50, 150, paint);
        
        // Draw game over screen
        if (gameOver) {
            // Semi-transparent overlay
            paint.setColor(Color.argb(150, 0, 0, 0));
            canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
            
            // Game over text
            paint.setColor(Color.WHITE);
            paint.setTextSize(80);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("GAME OVER", screenWidth / 2, screenHeight / 2 - 100, paint);
            
            // Final score
            paint.setTextSize(50);
            canvas.drawText("Score: " + score, screenWidth / 2, screenHeight / 2 - 40, paint);
            
            // High score display
            if (newHighScore) {
                paint.setColor(Color.YELLOW);
                paint.setTextSize(45);
                canvas.drawText("🎉 NEW HIGH SCORE! 🎉", screenWidth / 2, screenHeight / 2 + 20, paint);
            } else {
                paint.setColor(Color.LTGRAY);
                paint.setTextSize(40);
                canvas.drawText("Best: " + highScore, screenWidth / 2, screenHeight / 2 + 20, paint);
            }
            
            // Restart instruction
            paint.setColor(Color.WHITE);
            paint.setTextSize(35);
            canvas.drawText("Tap to restart", screenWidth / 2, screenHeight / 2 + 80, paint);
        }
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
