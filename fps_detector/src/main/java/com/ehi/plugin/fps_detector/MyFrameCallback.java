package com.ehi.plugin.fps_detector;

import android.view.Choreographer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Omooo
 * @version v1.0
 * @Date 2020/03/11 16:42
 * desc :
 */
public class MyFrameCallback implements Choreographer.FrameCallback {
    private Choreographer mChoreographer;

    private long frameStartTime = 0;
    private int framesRendered = 0;

    private List<Audience> listeners = new ArrayList<>();
    private int interval = 500;

    public MyFrameCallback() {
        mChoreographer = Choreographer.getInstance();
    }

    public void start() {
        mChoreographer.postFrameCallback(this);
    }

    public void stop() {
        frameStartTime = 0;
        framesRendered = 0;
        mChoreographer.removeFrameCallback(this);
    }

    public void addListener(Audience l) {
        listeners.add(l);
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        long currentTimeMills = TimeUnit.NANOSECONDS.toMillis(frameTimeNanos);
        if (frameStartTime > 0) {
            final long timeSpan = currentTimeMills - frameStartTime;
            framesRendered++;

            if (timeSpan > interval) {
                final double fps = framesRendered * 1000 / (double) timeSpan;
                frameStartTime = currentTimeMills;
                framesRendered = 0;
                for (Audience audience : listeners) {
                    audience.heartbeat(fps);
                }
            }
        } else {
            frameStartTime = currentTimeMills;
        }

        mChoreographer.postFrameCallback(this);
    }
}
