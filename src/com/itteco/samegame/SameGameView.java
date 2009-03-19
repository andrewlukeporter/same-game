/**
 * Copyright (c) 2009 Itteco Software (http://itteco.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.itteco.samegame;

import android.content.Context;
import android.graphics.*;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.itteco.samegame.animation.*;
import com.itteco.samegame.engine.GameEngine;
import com.itteco.samegame.engine.TouchResult;
import com.itteco.samegame.configuration.Configuration;

/**
 * Game field view. All game drawing is occurs there
 */
public class SameGameView extends View implements AnimationCallback {

    private SelectionAnimation selectionAnimation;
    private MoveAnimation moveAnimation;
    private ExplosionAnimation explosionAnimation;
    private SameGameGraphicDataHolder dataHolder = new SameGameGraphicDataHolder();
    private Paint paintStatistics;


    private void initBallSkin(Context context) {
        Bitmap ballSkinFull = BitmapFactory.decodeResource(getResources(),
                dataHolder.getConfiguration() == null ? R.drawable.balls : dataHolder.getConfiguration().getSkin());

        dataHolder.setBallSkin(ballSkinFull);
        dataHolder.setExploseBallSkin(BitmapFactory.decodeResource(getResources(), R.drawable.explosion));
    }

    private void initBackgroundImage() {
        dataHolder.setBackgroundImage(BitmapFactory.decodeResource(getResources(), R.drawable.background));
    }

    public void init(Context context) {
        initBallSkin(context);
    }

