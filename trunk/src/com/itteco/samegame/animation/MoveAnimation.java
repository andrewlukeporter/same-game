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
package com.itteco.samegame.animation;

import android.graphics.Canvas;
import com.itteco.samegame.animation.GraphicUtils;
import com.itteco.samegame.SameGameGraphicDataHolder;
import com.itteco.samegame.engine.GameField;

public class MoveAnimation extends SameGameAnimation {
    private float SPEED_X = 4; // cell per second
    private final int CELL_RESOLUTION = 1000; // 1000 items per cell
    private int moveOnXCurrent = 0;
    private int moveOnYCurrent = 0;
    private int movesForX = 5;
    private int movesForY = 5;
    private float timeForYMove = 0;
    private float ACCELERATION = 1f / 200f;

    public MoveAnimation(AnimationCallback animationCallBack, SameGameGraphicDataHolder dataHolder) {
        super(animationCallBack, dataHolder);
    }

    @Override
    protected void recalculateValues() {
        float diff = getDiffereceTicks();
        float tmpDiff = diff;
        moveOnYCurrent = Math.round(ACCELERATION * tmpDiff * tmpDiff * CELL_RESOLUTION / SECOND);
        if (moveOnYCurrent > movesForY) {
            moveOnYCurrent = movesForY;
            diff -= timeForYMove;
            moveOnXCurrent = Math.round(diff * CELL_RESOLUTION * SPEED_X / SECOND);
            if (moveOnXCurrent > movesForX) {
                moveOnXCurrent = movesForX;
            }
        } else {
            moveOnXCurrent = 0;
        }
        if (moveOnXCurrent == movesForX &&
                moveOnYCurrent == movesForY) {
            stop();
        }
    }

    public float getDeltaX(int targetDeltaX) {
        targetDeltaX *= CELL_RESOLUTION;
        if (targetDeltaX > moveOnXCurrent) {
            targetDeltaX = moveOnXCurrent;
        }
        return ((float) targetDeltaX) / CELL_RESOLUTION;
    }

    public float getDeltaY(int targetDeltaY) {
        targetDeltaY *= CELL_RESOLUTION;
        if (targetDeltaY > moveOnYCurrent) {
            targetDeltaY = moveOnYCurrent;
        }
        return ((float) targetDeltaY) / CELL_RESOLUTION;
    }

    public void setStartValues(int maxMoveX, int maxMoveY) {
        movesForX = maxMoveX * CELL_RESOLUTION;
        movesForY = maxMoveY * CELL_RESOLUTION;
        timeForYMove = (float) Math.sqrt(movesForY * SECOND / (ACCELERATION * CELL_RESOLUTION));
    }

    public void setSpeed(boolean fast) {
        if (fast) {
            SPEED_X = 18; // cell per second
            ACCELERATION = 12f / 200f;
        } else {
            SPEED_X = 7; // cell per second
            ACCELERATION = 3f / 200f;
        }
    }

    public void draw(Canvas canvas) {
        SameGameGraphicDataHolder dataHolder = getDataHolder();
        int sizeX = dataHolder.getGameEngine().getGameField().getSizeX();
        int sizeY = dataHolder.getGameEngine().getGameField().getSizeY();
        GameField gameField = dataHolder.getGameEngine().getGameField();
        int cellSize = dataHolder.getCellSize();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if (gameField.getItemColor(i, j) > 0) {
                    float deltaX = 0, deltaY = 0;
                    cellSize = dataHolder.getCellSize();
                    deltaX = getDeltaX(gameField.getMoveX(i, j)) * cellSize;
                    deltaY = getDeltaY(gameField.getMoveY(i, j)) * cellSize;

                    GraphicUtils.drawSprite(i * cellSize - deltaX - 1,
                            j * cellSize + 1 + deltaY,
                            cellSize, 0, gameField.getItemColor(i, j) - 1,
                            dataHolder.getBallSkin(), canvas);

                }
            }
        }
    }
}
