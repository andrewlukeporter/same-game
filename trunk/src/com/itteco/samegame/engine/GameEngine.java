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

import com.itteco.samegame.configuration.Configuration;
import com.itteco.samegame.engine.TouchResult;


public class GameEngine implements EngineCallback {
    private GameField gameField;
    private Configuration configuration;
    private EngineNotificationCallback engineNotificationCallback;
    private FieldUpdateCallback fieldUpdateCallback = null;
    private long overallScore = 0;
    private int selectionScore = 0;

    public void setEngineNotificationCallback(EngineNotificationCallback engineNotificationCallback) {
        this.engineNotificationCallback = engineNotificationCallback;
    }

    public void addSelectionScoreToOverall() {
        overallScore += selectionScore;
    }

    public GameEngine(Configuration configuration) {
        this.configuration = configuration;
        gameField = new GameField(this);
    }

    public void initField() {
        gameField.initField(configuration.getSizeX(),
                configuration.getSizeY(),
                configuration.getColorsCount());
        overallScore = 0;
        selectionScore = 0;
        recalculateScore();
        if (fieldUpdateCallback != null) {
            fieldUpdateCallback.onFieldUpdatedEvent();
        }
    }

    public TouchResult touchAction(int x, int y) {
        if (gameField.touch(x, y)) {
            return TouchResult.explode;
        }
        recalculateScore();
        if (gameField.isSelectionEmpty()) {
            return TouchResult.no_action;
        } else {
            return TouchResult.selected;
        }
    }

    public void selectionExploded() {
        gameField.deleteSelection();
        gameField.fillMoveData();
        recalculateScore();
    }

    private void recalculateScore() {
        int selectionCount = gameField.getSelectionCount() - 2;
        if (selectionCount >= 0) {
            selectionScore = selectionCount * selectionCount;
        } else {
            selectionScore = 0;
        }
        engineNotificationCallback.updateScore(overallScore, selectionScore);
    }

    public GameField getGameField() {
        return gameField;
    }

    public long getOverallScore() {
        return overallScore;
    }

    public int getSelectionScore() {
        return selectionScore;
    }

    public void moveFinished() {
        gameField.moveBalls();
        if (fieldUpdateCallback != null) {
            fieldUpdateCallback.onFieldUpdatedEvent();
        }
        gameField.updateGameOver();

        if (gameField.isFieldEmpty()) {
            overallScore += 1000;
        }

        recalculateScore();
        if (gameField.isGameOver()) {
            engineNotificationCallback.gameOver();
        }
    }

    public void setFieldUpdateCallback(FieldUpdateCallback callback) {
        fieldUpdateCallback = callback;
    }

    public String saveToString() {
        int fieldDataSize = gameField.getSizeX() * gameField.getSizeY() + 2;
        byte[] array = new byte[fieldDataSize + 8];
        int position = 0;
        gameField.save(array, position);
        long scoreToSave = getOverallScore();
        position = fieldDataSize;
        for (int i = 0; i < 8; i++) {
            array[position + 7 - i] = (byte) (scoreToSave >>> (i * 8));
        }
        return new String(array);
    }

    public void loadFromString(String string) {
        byte array[] = string.getBytes();
        int position = 0;
        gameField.load(array, position);
        int fieldDataSize = gameField.getSizeX() * gameField.getSizeY() + 2;
        position = fieldDataSize;
        long scoreToLoad = 0;
        if (fieldDataSize <= array.length + 8) {
            for (int i = 0; i < 8; i++) {
                scoreToLoad <<= 8;
                scoreToLoad ^= (long) array[position + i] & 0xFF;
            }
            overallScore = scoreToLoad;
            selectionScore = 0;
        } else {
            overallScore = 0;
            selectionScore = 0;
        }
        recalculateScore();
    }

}