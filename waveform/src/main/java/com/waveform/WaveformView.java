package com.waveform;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class WaveformView extends View implements Animatable {

    private final static int VERTICES_COUNT = 7;
    private final static int CURVE_POINTS_NUMBER = 100;
    private final static int VARIANCE_THRESHOLD = 20;

    private boolean isRunning = false;

    private Spline spline;

    private double[] fromVerticesX;
    private double[] fromVerticesY;

    private double[] toVerticesX;
    private double[] toVerticesY;

    private Path wavePath;
    private Paint wavePaint;

    private boolean isSplinesInited;

    private Random rand;

    public WaveformView(Context context) {
        super(context);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isRunning) {
            return;
        }
        if (!isSplinesInited) {
            initFromSpline();
            initToVertices();
            drawWave(canvas);
            isSplinesInited = true;
            postInvalidate();
            return;
        }
        calculateFromSpline();
        drawWave(canvas);
        postInvalidate();
    }

    @Override
    public void start() {
        isRunning = true;
        invalidate();
    }

    @Override
    public void stop() {
        isRunning = false;
        invalidate();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    public void setWaveColor(int color) {
        wavePaint.setColor(color);
    }

    private void init() {
        rand = new Random();

        fromVerticesX = new double[VERTICES_COUNT];
        fromVerticesY = new double[VERTICES_COUNT];
        toVerticesX = new double[VERTICES_COUNT];
        toVerticesY = new double[VERTICES_COUNT];

        wavePath = new Path();
        wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setAntiAlias(true);

        spline = new Spline(VERTICES_COUNT);
    }

    private void initFromSpline() {
        int width = getWidth();
        int height = getHeight();
        int stepX = width / VERTICES_COUNT;
        for (int i = 0; i < VERTICES_COUNT; i++) {
            fromVerticesX[i] = i != VERTICES_COUNT - 1 ? i * stepX : width;
            fromVerticesY[i] = height;
        }
        spline.build(fromVerticesX, fromVerticesY);
    }

    private void initToVertices() {
        int width = getWidth();
        int height = getHeight();
        int stepX = width / VERTICES_COUNT;
        for (int i = 0; i < VERTICES_COUNT; i++) {
            toVerticesX[i] = i != VERTICES_COUNT - 1 ? i * stepX : width;
            toVerticesY[i] = rand.nextInt((int) (0.7 * height + 1)) + 0.3 * height;
        }
    }

    private void calculateFromSpline() {
        int height = getHeight();
        for (int i = 0; i < VERTICES_COUNT; i++) {
            double fromVertexY = fromVerticesY[i];
            double toVertexY = toVerticesY[i];
            double dy = fromVertexY - toVertexY;
            if (Math.abs(dy) <= 2 * VARIANCE_THRESHOLD) {
                toVerticesY[i] = rand.nextInt((int) (0.7 * height + 1)) + 0.3 * height;
                continue;
            }
            double newFromVertexY = fromVertexY + (dy > 0 ? -1 : 1) * VARIANCE_THRESHOLD;
            fromVerticesY[i] = newFromVertexY;
            fromVerticesY[i] = newFromVertexY;
        }
        spline.build(fromVerticesX, fromVerticesY);
    }

    private void drawWave(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        wavePath.reset();
        double stepX = width / CURVE_POINTS_NUMBER;
        int x, y;
        wavePath.moveTo(width, (int) spline.interpolate(width));
        wavePath.lineTo(width, height);
        wavePath.lineTo(0, height);
        wavePath.lineTo(0, (int) spline.interpolate(0));
        for (int i = 0; i < CURVE_POINTS_NUMBER; i++) {
            x = i * (int) stepX;
            y = (int) spline.interpolate(x);
            wavePath.lineTo(x, y);
        }
        wavePath.lineTo(width, (int) spline.interpolate(width));
        canvas.drawPath(wavePath, wavePaint);
    }
}
