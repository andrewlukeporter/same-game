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
package com.itteco.samegame.configuration;

import android.content.Context;
import android.preference.PreferenceManager;
import com.itteco.samegame.R;
import com.itteco.samegame.Preferences;

/**
 * Storage for configuration data.
 * It can read preferences data.
 */
public class ConfigurationEntity implements Configuration {
    private int skin = R.drawable.balls;
    private boolean fastAnimation = false;
    private int sizeX = 0;
    private int sizeY = 0;
    private int cellSize = 30;
    private int colorsCount = 0;
    private int level = 0;


    /**
     * Initialize configuration values. Configuration parameters will be taken using provided context.
     *
     * @param context
     */
    public void updateValues(Context context) {
        skin = R.drawable.balls;
        Preferences.Difficulty difficulty = Preferences.getCurrentSize(context);
        sizeX = difficulty.getX();
        sizeY = difficulty.getY();
        level = difficulty.getLevel();
        colorsCount = difficulty.getColorCount();
        cellSize = difficulty.getCellSize();
        fastAnimation = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Preferences.KEY_FAST_ANIMATION, false);
    }

    public int getSkin() {
        return skin;
    }

    public boolean isFastAnimation() {
        return fastAnimation;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getColorsCount() {
        return colorsCount;
    }

    public int getLevel() {
        return level;
    }
}
