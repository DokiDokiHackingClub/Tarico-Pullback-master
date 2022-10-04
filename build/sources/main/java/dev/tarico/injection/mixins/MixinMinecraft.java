package dev.tarico.injection.mixins;

import dev.tarico.Client;
import dev.tarico.event.EventBus;
import dev.tarico.event.events.misc.EventKey;
import dev.tarico.event.events.world.EventTick;
import dev.tarico.module.auth.GuiLogin;
import dev.tarico.module.auth.GuiRegister;
import dev.tarico.module.gui.mainmenu.GuiCustomMainMenu;
import dev.tarico.module.gui.other.GuiScare;
import dev.tarico.utils.render.SoundFxPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public abstract class MixinMinecraft {
    @Shadow
    public GuiScreen currentScreen;

    @Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
    private void startGame(CallbackInfo ci) {
        Display.setTitle("Tarico | " + Display.getTitle());
        Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void onKey(CallbackInfo ci) {
        if (Keyboard.getEventKeyState() && currentScreen == null)
            EventBus.getInstance().call(new EventKey(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()));
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    private void runTick(CallbackInfo ci) {
        EventBus.getInstance().call(new EventTick());

/*        if(Client.scare && !(Minecraft.getMinecraft().currentScreen instanceof GuiScare) && !(Minecraft.getMinecraft().currentScreen instanceof GuiDisconnected)){
            Minecraft.getMinecraft().displayGuiScreen(new GuiDisconnected(new GuiScare(),
                    "connect.failed", new ChatComponentText(EnumChatFormatting.DARK_RED + "Congratulations on coming here. Are you ready to receive our gifts?")));
            new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.WARNING,1);
        }*/
//        if(GuiLogin.isLogin() && Client.instance == null){
//            Client.call = true;
//            new Client(GuiLogin.usernameField.getText());
//        }
//        if (Client.instance != null && GuiLogin.isLogin()) {
//            if (Client.instance.user == null)
//                Client.exit();
            if (this.currentScreen instanceof GuiMainMenu) {
                Client.user = "HI";
                Minecraft.getMinecraft().displayGuiScreen(new GuiCustomMainMenu(20));
            }
//        } else if (!(Minecraft.getMinecraft().currentScreen instanceof GuiLogin) && !(Minecraft.getMinecraft().currentScreen instanceof GuiRegister)) {
//            Minecraft.getMinecraft().displayGuiScreen(new GuiLogin());
//        }
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onShutdown(CallbackInfo ci) {
        Client.instance.stop();
    }
}