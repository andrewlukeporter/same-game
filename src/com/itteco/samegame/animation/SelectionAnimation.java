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

/**
 * Animate selected balls
 *
 * @author szotov
 */
public class SelectionAnimation extends SameGameAnimation {
    private final long FRAMES_PER_SECOND = 12;
    private final long FRAMES_COUNT = 6;
    private final long START_FRAMES = 9;
    private int frame = 0;

    public SelectionAnimation(SameGameGraphicDataHolder dataHolder) {
        super(dataHolder);
    }

    @Override
    protected void recalculateValues() {
        long diff = getDiffereceTicks() * FRAMES_PER_SECOND / 1000;
        if (diff <= START_FRAMES) {
            frame = (int) diff;
            return;
        }
        frame = (int) (START_FRAMES + ((diff - START_FRAMES) % FRAMES_COUNT));
    }

    public int getCurrentFrame() {
        return frame;
    }

    public void draw(Canvas canvas) {
        SameGameGraphicDataHolder dataHolder = getDataHolder();
        final GameField gameField = dataHolder.getGameEngine().getGameField();
        final int sizeX = gameField.getSizeX();
        final int sizeY = gameField.getSizeY();
        final int cellSize = dataHolder.getCellSize();
        final Bitmap bitmap = dataHolder.getBallSkin();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if (gameField.getItemColor(i, j) > 0) {
                    GraphicUtils.drawSprite(i * cellSize - 1, j * cellSize + 1, cellSize,
                            gameField.isItemSelected(i, j) ? getCurrentFrame() : 0,
                            gameField.getItemColor(i, j) - 1, bitmap, canvas);
                }
            }
        }

    }
}
