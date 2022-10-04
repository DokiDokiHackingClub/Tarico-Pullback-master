package dev.tarico.module.modules.render;

import dev.tarico.management.CapeManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class Capes extends Module {
    public LayerRenderer<AbstractClientPlayer> cape;

    public Capes() {
        super("Capes", "Renderer Capes", ModuleType.Render);
    }

    boolean addedLayer = false;

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre e) {
        if (!addedLayer) {
            UUID playerUuid = e.entityPlayer.getUniqueID();
            if (playerUuid == Minecraft.getMinecraft().thePlayer.getUniqueID()) {
                cape = new CapeManager(e.renderer);
                e.renderer.addLayer(cape);
            }
            addedLayer = true;
        }
    }
}
