package dev.tarico.module.modules.render;

import dev.tarico.Client;
import dev.tarico.event.EventBus;
import dev.tarico.event.EventTarget;
import dev.tarico.event.events.rendering.EventBlur;
import dev.tarico.event.events.rendering.EventRender2D;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.gui.font.CFontRenderer;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.hud.HUDManager;
import dev.tarico.module.gui.hud.HUDObject;
import dev.tarico.module.gui.notification.NotificationsManager;
import dev.tarico.module.gui.other.TabGui;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.anim.AnimationUtils;
import dev.tarico.utils.client.RenderUtil;
import dev.tarico.utils.client.SessionHelper;
import dev.tarico.utils.render.ColorUtils;
import dev.tarico.utils.render.Colors;
import dev.tarico.utils.render.Palette;
import dev.tarico.utils.render.blur.GaussianBlur;
import dev.tarico.utils.render.blur.StencilUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HUD extends Module {
    public final static ModeValue<Enum<WM>> wmmode = new ModeValue<>("Watermark", WM.values(), WM.OneTap);
    public final static ModeValue<Enum<TabGUIS>> tab = new ModeValue<>("TabGui", TabGUIS.values(), TabGUIS.Default);
    public final static ModeValue<Enum<HUDModes>> mode = new ModeValue<>("HUDMode", HUDModes.values(), HUDModes.Tarico);
    public final static NumberValue<Double> radius = new NumberValue<>("Radius", 10.0, 1.0, 100.0, 0.01);
    public final static NumberValue<Double> alpha = new NumberValue<>("Alpha", 50.0, 0.0, 255.0, 0.01);
    public final static NumberValue<Double> hudalpha = new NumberValue<>("HUDAlpha", 50.0, 0.0, 255.0, 0.01);
    public final static NumberValue<Double> invalpha = new NumberValue<>("Inv Alpha", 50.0, 0.0, 255.0, 0.01);
    public final static BooleanValue<Boolean> blurShader = new BooleanValue<>("Blur", false);
    public final static BooleanValue<Boolean> tabgui = new BooleanValue<>("TabGUI", true);
    public static ModeValue<Enum<Colors>> mainc = new ModeValue<>("MainColor", Colors.values(), Colors.INDIGO);
    public static ModeValue<Enum<ColorMode>> color = new ModeValue<>("Color", ColorMode.values(), ColorMode.Fade);
    private final NumberValue<Double> bright = new NumberValue<>("Bright", 1.0, 0.1, 1.5, 0.01);
    private final NumberValue<Double> sr = new NumberValue<>("Saturation", 1.0, 0.1, 1.5, 0.01);
    public static BooleanValue<Boolean> invback = new BooleanValue<>("Inv Background", false);
    public BooleanValue<Boolean> watermark = new BooleanValue<>("Watermark", true);

    public BooleanValue<Boolean> shadow = new BooleanValue<>("Shadow", true);
    public static final BooleanValue<Boolean> textGui = new BooleanValue<>("TextGui", true);
    public static final BooleanValue<Boolean> hshadow = new BooleanValue<>("HUDShadow", true);
    public BooleanValue<Boolean> sidebar = new BooleanValue<>("SideBar", true);
    public BooleanValue<Boolean> back = new BooleanValue<>("Background", true);
    public BooleanValue<Boolean> noti = new BooleanValue<>("Notifications", true);
    public BooleanValue<Boolean> info = new BooleanValue<>("Info", true);

    public float hue = 0;
    public float hue_2 = 0;

    public static void blur() {
        if (!blurShader.getValue())
            return;
        StencilUtil.initStencilToWrite();
        EventBus.getInstance().call(new EventBlur());
        StencilUtil.readStencilBuffer(1);
        GaussianBlur.renderBlur(HUD.radius.getValue().floatValue());
        StencilUtil.uninitStencilBuffer();
    }


    @EventTarget
    @SuppressWarnings("unused")
    public void onR2D(EventRender2D e) {
        Client.instance.mainColor = (Colors) mainc.getValue();

        for (HUDObject hud : HUDManager.hudObjects)
            hud.draw(e.getPartialTicks());

        if (noti.getValue()) {
            NotificationsManager.renderNotifications();
            NotificationsManager.update();
        }

        if ((hue_2 += 0.9f / 5.0f) > 255.0f) {
            hue_2 = 0.0f;
        }
        float h1 = hue;

        if (info.getValue()) {
            String pos = EnumChatFormatting.GRAY + "X" + EnumChatFormatting.WHITE + ": " + MathHelper.floor_double(mc.thePlayer.posX) + " " + EnumChatFormatting.GRAY + "Y" + EnumChatFormatting.WHITE + ": " + MathHelper.floor_double(mc.thePlayer.posY) + " " + EnumChatFormatting.GRAY + "Z" + EnumChatFormatting.WHITE + ": " + MathHelper.floor_double(mc.thePlayer.posZ);
            String text = String.format(EnumChatFormatting.GRAY + "Build - %s - User - " + EnumChatFormatting.GREEN + " %s", Client.instance.version, Client.instance.user);
            FontLoaders.F18.drawStringWithShadow(text, new ScaledResolution(mc).getScaledWidth() - FontLoaders.F18.getStringWidth(text) - 1, new ScaledResolution(mc).getScaledHeight() - FontLoaders.F18.getHeight() - 1, -1);
            FontLoaders.F18.drawStringWithShadow(pos, 2, new ScaledResolution(mc).getScaledHeight() - FontLoaders.F18.getHeight() - 1, -1);
            FontLoaders.F18.drawStringWithShadow("Speed: " + Client.calculateBPS(), 2, new ScaledResolution(mc).getScaledHeight() - FontLoaders.F18.getHeight() - 1 - FontLoaders.F18.getHeight() - 1, -1);
        }

        if (mode.getValue() == HUDModes.Tarico) {
            if (watermark.getValue()) {
                if (wmmode.getValue() == WM.OneTap) {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String text = String.format("%s | %s | %sFPS | %sKills | %sms | %s", wm, Client.instance.user, Minecraft.getDebugFPS(), SessionHelper.SessionState.kills, mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime(), sdf.format(date));
                    RenderUtil.drawBlurRect(2, 2, FontLoaders.F18.getStringWidth(text) + 4, FontLoaders.F18.getHeight() + 4, new Color(0, 0, 0, 144).getRGB());
                    RenderUtil.drawRect(2, 1, 2 + FontLoaders.F18.getStringWidth(text) + 4, 2, Client.instance.mainColor.getColor().getRGB());
                    RenderUtil.drawShadow(2, 2, FontLoaders.F18.getStringWidth(text) + 2, FontLoaders.F18.getHeight() + 2);
                    FontLoaders.F18.drawString(text, 4, 4, -1);
                } else if (wmmode.getValue() == WM.Text) {
                    FontLoaders.F20.drawString(wm, 4, 4, -1);
                } else if (wmmode.getValue() == WM.GameSense) {
                    GL11.glPushMatrix();
                    NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());

                    if (this.hue > 255.0F) {
                        this.hue = 0.0F;
                    }

                    float h = this.hue;
                    float h2 = this.hue + 85.0F;
                    float h3 = this.hue + 170.0F;

                    if (h2 > 255.0F) {
                        h2 -= 255.0F;
                    }

                    if (h3 > 255.0F) {
                        h3 -= 255.0F;
                    }

                    Color a = Color.getHSBColor(h / 255.0F, 0.4F, 1.0F);
                    Color b = Color.getHSBColor(h2 / 255.0F, 0.4F, 1.0F);
                    Color c = Color.getHSBColor(h3 / 255.0F, 0.4F, 1.0F);
                    int color1 = a.getRGB();
                    int color2 = b.getRGB();
                    int color3 = c.getRGB();
                    this.hue = this.hue + 0.05f;
                    String ping;
                    ping = info != null ? info.getResponseTime() + "" : "0";

                    String str = wm.toLowerCase();
                    String str2 = "sense";
                    String str3 = " | " + Client.instance.user + " | " + (mc.isSingleplayer() ? "Singleplayer" : !mc.getCurrentServerData().serverIP.contains(":") ? mc.getCurrentServerData().serverIP + ":25565" : mc.getCurrentServerData().serverIP) + " | " + Minecraft.getDebugFPS() + "fps | " + ping + "ms";
                    RenderUtil.rectangle(5, 5, 14 + FontLoaders.F16.getStringWidth(str + str2 + str3), 20, ColorUtils.getColor(30));
                    RenderUtil.rectangleBordered(5, 5, 14 + FontLoaders.F16.getStringWidth(str + str2 + str3), 21, 0.5d, ColorUtils.getColor(0, 0), ColorUtils.getColor(10));
                    RenderUtil.rectangleBordered(5.5f, 5.5f, 13.5f + FontLoaders.F16.getStringWidth(str + str2 + str3), 20.5f, 0.5d, ColorUtils.getColor(0, 0), ColorUtils.getColor(100));
                    RenderUtil.rectangleBordered(6f, 6f, 13f + FontLoaders.F16.getStringWidth(str + str2 + str3), 20f, 1d, ColorUtils.getColor(0, 0), ColorUtils.getColor(60));
                    RenderUtil.rectangleBordered(7f, 7f, 12f + FontLoaders.F16.getStringWidth(str + str2 + str3), 19f, 0.5d, ColorUtils.getColor(0, 0), ColorUtils.getColor(100));
                    RenderUtil.drawGradientSideways(7.5f, 7.5f, 12f + (FontLoaders.F16.getStringWidth(str + str2 + str3) / 2), 8f, color1, color2);
                    RenderUtil.drawGradientSideways(12f + (FontLoaders.F16.getStringWidth(str + str2 + str3) / 2), 7.5f, 11.5f + FontLoaders.F16.getStringWidth(str + str2 + str3), 8f, color2, color3);
                    FontLoaders.F16.drawString(str, 10, (int) 11f, -1);
                    FontLoaders.F16.drawString(str2, 10 + FontLoaders.F16.getStringWidth(str), (int) 11f, new Color(0xFF00AA00).getRGB());
                    FontLoaders.F16.drawString(str3, 10 + FontLoaders.F16.getStringWidth(str + str2), (int) 11f, -1);
                    FontLoaders.F16.drawString(str, 10, (int) 11f, -1);
                    FontLoaders.F16.drawString(str2, 10 + FontLoaders.F16.getStringWidth(str), (int) 11f, new Color(0xFF00AA00).getRGB());
                    FontLoaders.F16.drawString(str3, 10 + FontLoaders.F16.getStringWidth(str + str2), (int) 11f, -1);
                    GL11.glPopMatrix();
                }
            }

            if (textGui.getValue()) {
                CFontRenderer font = FontLoaders.F18;
                ArrayList<Module> sorted = new ArrayList<Module>();
                for (Module m : ModuleManager.instance.getModules()) {
                    if ((!m.getState() && m.wasArrayRemoved() && m.getAnimx() == 0) || m.wasRemoved())
                        continue;
                    sorted.add(m);
                }
                sorted.sort((o1,
                             o2) -> font
                        .getStringWidth(o2.getSuffix().isEmpty() ? o2.getName()
                                : String.format("%s %s", o2.getName(), o2.getSuffix()))
                        - font.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName()
                        : String.format("%s %s", o1.getName(), o1.getSuffix())));
                int y = 1;
                int rainbowTick = 0;
                for (Module m : sorted) {
                    int nextIndex = sorted.indexOf(m) + 1;
                    name = m.getSuffix().isEmpty() ? m.getName() : String.format("%s %s", m.getName(), m.getSuffix());
                    Module nextModule = null;
                    if (sorted.size() > nextIndex) {
                        nextModule = this.getNextEnabledModule(sorted, nextIndex);
                    }

                    if (m.getState()) {
                        m.setArrayRemoved(false);
                        if (mc.thePlayer.ticksExisted >= 30) {
                            m.setAnimy(AnimationUtils.animation(12, 0, m.getAnimy(), 2 * (200d / Math.max(Minecraft.getDebugFPS(), 1)), AnimationUtils.AnimationTypes.slowDown));
                            m.setAnimx(AnimationUtils.animation(0, font.getStringWidth(name), m.getAnimx(), 10 * (200d / Math.max(Minecraft.getDebugFPS(), 1)), AnimationUtils.AnimationTypes.slowDown));
                        } else {
                            m.setAnimx(font.getStringWidth(name));
                        }
                    } else {
                        if (m.getAnimx() <= 0) {
                            m.setArrayRemoved(true);
                        } else {
                            if (mc.thePlayer.ticksExisted >= 30) {
                                m.setAnimy(AnimationUtils.animation(0, 12, m.getAnimy(), 2 * (200d / Math.max(Minecraft.getDebugFPS(), 1)), AnimationUtils.AnimationTypes.slowDown));
                                m.setAnimx(AnimationUtils.animation(font.getStringWidth(name), 0, m.getAnimx(), 10 * (200d / Math.max(Minecraft.getDebugFPS(), 1)), AnimationUtils.AnimationTypes.slowDown));
                            } else {
                                m.setAnimx(0);
                            }
                        }
                    }
                    double x = RenderUtil.width() - m.getAnimx();

                    Color rainbow3 = new Color(Color.getHSBColor(h1 / 255.0f, 0.4f, 0.86f).getRed(),
                            0, Color.getHSBColor(h1 / 255.0f, 0.6f, 0.87f).getBlue());

                    Color rainbow2 = new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) (sorted.indexOf(nextModule) * 2 + 53 + (y - 4) / 12) / 50.0 * 1.6)) % 1.0f, 0.5f, 1));

                    h1 += 0.3D / 5.0F;
                    if (h1 > 255.0F) {
                        h1 = 0.0F;
                    }

                    Color rainbow = new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) rainbowTick / 50.0 * 1.6)) % 1.0f, sr.getValue().floatValue(), bright.getValue().floatValue()));
                    int c = -1;
                    if (color.getValue() == ColorMode.Static) {
                        c = Client.instance.mainColor.getColor().getRGB();
                    } else if (color.getValue() == ColorMode.Fade) {
                        c = Palette.fade(Client.instance.mainColor.getColor(), 100, sorted.indexOf(m) * 2 + 10).getRGB();
                    } else if (color.getValue() == ColorMode.Rainbow) {
                        c = rainbow.getRGB();
                    } else if (color.getValue() == ColorMode.LightRainbow) {
                        c = new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f)).getRGB();
                    }

                    RenderUtil.drawRect(RenderUtil.width(), y + 11, x - 6f, y - 1,
                            new Color(0, 0, 0, alpha.getValue().intValue()).getRGB());
                    RenderUtil.drawRect(x - 10f, y + 11, x - 6f, y - 1, new Color(c).getRGB());

                    if(shadow.getValue()){
                        font.drawStringWithShadow(name, x - 3.0f, y + 2, new Color(c).getRGB());
                    }else{
                        font.drawString(name, (float) (x - 3), y + 2, new Color(c).getRGB());
                    }

                    if (++rainbowTick > 50) {
                        rainbowTick = 0;
                    }
                    y += 12 - m.getAnimy();
                    h1 += 9.0f;
                }
            }

        } else if (mode.getValue() == HUDModes.Evileye) {

            ScaledResolution s = new ScaledResolution(mc);
            int width = new ScaledResolution(mc).getScaledWidth();
            int height = new ScaledResolution(mc).getScaledHeight();
            int y = 1;
            mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.DARK_RED + "E" + EnumChatFormatting.WHITE + "vileye", 2, 2, -1);
            ArrayList<Module> enabledModules = new ArrayList<>();
            for (Module m : ModuleManager.instance.getModules()) {
                if (m.state) {
                    enabledModules.add(m);
                }
            }
            enabledModules.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2.getName()) - mc.fontRendererObj.getStringWidth(o1.getName()));
            for (Module m : enabledModules) {
                if (m != null && m.getState()) {
                    int moduleWidth = mc.fontRendererObj.getStringWidth(m.name);
                    mc.fontRendererObj.drawStringWithShadow(m.name, width - moduleWidth - 1, y, -1);
                    y += mc.fontRendererObj.FONT_HEIGHT;

                }
            }
        } else if (mode.getValue() == HUDModes.Vape) {
            int rainbowTick = 0;
            /*Color rainbow = new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
             */
            ScaledResolution s = new ScaledResolution(mc);
/*            RenderUtil.drawImage(new ResourceLocation("vape/logo.png"),s.getScaledWidth() - 63,0, 45,13,rainbow);
            RenderUtil.drawImage(new ResourceLocation("vape/v4.png"),s.getScaledWidth() - 19,0, 19,13,new Color(-1));*/
            int y = 14;
            ArrayList<Module> enabledModules = new ArrayList<>();
            for (Module m : ModuleManager.instance.getModules()) {
                if (m.state && m.inVape) {
                    enabledModules.add(m);
                }
            }
            enabledModules.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2.vapeSuffix == null ? o2.vapeName : o2.vapeName + o2.vapeSuffix) - mc.fontRendererObj.getStringWidth(o1.vapeSuffix == null ? o1.vapeName : o1.vapeName + o1.vapeSuffix));
            for (Module m : enabledModules) {
                if (m != null && m.getState()) {
                    String name = m.vapeSuffix == null ? m.vapeName : m.vapeName + m.vapeSuffix;
                    Color rainbow = new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) rainbowTick / 50.0 * 1.6)) % 1.0f, sr.getValue().floatValue(), bright.getValue().floatValue()));
                    int moduleWidth = mc.fontRendererObj.getStringWidth(name);
                    mc.fontRendererObj.drawStringWithShadow(name, s.getScaledWidth() - moduleWidth - 1, y, rainbow.getRGB());
                    y += mc.fontRendererObj.FONT_HEIGHT + 1;
                    if (++rainbowTick > 50) {
                        rainbowTick = 0;
                    }
                }
            }
        }else if (mode.getValue() == HUDModes.Sigma) {

            ScaledResolution s = new ScaledResolution(mc);
            int width = new ScaledResolution(mc).getScaledWidth();
            int height = new ScaledResolution(mc).getScaledHeight();
            int y = 1;
            FontLoaders.S33.drawString("Sigma",10,10,new Color(255,255,255,146).getRGB());
            ArrayList<Module> enabledModules = new ArrayList<>();
            for (Module m : ModuleManager.instance.getModules()) {
                if (m.state) {
                    enabledModules.add(m);
                }
            }
            enabledModules.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2.getName()) - FontLoaders.S18.getStringWidth(o1.getName()));
            for (Module m : enabledModules) {
                if (m != null && m.getState()) {
                    int moduleWidth = FontLoaders.S18.getStringWidth(m.name);
                    FontLoaders.S18.drawStringWithShadow(m.name, width - moduleWidth - 1, y, -1);
                    y += FontLoaders.S18.getHeight() + 3;

                }
            }

        }
        drawPotionStatus(new ScaledResolution(mc));
    }

    public enum TabGUIS {
        Default,
        Gradual
    }

    public static String wm = "Tarico";
    TabGui tabGui = new TabGui();

    public HUD() {
        super("HUD", "Head Up Display", ModuleType.Render);
        this.setState(true);
        tabGui.init();
        this.setRemoved(true);
    }

    @EventTarget
    @SuppressWarnings("unused")
    public void onRenderBlur(EventRender2D e) {
        if (blurShader.getValue())
            blurScreen();
    }

    private void blurScreen() {
        StencilUtil.initStencilToWrite();
        EventBus.getInstance().call(new EventBlur());
        StencilUtil.readStencilBuffer(1);

        GaussianBlur.renderBlur(HUD.radius.getValue().floatValue());
        StencilUtil.uninitStencilBuffer();
    }

    @Override
    public String getName() {
        return "HUD";
    }

