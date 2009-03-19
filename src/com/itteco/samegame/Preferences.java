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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;

/**
 * Application preferences.
 */
public class Preferences extends PreferenceActivity {
    public static final String KEY_SIZE = "key_size";
    public static final String KEY_FAST_ANIMATION = "key_fast_animation";

    public enum Difficulty {
        Easy(0, 8, 10, 2, 40),
        Medium(1, 8, 10, 3, 40),
        Hard(2, 8, 10, 4, 40);

        private final int level;
        private final int x;
        private final int y;
        private final int colors;
        private final int cellSize;


        Difficulty(int level, int x, int y, int colors, int cellSize) {
            this.level = level;
            this.x = x;
            this.y = y;
            this.colors = colors;
            this.cellSize = cellSize;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getColorCount() {
            return colors;
        }

        public int getCellSize() {
            return cellSize;
        }

        public int getLevel() {
            return level;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPreferenceScreen(createPreferenceHierarchy());
    }

    public static Difficulty getCurrentSize(Context context) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final String size = preferences
                .getString(KEY_SIZE, DIFFICULTY.toString());
        return Difficulty.valueOf(size);
    }

    /**
     * By defalut we will use 3 colors.
     */
    public static final Difficulty DIFFICULTY = Difficulty.Medium;

    private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        // fast animation true/false
        CheckBoxPreference fastAnimationPref = new CheckBoxPreference(this);
        fastAnimationPref.setDefaultValue(false);
        fastAnimationPref.setKey(KEY_FAST_ANIMATION);
        fastAnimationPref.setTitle(R.string.settings_fast_animation);
        fastAnimationPref.setSummary(R.string.settings_fast_animation_summary);
        root.addPreference(fastAnimationPref);
        // field size
        ListPreference difficultyPref = new ListPreference(this);
        difficultyPref.setEntries(new String[]{
                getString(R.string.settings_difficulty1),
                getString(R.string.settings_difficulty2),
                getString(R.string.settings_difficulty3),
        });
        difficultyPref.setEntryValues(new String[]{
                Difficulty.Easy.toString(),
                Difficulty.Medium.toString(),
                Difficulty.Hard.toString()});
        difficultyPref.setKey(KEY_SIZE);
        difficultyPref.setTitle(R.string.settings_difficulty);
        difficultyPref.setSummary(R.string.settings_difficulty_summary);
        final SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPrefs.contains(KEY_SIZE)) {
            difficultyPref.setValue(DIFFICULTY.toString());
        }
        root.addPreference(difficultyPref);
        return root;
    }

    public Preferences() {
        super();
    }
}