package dev.tarico.module.auth;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.module.auth.client.ClientImpl;
import dev.tarico.module.auth.packet.client.CRegisterPacket;
import dev.tarico.module.auth.packet.server.SRegisterResultPacket;
import dev.tarico.utils.client.HWIDUtils;
import dev.tarico.utils.client.Helper;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class GuiRegister extends GuiScreen {
    public static String state = "Waiting...";
    private final GuiScreen previousScreen;
    private GuiTextField password;
    private GuiTextField username;
    private GuiTextField code;

    public GuiRegister(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Native
    public static void callback(SRegisterResultPacket result) {
        state = result.getResult();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1: {
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            }
            case 0: {
                if (username.getText().isEmpty() || password.getText().isEmpty() || code.getText().isEmpty()) {
                    state = "Some Field is empty!";
                } else {
                    state = "Register..";
                    try {
                        ClientImpl.sendPacket(new CRegisterPacket(username.getText(), password.getText(), HWIDUtils.getHWID(), code.getText()));
                    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public void drawScreen(int x, int y, float z) {
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(26, 26, 26).getRGB());
        this.username.drawTextBox();
        this.password.drawTextBox();
        this.code.drawTextBox();
        drawCenteredString(mc.fontRendererObj, "Register", width / 2, 20, -1);
        drawCenteredString(mc.fontRendererObj, state, width / 2, 29, -1);
        if (this.username.getText().isEmpty()) {
            Helper.mc.fontRendererObj.drawStringWithShadow("Username", width / 2 - 96, 66.0f, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            Helper.mc.fontRendererObj.drawStringWithShadow("Password", width / 2 - 96, 106.0f, -7829368);
        }
        if (this.code.getText().isEmpty()) {
            Helper.mc.fontRendererObj.drawStringWithShadow("Active code", width / 2 - 96, 146.0f, -7829368);
        }
        super.drawScreen(x, y, z);
    }

    @Override
    public void initGui() {
        int var3 = height / 4 + 24;
        this.buttonList.add(new GuiButton(0, width / 2 - 100, var3 + 72 + 12, "Register"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        this.username = new GuiTextField(1, mc.fontRendererObj, width / 2 - 100, 60, 200, 20);
        this.password = new GuiTextField(2, mc.fontRendererObj, width / 2 - 100, 100, 200, 20);
        this.code = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 140, 200, 20);
        this.username.setFocused(true);
        this.username.setMaxStringLength(200);
        this.password.setMaxStringLength(200);
        this.code.setMaxStringLength(200);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (username.isFocused()) {
                password.setFocused(true);
                username.setFocused(false);
            } else if (password.isFocused()) {
                code.setFocused(true);
                password.setFocused(false);
            } else if (code.isFocused()) {
                code.setFocused(false);
                username.setFocused(true);
            }
        }
        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
        this.code.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        try {
            super.mouseClicked(x, y, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(x, y, button);
        this.password.mouseClicked(x, y, button);
        this.code.mouseClicked(x, y, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
        this.code.updateCursorCounter();
    }
}
