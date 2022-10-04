package dev.tarico.utils.anim.animator;

import dev.tarico.Client;

public class Animator {
    private float value, min, max, speed, time;
    private boolean reversed;
    private Easing ease;

    public Animator() {
        this.ease = Easing.LINEAR;
        this.value = 0;
        this.min = 0;
        this.max = 1;
        this.speed = 50;
        this.reversed = false;
    }

    public void reset() {
        time = min;
    }

    public void resetMax() {
        time = max;
    }

    public Animator update() {
        if (reversed) {
            if (time > min) time -= (Client.instance.getRenderDeltaTime() * .001F * speed);
        } else {
            if (time < max) time += (Client.instance.getRenderDeltaTime() * .001F * speed);
        }
        time = clamp(time, min, max);
        this.value = ease.ease(time, min, max, max);
        return this;
    }

    public float getValue() {
        return value;
    }

    public Animator setValue(float value) {
        this.value = value;
        return this;
    }

    public Animator setMin(float min) {
        this.min = min;
        return this;
    }

    public Animator setMax(float max) {
        this.max = max;
        return this;
    }

    public Animator setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public Animator setReversed(boolean reversed) {
        this.reversed = reversed;
        return this;
    }

    public Animator setEase(Easing ease) {
        this.ease = ease;
        return this;
    }

    private float clamp(float num, float min, float max) {
        return num < min ? min : Math.min(num, max);
    }
}
