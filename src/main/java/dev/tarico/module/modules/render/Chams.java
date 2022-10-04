package dev.tarico.module.modules.render;

import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Chams extends Module {
    public Chams() {
        super("Chams", "Allow your render play trough walls", ModuleType.Render);
        this.inVape = true;
    }

    @SubscribeEvent
    public void onRenderPlayer(final RenderPlayerEvent.Pre e) {
        GL11.glEnable(32823);
        GL11.glPolygonOffset(1.0f, -1100000.0f);
    }

    @SubscribeEvent
    public void onRenderPlayer(final RenderPlayerEvent.Post e) {
        GL11.glDisable(32823);
        GL11.glPolygonOffset(1.0f, 1100000.0f);
    }

}