    public SameGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);


        paintStatistics = new Paint();

        paintStatistics.setStrokeWidth(1);
        paintStatistics.setColor(Color.YELLOW);
        setFocusableInTouchMode(true);
        initBallSkin(context);
        initBackgroundImage();

        moveAnimation = new MoveAnimation(this, dataHolder);
        selectionAnimation = new SelectionAnimation(dataHolder);
        explosionAnimation = new ExplosionAnimation(this, dataHolder);
    }

    public void setGameEngine(GameEngine gameEngine) {
        dataHolder.setGameEngine(gameEngine);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (moveAnimation.isActive() || explosionAnimation.isActive()) {
            return true;
        }
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return true;//super.onTouchEvent(event);
        }
        int x = Math.round((event.getX() - dataHolder.getCellSize() / 2) / dataHolder.getCellSize());
        int y = Math.round((event.getY() - dataHolder.getCellSize() / 2) / dataHolder.getCellSize());
        if (x < 0 || x >= dataHolder.getGameEngine().getGameField().getSizeX() ||
                y < 0 || y >= dataHolder.getGameEngine().getGameField().getSizeY()) {
            return super.onTouchEvent(event);
        } else {
            TouchResult results = dataHolder.getGameEngine().touchAction(x, y);
            switch (results) {
                case explode:
                    startExplosion();
                    break;

                case selected:
                    startSelection();
                    break;

                case no_action:
                    if (selectionAnimation.isActive()) {
                        selectionAnimation.stop();
                        invalidate();
                    }
                    if (moveAnimation.isActive()) {
                        moveAnimation.stop();
                        invalidate();
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    public void startSelection() {
        if (moveAnimation.isActive()) {
            return;
        }
        selectionAnimation.init(SystemClock.elapsedRealtime());
        invalidate();
    }

    private void startExplosion() {
        if (selectionAnimation.isActive()) {
            selectionAnimation.stop();
        }
        explosionAnimation.setSpeed(dataHolder.getConfiguration().isFastAnimation());
        explosionAnimation.init(SystemClock.elapsedRealtime());
        invalidate();
    }

    public void startMove() {
        if (selectionAnimation.isActive()) {
            selectionAnimation.stop();
        }
        moveAnimation.setSpeed(dataHolder.getConfiguration().isFastAnimation());
        moveAnimation.setStartValues(dataHolder.getGameEngine().getGameField().getMaxMoveX(),
                dataHolder.getGameEngine().getGameField().getMaxMoveY());
        moveAnimation.init(SystemClock.elapsedRealtime());
        invalidate();
    }


    private void updateAnimationObjects() {
        selectionAnimation.update(SystemClock.elapsedRealtime());
        moveAnimation.update(SystemClock.elapsedRealtime());
        explosionAnimation.update(SystemClock.elapsedRealtime());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //drawField(canvas);
        drawBackGroud(canvas);

        if (moveAnimation.isActive()) {
            moveAnimation.draw(canvas);
        } else if (explosionAnimation.isActive()) {
            explosionAnimation.draw(canvas);
        } else {
            selectionAnimation.draw(canvas);
        }

        if (selectionAnimation.isActive() || moveAnimation.isActive() || explosionAnimation.isActive()) {
            invalidate();
        }
        updateAnimationObjects();
        if (false) {
            drawStatistic(canvas);
        }
    }


    private long lastTime = 0;
    private int renderCount = 0;
    private int speed = 0;

    private void drawStatistic(Canvas canvas) {
        renderCount++;
        long diffTime = SystemClock.elapsedRealtime() - lastTime;
        if (diffTime > 1000) {
            speed = Math.round(renderCount * 1000f / diffTime);
            lastTime = SystemClock.elapsedRealtime();
            renderCount = 0;
        }
        canvas.drawText("FPS: " + speed, 30, 100, paintStatistics);
    }


    private int backGroundImageFieldSizeX = 0;
    private int backGroundImageFieldSizeY = 0;
    private Bitmap backgroundImageSaved = null;

    private void drawBackGroud(Canvas canvas) {
        if (dataHolder.getGameEngine().getGameField().getSizeY() != backGroundImageFieldSizeY ||
                dataHolder.getGameEngine().getGameField().getSizeX() != backGroundImageFieldSizeX ||
                backgroundImageSaved == null) {

            backgroundImageSaved = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
            Canvas newCanvas = new Canvas(backgroundImageSaved);
            drawBackGroudImageOnGivenCanvas(newCanvas);
            backGroundImageFieldSizeX = dataHolder.getGameEngine().getGameField().getSizeX();
            backGroundImageFieldSizeY = dataHolder.getGameEngine().getGameField().getSizeY();
        }
        canvas.drawBitmap(backgroundImageSaved, 0, 0, null);
    }

    private void drawBackGroudImageOnGivenCanvas(Canvas canvas) {
        canvas.drawBitmap(dataHolder.getBackgroundImage(), 0, 0, null);


        dataHolder.getLightPaint().setColor(Color.rgb(0x83, 0x83, 0x83));
        dataHolder.getDarkPaint().setColor(Color.rgb(0x3b, 0x3b, 0x3b));


        final int cellSize = dataHolder.getCellSize();
        final int sizeY = dataHolder.getGameEngine().getGameField().getSizeY();
        final int sizeX = dataHolder.getGameEngine().getGameField().getSizeX();
        for (int i = 1; i < sizeX; i++) {
            canvas.drawLine(i * cellSize - 1, 0, i * cellSize - 1, sizeY * cellSize, dataHolder.getLightPaint());
            canvas.drawLine(i * cellSize, 0, i * cellSize, sizeY * cellSize, dataHolder.getDarkPaint());
        }

        for (int i = 1; i < sizeY; i++) {
            canvas.drawLine(0, i * cellSize, sizeX * cellSize, i * cellSize, dataHolder.getDarkPaint());
            canvas.drawLine(0, i * cellSize + 1, sizeX * cellSize, i * cellSize + 1, dataHolder.getLightPaint());
        }
    }

    public void setConfiguration(Configuration configuration) {
        dataHolder.setConfiguration(configuration);
    }

    public void pause() {
        if (moveAnimation.isActive()) {
            moveAnimation.stop();
        }
        if (selectionAnimation.isActive()) {
            selectionAnimation.stop();
        }
    }

    public void onAnimationFinished(Animation animation) {
        if (animation == moveAnimation) {
            dataHolder.getGameEngine().moveFinished();
        } else if (animation == explosionAnimation) {
            dataHolder.getGameEngine().selectionExploded();
            startMove();
        }
    }

    public int getCellSize() {
        return dataHolder.getCellSize();
    }

    public void setCellSize(int value) {
        dataHolder.setCellSize(value);
    }
}