package dev.tarico.injection.mixins;

import dev.tarico.event.Event;
import dev.tarico.event.EventBus;
import dev.tarico.event.events.world.EventPacketReceive;
import dev.tarico.event.events.world.EventPacketSend;
import dev.tarico.utils.client.PacketUtils;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = "channelRead0*", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;processPacket(Lnet/minecraft/network/INetHandler;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void packetReceived(ChannelHandlerContext p_channelRead0_1_, Packet<?> packet, CallbackInfo ci) {
        Event event = new EventPacketReceive(packet);
        EventBus.getInstance().call(event);

        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void sendPacket(Packet<?> packetIn, CallbackInfo ci) {
        if (!PacketUtils.packets.contains(packetIn)) {
            EventPacketSend event = new EventPacketSend(packetIn);
            EventBus.getInstance().call(event);
            if (event.isCancelled()) ci.cancel();
        }
    }


}
