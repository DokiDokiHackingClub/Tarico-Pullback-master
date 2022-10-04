package dev.tarico.module.modules.render;

import dev.tarico.management.FriendManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.modules.combat.AntiBot;
import dev.tarico.module.modules.combat.Teams;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.render.Colors;
import dev.tarico.utils.render.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class Tracers extends Module {
    private final NumberValue<Number> line = new NumberValue<>("line", 0.5D, 0.1D, 5.0, 0.1D);
    private final BooleanValue<Boolean> invisible = new BooleanValue<>("Invisible", false);
    private boolean states;

    public Tracers() {
        super("Tracers", "Draw Tracers for players", ModuleType.Render);
    }

    @Override
    public void enable() {
        this.states = Tracers.mc.gameSettings.viewBobbing;
        if (this.states) {
            Tracers.mc.gameSettings.viewBobbing = false;
        }
    }

    @SubscribeEvent
    public void update(TickEvent.PlayerTickEvent event) {
        if (Tracers.mc.gameSettings.viewBobbing) {
            Tracers.mc.gameSettings.viewBobbing = false;
        }
    }

    @Override
    public void disable() {
        Tracers.mc.gameSettings.viewBobbing = this.states;
    }


    @SubscribeEvent
    public void onRender(final RenderWorldLastEvent ev) {
        for (final EntityPlayer TargetEntity : Tracers.mc.theWorld.playerEntities) {
            if (TargetEntity != Tracers.mc.thePlayer && !AntiBot.isServerBot(TargetEntity)) {
                if (!this.invisible.getValue() && TargetEntity.isInvisible()) {
                    return;
                }
                int rgb;
                if (Teams.isOnSameTeam(TargetEntity)) {
                    rgb = new Color(255, 255, 255).getRGB();
                } else if (FriendManager.isFriend(TargetEntity.getName())) {
                    rgb = new Color(34, 255, 0, 255).getRGB();
                } else {
                    rgb = Colors.RED.getColor().getRGB();
                }
                RenderHelper.drawTracers(TargetEntity, rgb, this.line.getValue().floatValue());
            }
        }
    }

}
