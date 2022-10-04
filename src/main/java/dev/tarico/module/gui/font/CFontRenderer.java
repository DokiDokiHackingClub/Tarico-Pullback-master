package dev.tarico.module.gui.font;

import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CFontRenderer extends CFont {
    private final int[] colorCode = new int[32];
    protected CharData[] boldChars = new CharData[256];
    protected CharData[] italicChars = new CharData[256];
    protected CharData[] boldItalicChars = new CharData[256];
    protected DynamicTexture texBold;
    protected DynamicTexture texItalic;
    protected DynamicTexture texItalicBold;

    public CFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        this.setupMinecraftColorcodes();
        this.setupBoldItalicIDs();
    }

    public void drawStringWithShadow(String text, double x, double y, int color) {
        this.drawString(text, x + 0.5, y + 0.5, color, true);
        this.drawString(text, x, y, color, false);
    }

    public float drawString(String text, float x, float y, int color) {
        GlStateManager.color(1, 1, 1);

        RenderUtil.color(color);

        return this.drawString(text, x, y, color, false);
    }

    public void drawCenteredString(String text, double x, double y, int color) {
        GlStateManager.color(1, 1, 1);

        this.drawString(text, (float) (x - (float) (this.getStringWidth(text) / 2)), (float) y, color);

    }

    public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        this.drawStringWithShadow(text, x - (float) (this.getStringWidth(text) / 2), y, color);
    }

    public float drawString(String text, double x, double y, int color, boolean shadow) {
        GlStateManager.enableBlend();
        GlStateManager.disableBlend();
        x -= 1.0;
        if (text == null) {
            return 0.0f;
        }
        if (color == 553648127) {
            color = 16777215;
        }
        if ((color & -67108864) == 0) {
            color |= -16777216;
        }
        if (shadow) {
            color = new Color(0, 0, 0).getRGB();
        }
        CharData[] currentData = this.charData;
        float alpha = (float) (color >> 24 & 255) / 255.0f;
        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underline = false;
        x *= 2.0;
        y = (y - 3.0) * 2.0;
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color((float) (color >> 16 & 255) / 255.0f, (float) (color >> 8 & 255) / 255.0f, (float) (color & 255) / 255.0f, alpha);
        int size = text.length();
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.tex.getGlTextureId());
        GL11.glBindTexture(3553, this.tex.getGlTextureId());
        int i = 0;
        while (i < size) {
            char character = text.charAt(i);
            if (character == '\u00a7') {
                int colorIndex = 21;
                try {
                    colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;
                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }
                    if (shadow) {
                        colorIndex += 16;
                    }
                    int colorcode = this.colorCode[colorIndex];
                    GlStateManager.color((float) (colorcode >> 16 & 255) / 255.0f, (float) (colorcode >> 8 & 255) / 255.0f, (float) (colorcode & 255) / 255.0f, alpha);
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(this.texBold.getGlTextureId());
                        currentData = this.boldChars;
                    }
                } else if (colorIndex == 18) {
                    strikethrough = true;
                } else if (colorIndex == 19) {
                    underline = true;
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(this.texItalic.getGlTextureId());
                        currentData = this.italicChars;
                    }
                } else {
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.color((float) (color >> 16 & 255) / 255.0f, (float) (color >> 8 & 255) / 255.0f, (float) (color & 255) / 255.0f, alpha);
                    GlStateManager.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;
                }
                ++i;
            } else if (character < currentData.length) {
                GL11.glBegin(4);
                this.drawChar(currentData, character, (float) x, (float) y);
                GL11.glEnd();
                if (strikethrough) {
                    this.drawLine(x, y + (double) (currentData[character].height / 2), x + (double) currentData[character].width - 8.0, y + (double) (currentData[character].height / 2));
                }
                if (underline) {
                    this.drawLine(x, y + (double) currentData[character].height - 2.0, x + (double) currentData[character].width - 8.0, y + (double) currentData[character].height - 2.0);
                }
                x += currentData[character].width - 8 + this.charOffset;
            }
            ++i;
        }
        GL11.glHint(3155, 4352);
        GL11.glPopMatrix();
        return (float) x / 2.0f;
    }

    @Override
    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        int width = 0;
        CharData[] currentData = this.charData;
        int size = text.length();
        int i = 0;
        while (i < size) {
            char character = text.charAt(i);
            if (character == '\u00a7') {
                ++i;
            } else if (character < currentData.length) {
                width += currentData[character].width - 8 + this.charOffset;
            }
            ++i;
        }
        return width / 2;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        this.setupBoldItalicIDs();
    }

    private void setupBoldItalicIDs() {
        this.texBold = this.setupTexture(this.font.deriveFont(Font.BOLD), this.antiAlias, this.fractionalMetrics, this.boldChars);
        this.texItalic = this.setupTexture(this.font.deriveFont(Font.ITALIC), this.antiAlias, this.fractionalMetrics, this.italicChars);
    }

    private void drawLine(double x, double y, double x1, double y1) {
        GL11.glDisable(3553);
        GL11.glLineWidth((float) 1.0);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    private void setupMinecraftColorcodes() {
        int index = 0;
        while (index < 32) {
            int noClue = (index >> 3 & 1) * 85;
            int red = (index >> 2 & 1) * 170 + noClue;
            int green = (index >> 1 & 1) * 170 + noClue;
            int blue = (index & 1) * 170 + noClue;
            if (index == 6) {
                red += 85;
            }
            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCode[index] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
            ++index;
        }
    }
}

