package dev.tarico.module.gui.hud.implement;

import dev.tarico.Client;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.gui.hud.HUDObject;
import dev.tarico.module.modules.render.HUD;
import dev.tarico.utils.client.RenderUtil;
import dev.tarico.utils.client.SessionHelper;
import net.minecraft.client.multiplayer.ServerData;

import java.awt.*;

public class SessionHUD extends HUDObject {
    public SessionHUD() {
        super(135, "Session Info");
    }

    @Override
    public void drawHUD(int x, int y, float p) {
        RenderUtil.drawShadow(x, y - 10, 135, 65 + 10);

        RenderUtil.drawBlurRect(x, y, 135, 65, new Color(0, 0, 0, HUD.hudalpha.getValue().intValue()).getRGB());
        FontLoaders.I20.drawString("t", x + 5, y + 5, 0xffffffff);
        FontLoaders.F16.drawString("Server: " + getRemoteIp(), x + 20, y + 4, 0xffffffff);
        FontLoaders.I20.drawString("G", x + 5, y + 21, 0xffffffff);
        FontLoaders.F16.drawString("Kills: " + SessionHelper.SessionState.kills, x + 20, y + 20, 0xffffffff);
        FontLoaders.I25.drawString("H", x + 4, y + 34, 0xffffffff);
        FontLoaders.F16.drawString("Win:" + SessionHelper.SessionState.wins, x + 20, y + 35, 0xffffffff);
        FontLoaders.I25.drawString("K", x + 4, y + 49, 0xffffffff);
        FontLoaders.F16.drawString("Username:  " + Client.instance.user, x + 20, y + 49.5f, 0xffffffff);
    }

    private String getRemoteIp() {
        String serverIp = "SinglePlayer";
        if (mc.theWorld.isRemote) {
            final ServerData serverData = mc.getCurrentServerData();
            if (serverData != null)
                serverIp = serverData.serverIP;
        }
        return serverIp;
    }
}
