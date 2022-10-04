package dev.tarico.api.pluginapi;


/**
 * 转头数据
 */
public class Rotation {
    public float yaw, pitch = 0;

    /**
     * 转头数据
     * @param yaw 水平转头
     * @param pitch 垂直转头
     */
    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
