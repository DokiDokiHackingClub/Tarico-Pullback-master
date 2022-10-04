package dev.tarico.api.pluginapi;


public class Vec3 {
    public double xCoord, yCoord, zCoord;

    public Vec3(double x, double y, double z) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public static Vec3 getVec3(net.minecraft.util.Vec3 vec) {
        return new Vec3(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public double getxCoord() {
        return xCoord;
    }

    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    public double getyCoord() {
        return yCoord;
    }

    public void setyCoord(double yCoord) {
        this.yCoord = yCoord;
    }

    public double getzCoord() {
        return zCoord;
    }

    public void setzCoord(double zCoord) {
        this.zCoord = zCoord;
    }

    public net.minecraft.util.Vec3 getNativeVec3() {
        return new net.minecraft.util.Vec3(xCoord, yCoord, zCoord);
    }
}
