package com.siddhpuria.shaishav.tilt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * ExperimentView.java
 * This is the actual canvas on which the interaction is drawn.
 */

public class ExperimentView extends View {

    Paint paint = null;

    public ExperimentView(Context context) {
        super(context);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();
        int radius = 100;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawPaint(paint);
        paint.setColor(Color.RED);
        canvas.drawCircle(x/2, y/2, radius, paint);
        System.out.println("DRAWING");

    }
}
