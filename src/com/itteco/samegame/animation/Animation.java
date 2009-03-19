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

/**
 * This class is abstract base for classes which need to hold all info related for some animation process.
 * Generally it will be initialized with initial tick and then will be updated.
 * It can be active or inactive.
 *
 * @author szotov
 */
public abstract class Animation {
    public static final long SECOND = 1000; // milliseconds per second
    private long initalTicks;
    private long currentTicks;
    private long differenceTicks;
    private boolean active;
    private boolean paused;
    private AnimationCallback callback = null;

    public Animation() {
        initalTicks = 0;
        currentTicks = 0;
        differenceTicks = 0;
        active = false;
        paused = false;
    }

    public Animation(final AnimationCallback animationCallBack) {
        this();
        callback = animationCallBack;
    }

    public void init(final long newTick) {
        active = true;
        initalTicks = newTick;
        currentTicks = initalTicks;
        recalculateValues();
    }

    public void stop() {
        if (callback != null) {
            callback.onAnimationFinished(this);
        }
        active = false;
    }

    public void update(final long tick) {
        if (!active) {
            return;
        }
        currentTicks = tick;
        recalculateValues();
    }

    public void pause() {
        paused = true;
        differenceTicks = currentTicks - initalTicks;
    }

    public void continueAnimation(final long fromTick) {
        initalTicks = fromTick - differenceTicks;
        recalculateValues();
    }

    public boolean isActive() {
        return active && !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    protected long getDiffereceTicks() {
        return currentTicks - initalTicks;
    }

    protected abstract void recalculateValues();

}
