package com.yourname.flappybird;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Bird {
    private float x, y;
    private float velocity;
    private static final float GRAVITY = 0.8f;
    private static final float JUMP_VELOCITY = -15f;
    private static final int RADIUS = 45; // Increased by 50% from 30

    // Wing animation variables
    private int wingFrame = 0;
    private static final int WING_ANIMATION_SPEED = 5; // Frames per wing position
    private float wingAngle = 0;

    public Bird(float x, float y) {
        this.x = x;
        this.y = y;
        this.velocity = 0;
    }

    public void update() {
        // Apply gravity
        velocity += GRAVITY;
        y += velocity;

        // Update wing animation
        wingFrame++;
        if (wingFrame >= WING_ANIMATION_SPEED * 2) {
            wingFrame = 0;
        }

        // Calculate wing angle (flap up and down)
        if (wingFrame < WING_ANIMATION_SPEED) {
            wingAngle = -30; // Wings up
        } else {
            wingAngle = 30; // Wings down
        }
    }

    public void jump() {
        velocity = JUMP_VELOCITY;
    }

    public void draw(Canvas canvas, Paint paint) {
        // Save canvas state for rotation
        canvas.save();

        // Wing size (about quarter of body)
        int wingWidth = RADIUS / 2;
        int wingHeight = RADIUS / 4;

        // Draw left wing
        canvas.save();
        canvas.translate(x - RADIUS / 3, y);
        canvas.rotate(wingAngle);
        paint.setColor(Color.rgb(200, 160, 0)); // Darker yellow/orange for wings
        RectF leftWingRect = new RectF(-wingWidth, -wingHeight, 0, wingHeight);
        canvas.drawOval(leftWingRect, paint);
        canvas.restore();

        // Draw right wing
        canvas.save();
        canvas.translate(x + RADIUS / 3, y);
        canvas.rotate(-wingAngle);
        paint.setColor(Color.rgb(200, 160, 0)); // Darker yellow/orange for wings
        RectF rightWingRect = new RectF(0, -wingHeight, wingWidth, wingHeight);
        canvas.drawOval(rightWingRect, paint);
        canvas.restore();

        // Draw bird as a yellow circle
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(x, y, RADIUS, paint);

        // Draw bird's eye
        paint.setColor(Color.BLACK);
        canvas.drawCircle(x + 12, y - 12, 9, paint);

        // Draw bird's beak
        paint.setColor(Color.rgb(255, 140, 0)); // Orange beak
        canvas.drawCircle(x + 30, y, 12, paint);

        canvas.restore();
    }

    public boolean collidesWith(Pipe pipe) {
        // Use the Pipe's own collision detection method
        // This delegates to the pipe which knows its own structure
        return pipe.collidesWith(this);
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public int getRadius() { return RADIUS; }
}
