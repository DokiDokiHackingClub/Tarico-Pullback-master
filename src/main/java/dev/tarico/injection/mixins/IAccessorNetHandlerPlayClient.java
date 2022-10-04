package dev.tarico.injection.mixins;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(NetHandlerPlayClient.class)
public interface IAccessorNetHandlerPlayClient {
    @Accessor("playerInfoMap")
    Map<UUID, NetworkPlayerInfo> getPlayerInfoMap();
}
