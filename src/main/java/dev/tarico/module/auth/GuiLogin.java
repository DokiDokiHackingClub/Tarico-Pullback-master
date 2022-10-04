package dev.tarico.module.auth;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.event.EventBus;
import dev.tarico.module.auth.client.ClientImpl;
import dev.tarico.module.auth.packet.client.CAuthPacket;
import dev.tarico.module.auth.packet.server.SAuthResultPacket;
import dev.tarico.module.gui.alt.GuiPasswordField;
import dev.tarico.utils.client.HWIDUtils;
import dev.tarico.utils.client.RenderUtil;
import dev.tarico.utils.client.SessionHelper;
import dev.tarico.utils.client.invcleaner.TimerUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class GuiLogin extends GuiScreen {
    public static String status;
    static boolean login = true;
    public static GuiTextField usernameField;
    public static GuiPasswordField passwordField;
    static TimerUtil timer = null;

    public GuiLogin() {
        // 防止开裂者IQ特别高甚至高于1，把这个数值改了
        login = false;
        try {
            ClientImpl.connect();
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(null, "服务器连接失败, 请联系售后", "L", JOptionPane.ERROR_MESSAGE);
            Runtime.getRuntime().exit(1);
        }
    }


    @Native
    public static boolean isLogin() {
        return login;
    }

    @Native
    public static void callback(SAuthResultPacket.Result result) throws IOException {
        login = true;
        status = EnumChatFormatting.GREEN + "Login Success. Please wait...";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(26, 26, 26).getRGB());


        drawString(mc.fontRendererObj, status, width / 2 - fontRendererObj.getStringWidth(SessionHelper.stripColorCodes(status)) / 2, height / 2 - 110, 0xAAAAAA);


        if (!login) {
            drawString(mc.fontRendererObj, "Username: ", width / 2 - 250 / 2, height / 2 - 220 + 135, -1);
            drawString(mc.fontRendererObj, "Password: ", width / 2 - 250 / 2, height / 2 - 220 + 135 + 43, -1);
            usernameField.drawTextBox();
            passwordField.drawTextBox();
        }

        if(timer != null){
            if(timer.hasTimeElapsed(5000)){
                status = EnumChatFormatting.RED + "Login Failed or Connection Lost!";
                timer.reset();
                timer = null;
            }
        }
        super.drawScreen(mouseX,mouseY,partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            if (!login)
                login();
        }
        if (button.id == 2)
            mc.displayGuiScreen(new GuiRegister(this));
        if (button.id == 3) {
            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable trans = null;
                trans = new StringSelection(HWIDUtils.getHWID());
                clipboard.setContents(trans, null);
                status = EnumChatFormatting.GREEN + "Copy HWID Success.";
            } catch (NoSuchAlgorithmException e) {
                status = EnumChatFormatting.RED + "Failed!";
            }
        }
        super.actionPerformed(button);
    }


    @Native
    private void login() {
        status = EnumChatFormatting.YELLOW + "Waiting...";

        try {
            ClientImpl.sendPacket(new CAuthPacket(usernameField.getText(), passwordField.getText(), HWIDUtils.getHWID()));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            status = EnumChatFormatting.RED + "Failed Get Failed!";
        }
        timer = new TimerUtil();
        timer.reset();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RETURN) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            login();
        } else if (keyCode == Keyboard.KEY_TAB) {
            if (usernameField.isFocused()) {
                usernameField.setFocused(false);
                passwordField.setFocused(true);
            } else if (passwordField.isFocused()) {
                passwordField.setFocused(false);
                usernameField.setFocused(true);
            }
        }

        usernameField.textboxKeyTyped(typedChar, keyCode);
        passwordField.textboxKeyTyped(typedChar, keyCode);

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        usernameField.mouseClicked(mouseX, mouseY, mouseButton);
        passwordField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void initGui() {
        usernameField = new GuiTextField(0, mc.fontRendererObj, width / 2 - 250 / 2, height / 2 - 220 + 150, 250, 20);
        passwordField = new GuiPasswordField(mc.fontRendererObj, width / 2 - 250 / 2, height / 2 - 220 + 150 + 42, 250, 20);

        usernameField.setMaxStringLength(64);
        passwordField.setMaxStringLength(64);
        GuiButton loginButton = new GuiButton(1, width / 2 - 250 / 2, height / 2 - 220 + 150 + 40 * 3 + 25, 250, 20, "LOGIN");
        GuiButton regButton = new GuiButton(2, 4, 4, 100, 20, "Register");
        GuiButton hwidButton = new GuiButton(3, 4, 26, 100, 20, "Copy HWID");
        buttonList.add(hwidButton);
        buttonList.add(loginButton);
        buttonList.add(regButton);
        status = "Not logged in.";
        EventBus.getInstance().register(this);
        super.initGui();
    }
}
