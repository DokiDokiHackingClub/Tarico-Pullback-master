package dev.tarico.utils.path;

import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;

public final class GLUtils {
    private final LinkedList<Integer> enableToggleList = new LinkedList<>();
    private final LinkedList<Integer> disableToggleList = new LinkedList<>();

    public void enable(int... caps) {
        for (int cap : caps) {
            enable(cap);
        }
    }

    public void enable(int cap) {
        glEnable(cap);
        enableToggleList.add(cap);
    }

    public void disable(int... caps) {
        for (int cap : caps) {
            disable(cap);
        }
    }

    public void disable(int cap) {
        glDisable(cap);
        disableToggleList.add(cap);
    }

    public void enableNoToggle(int... cap) {
        for (int i : cap) {
            enableNoToggle(i);
        }
    }

    public void enableNoToggle(int cap) {
        glEnable(cap);
    }

    public void disableNoToggle(int... cap) {
        for (int i : cap) {
            disableNoToggle(i);
        }
    }

    public void disableNoToggle(int cap) {
        glDisable(cap);
    }

    public void toggle() {
        int cap;

        while (!enableToggleList.isEmpty()) {
            cap = enableToggleList.poll();

            glDisable(cap);
        }

        while (!disableToggleList.isEmpty()) {
            cap = disableToggleList.poll();

            glEnable(cap);
        }
    }

    public void pushMatrix() {
        glPushMatrix();
    }

    public void popMatrix() {
        glPopMatrix();
    }

    public void scale(double x, double y, double z) {
        glScaled(x, y, z);
    }
}
