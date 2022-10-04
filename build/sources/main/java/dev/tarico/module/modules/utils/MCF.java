package dev.tarico.module.modules.utils;

import dev.tarico.event.EventTarget;
import dev.tarico.event.events.world.EventPreUpdate;
import dev.tarico.management.FriendManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;


public class MCF extends Module {
    private boolean down;

    public MCF() {
        super("MCF", "Middle Click Friends", ModuleType.Utils);
    }

    @EventTarget
    @SuppressWarnings("unused")
    private void onClick(EventPreUpdate e) {
        if (Mouse.isButtonDown(2) && !this.down) {
            if (mc.objectMouseOver.entityHit != null) {
                EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;
                String playername = player.getName();
                if (!FriendManager.isFriend(playername)) {
                    mc.thePlayer.sendChatMessage(".f add " + playername);
                } else {
                    mc.thePlayer.sendChatMessage(".f del " + playername);
                }
            }
            this.down = true;
        }
        if (!Mouse.isButtonDown(2)) {
            this.down = false;
        }
    }
}
