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
import android.graphics.Region;

/**
 * A set of graphic functions.
 */
public class GraphicUtils {

    /**
     * Draw specified in parameter <code>n</code> sprite item on position <code>x,y</code> and
     * <code>type</code> is define the element type.
     *
     * @param x        horisontal positioin
     * @param y        vertical hosition
     * @param cellSize size of the cell. Sprite, actually is square.
     * @param n        position of the sprite
     * @param type
     * @param bitmap   image with all sprites
     * @param canvas   place where we will draw
     */
    public static void drawSprite(float x, float y, float cellSize, int n, int type, Bitmap bitmap, Canvas canvas) {
        canvas.save();   // save clip region
        canvas.clipRect(x, y, x + cellSize, y + cellSize, Region.Op.REPLACE);
        canvas.drawBitmap(bitmap, x - (n * cellSize), y - (type * cellSize), null);
        canvas.restore();// restore clip region
    }

}
