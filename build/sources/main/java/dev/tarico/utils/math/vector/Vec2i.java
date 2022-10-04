package dev.tarico.utils.math.vector;

public class Vec2i {
    private int x, y;

    public Vec2i() {
        this(0, 0);
    }

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Vec2i copy() {
        return new Vec2i(this.x, this.y);
    }
}
