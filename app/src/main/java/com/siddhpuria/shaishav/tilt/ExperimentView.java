package com.siddhpuria.shaishav.tilt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * ExperimentView.java
 * This is the actual canvas on which the interaction is drawn.
 */

public class ExperimentView extends View {

    Pointer pointer;

    public ExperimentView(Context context) {
        super(context);
        pointer = new Pointer();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        pointer.draw(canvas);

    }

    public void updatePointerPosition(float xOffset, float yOffset) {
        pointer.drawAtOffset(xOffset, yOffset);
        this.invalidate();
    }

    /**
     * Pointer object to navigate for menu selection.
     * This will be drawn at relative x,y offsets to center of the interaction being (0,0)
     */
    public class Pointer extends Drawable {

        float x, y;
        Canvas canvas = null;

        @Override
        public void draw(@NonNull Canvas canvas) {
            if (this.canvas == null) {
                this.canvas = canvas;
                x = canvas.getWidth()/2;
                y = canvas.getHeight()/2;
            }

            int radius = 50;
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            canvas.drawPaint(paint);
            paint.setColor(Color.RED);
            canvas.drawCircle(x, y, radius, paint);
        }

        public void drawAtOffset(float xOffset, float yOffset) {
            if (canvas == null) return;

            x = Math.min(canvas.getWidth(), Math.max(0, canvas.getWidth()/2 + xOffset));
            y = Math.min(canvas.getHeight(), Math.max(0, canvas.getHeight()/2 + yOffset));
//            System.out.println("x "+x+" y "+y);
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getOpacity() {
            throw new UnsupportedOperationException();
        }
    }
}
