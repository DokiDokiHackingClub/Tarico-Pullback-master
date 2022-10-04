package dev.tarico.module.modules.render;

import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.utils.render.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemESP extends Module {
    public ItemESP() {
        super("ItemESP", "Render Items ESP", ModuleType.Render);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        for (Entity e : mc.theWorld.loadedEntityList) {
            if (!(e instanceof EntityItem))
                continue;
            RenderHelper.drawESP(e, -1, false, 3);
        }
    }
}
