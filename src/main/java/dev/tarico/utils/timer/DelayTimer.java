package dev.tarico.utils.timer;

public final class DelayTimer {
    private final TimerUtils timerUtils = new TimerUtils(true);

    public boolean delay(int tick) {
        int aps = 20 / tick;
        return timerUtils.hasReached(50 * aps);
    }

    public void reset() {
        timerUtils.reset();
    }
}
