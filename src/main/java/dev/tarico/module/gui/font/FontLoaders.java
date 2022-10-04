package dev.tarico.module.gui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;

@SuppressWarnings("unused")
public abstract class FontLoaders {
    public static CFontRenderer I10 = new CFontRenderer(FontLoaders.getIcon(10), true, true);
    public static CFontRenderer I14 = new CFontRenderer(FontLoaders.getIcon(14), true, true);
    public static CFontRenderer I15 = new CFontRenderer(FontLoaders.getIcon(15), true, true);
    public static CFontRenderer I16 = new CFontRenderer(FontLoaders.getIcon(16), true, true);
    public static CFontRenderer I18 = new CFontRenderer(FontLoaders.getIcon(18), true, true);
    public static CFontRenderer I20 = new CFontRenderer(FontLoaders.getIcon(20), true, true);
    public static CFontRenderer I25 = new CFontRenderer(FontLoaders.getIcon(25), true, true);

    public static CFontRenderer F12 = new CFontRenderer(FontLoaders.getFont(12), true, true);
    public static CFontRenderer S33 = new CFontRenderer(FontLoaders.getSigma(33), true, true);
    public static CFontRenderer S18 = new CFontRenderer(FontLoaders.getSigma(18), true, true);

    public static CFontRenderer F13 = new CFontRenderer(FontLoaders.getFont(13), true, true);
    public static CFontRenderer F14 = new CFontRenderer(FontLoaders.getFont(14), true, true);
    public static CFontRenderer F16 = new CFontRenderer(FontLoaders.getFont(16), true, true);
    public static CFontRenderer F18 = new CFontRenderer(FontLoaders.getFont(18), true, true);
    public static CFontRenderer F20 = new CFontRenderer(FontLoaders.getFont(20), true, true);
    public static CFontRenderer F22 = new CFontRenderer(FontLoaders.getFont(22), true, true);
    public static CFontRenderer F23 = new CFontRenderer(FontLoaders.getFont(23), true, true);
    public static CFontRenderer F24 = new CFontRenderer(FontLoaders.getFont(24), true, true);
    public static CFontRenderer B16 = new CFontRenderer(FontLoaders.getBFont(16), true, true);
    public static CFontRenderer B18 = new CFontRenderer(FontLoaders.getBFont(18), true, true);
    public static CFontRenderer B20 = new CFontRenderer(FontLoaders.getBFont(20), true, true);
    public static CFontRenderer P20 = new CFontRenderer(FontLoaders.getProductsans(20), true, true);

    public static ArrayList<CFontRenderer> fonts = new ArrayList<>();


    public static CFontRenderer getFontRender(int size) {
        return fonts.get(size - 10);
    }

    public static Font getFont(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("fonts/font.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    public static Font getSigma(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("fonts/sigma.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    public static Font getIcon(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("fonts/icon.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    public static Font getProductsans(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("fonts/productsans.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    public static Font getBFont(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("fonts/roboto.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }


}

