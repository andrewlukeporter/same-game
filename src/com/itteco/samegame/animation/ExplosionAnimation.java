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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.itteco.samegame.animation.GraphicUtils;
import com.itteco.samegame.SameGameGraphicDataHolder;
import com.itteco.samegame.engine.GameField;

public class ExplosionAnimation extends SameGameAnimation {
    private long FRAMES_PER_SECOND = 15;
    private final long FRAMES_COUNT = 11;
    private int frame = 0;

    public ExplosionAnimation(final AnimationCallback animationCallBack, final SameGameGraphicDataHolder dataHolder) {
        super(animationCallBack, dataHolder);
    }

    @Override
    protected void recalculateValues() {
        long diff = getDiffereceTicks() * FRAMES_PER_SECOND / 1000;
        frame = (int) (diff);
        if (diff >= FRAMES_COUNT) {
            frame = (int) FRAMES_COUNT - 1;
            stop();
        }
    }

    public int getCurrentFrame() {
        return frame;
    }

    public void setSpeed(boolean fastAnimation) {
        if (fastAnimation) {
            FRAMES_PER_SECOND = 300;
        } else {
            FRAMES_PER_SECOND = 15;
        }
    }

    public void draw(Canvas canvas) {
        final SameGameGraphicDataHolder holder = getDataHolder();
        int sizeX = holder.getGameEngine().getGameField().getSizeX();
        int sizeY = holder.getGameEngine().getGameField().getSizeY();
        GameField gameField = holder.getGameEngine().getGameField();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if (gameField.getItemColor(i, j) > 0) {
                    final boolean isCurrentItemSelected = gameField.isItemSelected(i, j);
                    final Bitmap bitmap = isCurrentItemSelected ?
                            holder.getExploseBallSkin() :
                            holder.getBallSkin();

                    final int cellSize = holder.getCellSize();
                    final int currentFrame = getCurrentFrame();
                    GraphicUtils.drawSprite(i * cellSize - 1,
                            j * cellSize + 1, cellSize,
                            isCurrentItemSelected ? currentFrame : 0,
                            gameField.getItemColor(i, j) - 1, bitmap, canvas);
                }
            }
        }
    }
}
