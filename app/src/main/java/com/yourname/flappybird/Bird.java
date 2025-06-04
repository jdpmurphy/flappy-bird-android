package com.yourname.flappybird;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Bird {
    private float x, y;
    private float velocity;
    private static final float GRAVITY = 0.8f;
    private static final float JUMP_VELOCITY = -15f;
    private static final int RADIUS = 30;

    public Bird(float x, float y) {
        this.x = x;
        this.y = y;
        this.velocity = 0;
    }

    public void update() {
        // Apply gravity
        velocity += GRAVITY;
        y += velocity;
    }

    public void jump() {
        velocity = JUMP_VELOCITY;
    }

    public void draw(Canvas canvas, Paint paint) {
        // Draw bird as a yellow circle
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(x, y, RADIUS, paint);
        
        // Draw bird's eye
        paint.setColor(Color.BLACK);
        canvas.drawCircle(x + 8, y - 8, 6, paint);
        
        // Draw bird's beak
        paint.setColor(Color.DKGRAY);
        canvas.drawCircle(x + 20, y, 8, paint);
    }

    public boolean collidesWith(Pipe pipe) {
        // Check collision with top pipe
        if (x + RADIUS > pipe.getX() && x - RADIUS < pipe.getX() + pipe.getWidth()) {
            if (y - RADIUS < pipe.getTopHeight() || y + RADIUS > pipe.getBottomY()) {
                return true;
            }
        }
        return false;
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public int getRadius() { return RADIUS; }
}
