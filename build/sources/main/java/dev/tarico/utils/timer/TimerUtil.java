package dev.tarico.utils.timer;

public class TimerUtil {
    public long lastMS;

    private long currentMS = System.currentTimeMillis();

    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasReached(double milliseconds) {
        return (double) (this.getCurrentMS() - this.lastMS) >= milliseconds;
    }

    public void setCurrentMS(long currentMS) {
        this.currentMS = currentMS;
    }

    public boolean hasElapsed(long milliseconds) {
        return elapsed() > milliseconds;
    }

    public void reset() {
        this.lastMS = this.getCurrentMS();
    }

    public boolean delay(float milliSec) {
        return (float) (this.getTime() - this.lastMS) >= milliSec;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public long elapsed() {
        return System.currentTimeMillis() - currentMS;
    }
}
