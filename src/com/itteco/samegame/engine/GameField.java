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

import java.util.LinkedList;
import java.util.Random;

public class GameField {
    private int sizeX = 0;
    private int sizeY = 0;
    private byte[][] field;
    private byte[][] moveDataX;
    private byte[][] moveDataY;
    private byte maxMoveX = 0;
    private byte maxMoveY = 0;
    private LinkedList<Point> selection;
    private Random random;
    private EngineCallback engineCallback;
    private boolean gameOver = false;

    public GameField(EngineCallback engineCallback) {
        this.engineCallback = engineCallback;
        random = new Random();
        selection = new LinkedList<Point>();
    }

    public void initField(int k, int l, int m) {
        gameOver = false;
        sizeX = k;
        sizeY = l;
        removeSelection();
        createArrays();
        int i, j;
        for (i = 0; i < sizeY; i++) {
            for (j = 0; j < sizeX; j++) {
                field[i][j] = (byte) (random.nextInt(m) + 1);
            }
        }
    }

    private void createArrays() {
        field = new byte[sizeY][];
        int i, j;
        for (i = 0; i < sizeY; i++) {
            field[i] = new byte[sizeX];
        }

        moveDataX = new byte[sizeY][];
        for (i = 0; i < sizeY; i++) {
            moveDataX[i] = new byte[sizeX];
            for (j = 0; j < sizeX; j++) {
                moveDataX[i][j] = 0;
            }
        }
        moveDataY = new byte[sizeY][];
        for (i = 0; i < sizeY; i++) {
            moveDataY[i] = new byte[sizeX];
            for (j = 0; j < sizeX; j++) {
                moveDataY[i][j] = 0;
            }
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Must be called if gameover to check is bonus needed
     *
     * @return true if field is empty
     */
    public boolean isFieldEmpty() {
        for (int i = sizeY - 1; i >= 0; i--) {
            for (int j = sizeX - 1; j >= 0; j--) {
                if (field[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateGameOver() {
        for (int i = sizeX - 1; i >= 0; i--) {
            for (int j = sizeY - 1; j >= 0; j--) {
                byte color1 = field[j][i];
                if (color1 == 0) {
                    continue;
                }
                byte color2;
                if (i + 1 < sizeX) {
                    color2 = field[j][i + 1];
                    if (color2 != 0 && color1 == color2) {
                        return; //same colors
                    }
                }
                if (j + 1 < sizeY) {
                    color2 = field[j + 1][i];
                    if (color2 != 0 && color1 == color2) {
                        return; //same colors
                    }
                }
            }

        }

        //not found any near bolls with same collor
        gameOver = true;
    }

    public boolean touch(int x, int y) {
        Point point = new Point(x, y);

        if (selection.contains(point)) {
            // second selection - need to delete
            return true;
        }
        //first time click
        removeSelection();
        if (getItemColor(x, y) == 0) {
            // cell is empty
            return false;
        }
        addSelection(x, y, getItemColor(x, y));
        if (selection.size() == 1) {
            selection.clear();
        }
        return false;
    }

    private void addSelection(int x, int y, byte color) {
        Point p = new Point(x, y);
        if (selection.contains(p)) {
            return;
        }
        selection.add(p);
        if (x > 0 && getItemColor(x - 1, y) == color) {
            addSelection(x - 1, y, color);
        }

        if (x + 1 < sizeX && getItemColor(x + 1, y) == color) {
            addSelection(x + 1, y, color);
        }

        if (y > 0 && getItemColor(x, y - 1) == color) {
            addSelection(x, y - 1, color);
        }
        if (y + 1 < sizeY && getItemColor(x, y + 1) == color) {
            addSelection(x, y + 1, color);
        }
    }

    public void deleteSelection() {
        engineCallback.addSelectionScoreToOverall();

        // first go and delete balls
        for (Point point : selection) {
            field[point.getY()][point.getX()] = 0;
        }
        removeSelection();
    }

    public void moveBalls() {
        int i, j;
        int newX, newY;
        for (i = 0; i < sizeX; i++) {
            for (j = sizeY - 1; j >= 0; j--) {
                if (field[j][i] != 0 &&
                        (moveDataX[j][i] != 0 || moveDataY[j][i] != 0)) {
                    newX = i - moveDataX[j][i];
                    newY = j + moveDataY[j][i];
                    field[newY][newX] = field[j][i];
                    field[j][i] = 0;
                }

            }
        }

    }

    public void fillMoveData() {
        int i, j;
        byte xSpace = 0;
        byte ySpace;
        maxMoveX = 0;
        maxMoveY = 0;
        for (i = 0; i < sizeX; i++) {
            ySpace = 0;
            for (j = sizeY - 1; j >= 0; j--) {
                if (field[j][i] == 0) {
                    ySpace++;
                    moveDataX[j][i] = 0;
                    moveDataY[j][i] = 0;
                    continue;
                }
                if (ySpace > maxMoveY) {
                    maxMoveY = ySpace;
                }
                if (xSpace > maxMoveX) {
                    maxMoveX = xSpace;
                }
                moveDataX[j][i] = xSpace;
                moveDataY[j][i] = ySpace;
            }
            if (ySpace >= sizeY) {
                xSpace++;
            }
        }
    }

    public int getSelectionCount() {
        return selection.size();
    }

    public byte getItemColor(int x, int y) {
        return field[y][x];
    }

    private Point pointForTests = new Point((byte) 0, (byte) 0);

    public boolean isItemSelected(int x, int y) {
        pointForTests.init(x, y);
        return selection.contains(pointForTests);
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public boolean isSelectionEmpty() {
        return selection.isEmpty();
    }

    public byte getMaxMoveX() {
        return maxMoveX;
    }

    public byte getMaxMoveY() {
        return maxMoveY;
    }

    public byte getMoveX(int x, int y) {
        return moveDataX[y][x];
    }

    public byte getMoveY(int x, int y) {
        return moveDataY[y][x];
    }

    public String save(byte[] array, int startPosition) {
        array[startPosition++] = (byte) sizeX;
        array[startPosition++] = (byte) sizeY;
        int index = startPosition;
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                array[index] = field[i][j];
                index++;
            }
        }
        return new String(array);
    }

    public void load(byte[] array, int startPosition) {
        removeSelection();
        sizeX = array[startPosition++];
        sizeY = array[startPosition++];
        createArrays();
        int index = startPosition;
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                field[i][j] = array[index];
                index++;
            }
        }
    }

    private void removeSelection() {
        selection.clear();
    }
}