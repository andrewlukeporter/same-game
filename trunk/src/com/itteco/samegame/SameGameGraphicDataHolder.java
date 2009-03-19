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

import android.graphics.Bitmap;
import android.graphics.Paint;
import com.itteco.samegame.engine.GameEngine;
import com.itteco.samegame.configuration.Configuration;

/**
 * User: szotov
 * Date: Mar 16, 2009
 * Time: 9:46:18 AM
 */
public class SameGameGraphicDataHolder {
    private Configuration configuration;
    private GameEngine gameEngine;

    private Paint lightPaint;
    private Paint darkPaint;

    private Bitmap ballSkin;
    private Bitmap exploseBallSkin;
    private Bitmap backgroundImage;

    private int cellSize = 30;


    public SameGameGraphicDataHolder() {
        initData();
    }

    private void initData() {
        lightPaint = new Paint();
        darkPaint = new Paint();
        lightPaint.setAntiAlias(false);
        lightPaint.setStrokeWidth(1);
        darkPaint.setAntiAlias(false);
        darkPaint.setStrokeWidth(1);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public Paint getLightPaint() {
        return lightPaint;
    }

    public Paint getDarkPaint() {
        return darkPaint;
    }

    public Bitmap getBallSkin() {
        return ballSkin;
    }

    public void setBallSkin(Bitmap ballSkin) {
        this.ballSkin = ballSkin;
    }

    public Bitmap getExploseBallSkin() {
        return exploseBallSkin;
    }

    public void setExploseBallSkin(Bitmap exploseBallSkin) {
        this.exploseBallSkin = exploseBallSkin;
    }

    public Bitmap getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Bitmap backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }
}
