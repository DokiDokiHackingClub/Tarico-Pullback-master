package dev.tarico.api.pluginapi;


import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

/**
 * 客户端渲染
 */
public class PluginRenderUtils {
    public static Minecraft mc = Minecraft.getMinecraft();
    static PluginRenderUtils INSTANCE = new PluginRenderUtils();

    /**
     * 获取本类实例
     *
     * @return 本类实例INSTANCE
     */
    public static PluginRenderUtils getInstance() {
        return INSTANCE;
    }

    /**
     * 获取窗口尺寸
     *
     * @return int[] {Width, Height}
     */

    public int[] getWindowSize() {
        ScaledResolution res = new ScaledResolution(mc);
        return new int[]{res.getScaledWidth(), res.getScaledHeight()};
    }

    /**
     * 绘制字符串
     *
     * @param text       要渲染的字符串
     * @param x          轴
     * @param y          轴
     * @param color      RGB颜色代码
     * @param dropShadow 阴影
     * @return 绘制长度
     */

    public int drawString(String text, float x, float y, int color, boolean dropShadow) {
        return mc.fontRendererObj.drawString(text, x, y, color, dropShadow);
    }

    /**
     * 获取字符串宽度
     *
     * @param text 要渲染的字符串
     * @return int类型字符串宽度值
     */

    public int getStringWidth(String text) {
        return mc.fontRendererObj.getStringWidth(text);
    }

    /**
     * 渲染使用Unicode字体渲染字符串
     *
     * @param text  要渲染的字符串
     * @param x     x轴
     * @param y     y轴
     * @param color RGB颜色代码
     */

    public void drawUnicodeString(String text, float x, float y, int color) {
        FontLoaders.F18.drawString(text, x, y, color);
    }

    /**
     * 使用平滑字体渲染字符串(有阴影)
     *
     * @param text  要渲染的字符串
     * @param x     x轴
     * @param y     y轴
     * @param color RGB颜色代码
     */

    public void drawUnicodeStringWithShadow(String text, float x, float y, int color) {
        FontLoaders.F18.drawStringWithShadow(text, x, y, color);
    }

    /**
     * 获取平滑字体字符串宽度
     *
     * @param text 需要计算的字符串
     * @return int类型宽度值
     */

    public float getUnicodeStringWidth(String text) {
        return FontLoaders.F18.getStringWidth(text);
    }



    /**
     * 绘制图形
     *
     * @param left   左
     * @param top    顶部
     * @param right  右
     * @param bottom 底部
     * @param color  RGB颜色代码
     */

    public void drawRect(float left, float top, float right, float bottom, int color) {
        RenderUtil.drawRect(left, top, right, bottom, color);
    }

    /**
     * 绘制图片
     *
     * @param image  实例化Image类型 (输入图片的base64)
     * @param x      x轴
     * @param y      y轴
     * @param width  图片宽度
     * @param height 图片高度
     */

    public void drawImage(Image image, int x, int y, int width, int height) {
        RenderUtil.drawImage(image.location.getResourceLocation(), x, y, width, height);
    }
}
