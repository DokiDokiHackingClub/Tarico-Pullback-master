package dev.tarico.injection.mixins;

import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.utils.ClientSpoof;
import io.netty.buffer.Unpooled;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Redirect(method = "handleJoinGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void posY(NetworkManager instance, Packet packetIn) {
        if (ModuleManager.instance.getModule(ClientSpoof.class).getState()) {
            instance.sendPacket(ModuleManager.instance.getModule(ClientSpoof.class).getSpoofPacket());
        } else
            instance.sendPacket(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));
    }
}
