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
package com.itteco.samegame.engine;

import android.content.SharedPreferences;

import java.util.ArrayList;

public class GameHistory {
    private static final String ITEMS_COUNT = "HISTORY_ITEMS_COUNT";
    private static final String HISTORY_CURRENT_POS = "HISTORY_CURRENT_POS";
    private static final String HISTORY_ITEM = "HISTORY_ITEM";

    private ArrayList<String> items;
    private int position;

    public void init() {
        position = 0;
        items = new ArrayList<String>(30);
    }


    public void add(String item) {
        if (position < items.size() - 1) {
            for (int i = items.size() - 1; i > position; i--) {
                items.remove(i);
            }
        }
        items.add(item);
        position = items.size() - 1;
    }

    public String undo() {
        if (position <= 0) {
            return null;
        }
        position--;
        return items.get(position);
    }


    public String redo() {
        if (position >= items.size() - 1) {
            return null;
        }
        return items.get(++position);
    }


    public void save(SharedPreferences.Editor editor) {
        int itemsCount = items.size();
        editor.putInt(ITEMS_COUNT, itemsCount);
        editor.putInt(HISTORY_CURRENT_POS, position);
        int i = 0;
        for (String item : items) {
            editor.putString(HISTORY_ITEM + i, item);
            i++;
        }

    }

    public void load(SharedPreferences settings) {
        int itemsCount = settings.getInt(ITEMS_COUNT, 0);
        position = settings.getInt(HISTORY_CURRENT_POS, 0);
        items = new ArrayList<String>(itemsCount);
        for (int i = 0; i < itemsCount; i++) {
            items.add(settings.getString(HISTORY_ITEM + i, null));
        }
    }
}