/*
    @EventTarget
    @SuppressWarnings("unused")
    public void onBlur(EventBlur e) {
        RenderUtil.drawRect(30, 30, 50, 50, new Color(37, 37, 37, 108).getRGB());
    }
*/

    public enum WM {
        OneTap,
        Text,
        GameSense
    }

    public enum HUDModes {
        Tarico,
        Evileye,
        Vape,
        Sigma
    }

    private void drawPotionStatus(ScaledResolution sr) {
        int y = -FontLoaders.F18.getHeight();
        for (PotionEffect effect : mc.thePlayer.getActivePotionEffects()) {
            int ychat;
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            String PType = I18n.format(potion.getName());
            switch (effect.getAmplifier()) {
                case 1: {
                    PType = PType + " II";
                    break;
                }
                case 2: {
                    PType = PType + " III";
                    break;
                }
                case 3: {
                    PType = PType + " IV";
                    break;
                }
            }
            if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                PType = PType + "\u00a77:\u00a76 " + Potion.getDurationString(effect);
            } else if (effect.getDuration() < 300) {
                PType = PType + "\u00a77:\u00a7c " + Potion.getDurationString(effect);
            } else if (effect.getDuration() > 600) {
                PType = PType + "\u00a77:\u00a77 " + Potion.getDurationString(effect);
            }
            ychat = -10;
            mc.fontRendererObj.drawStringWithShadow(PType, sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(PType) - 2, sr.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT + y - 12 - ychat, potion.getLiquidColor());

            y -= 10;
        }
    }

    private Module getNextEnabledModule(List<Module> modules, int startingIndex) {
        int modulesSize = modules.size();
        for (int i = startingIndex; i < modulesSize; ++i) {
            Module module = modules.get(i);
            if (!module.getState())
                continue;
            return module;
        }
        return null;
    }

    enum ColorMode {
        Static,
        Rainbow,
        Fade,
        LightRainbow
    }
}
