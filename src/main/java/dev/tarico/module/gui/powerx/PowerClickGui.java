package dev.tarico.module.gui.powerx;

import by.radioegor146.nativeobfuscator.Native;
import dev.tarico.Client;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.gui.AbstractClickGui;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.modules.render.HUD;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.module.value.Value;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings(value = {"rawtypes", "unchecked"})
public class PowerClickGui extends AbstractClickGui {
   public static boolean binding = false;
   public static Module currentMod = null;
   private final PowerButton handlerMid;
   private final PowerButton handlerRight;
   private final PowerButton handler;
   public int moveX;
   public int moveY;
   public int startX;
   public int startY;
   public int selectCategory;
   public Module bmod;
   public boolean dragging;
   public boolean drag;
   public boolean Mdrag;
   ArrayList<Module> mods;
   ScaledResolution res;
   Value<?> value;
   ScaledResolution sr;
   private float scrollY;
   private float modscrollY;

   public PowerClickGui() {
      this.mods = new ArrayList<>(ModuleManager.instance.getopenValues());
      this.handlerMid = new PowerButton(2);
      this.handlerRight = new PowerButton(1);
      this.handler = new PowerButton(0);
      this.res = new ScaledResolution(Minecraft.getMinecraft());
      this.moveX = 0;
      this.moveY = 0;
      this.startX = 50;
      this.startY = 40;
      this.selectCategory = 0;
      this.sr = new ScaledResolution(Minecraft.getMinecraft());
   }

   public static void erase(boolean stencil) {
      GL11.glStencilFunc(stencil ? 514 : 517, 1, '\uffff');
      GL11.glStencilOp(7680, 7680, 7681);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.enableAlpha();
      GlStateManager.enableBlend();
      GL11.glAlphaFunc(516, 0.0F);
   }

   public static List getValueList(Module module) {

      return module.getValues();
   }

   public static List<Module> getModsInCategory(ModuleType cat) {
      ArrayList<Module> list = new ArrayList<>();

      for (Module m : ModuleManager.instance.getModules()) {
         if (m.getCategory() == cat) {
            list.add(m);
         }
      }

      return list;
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (binding) {
         if (keyCode != 1 && keyCode != 211) {
            this.bmod.setKey(keyCode);
            Client.instance.configLoader.saveSetting();
         } else if (keyCode == 211) {
            this.bmod.setKey(0);
            Client.instance.configLoader.saveSetting();
         }

         binding = false;
      }

      super.keyTyped(typedChar, keyCode);
   }

   @Override
   public void mouseRelease(int mouseX, int mouseY, int state) {
      if (this.dragging) {
         this.dragging = false;
      }

      if (this.drag) {
         this.drag = false;
      }

      super.mouseRelease(mouseX, mouseY, state);
   }

   @Native
   @Override
   public void drawScr(int mouseX, int mouseY, float partialTicks) {
      this.sr = new ScaledResolution(Minecraft.getMinecraft());
      if (this.isHovered((float) this.startX, (float) (this.startY - 8), (float) (this.startX + 300), (float) (this.startY + 5), mouseX, mouseY) && !this.isHovered((float) (this.startX + 289), (float) (this.startY - 8), (float) (this.startX + 296), (float) (this.startY), mouseX, mouseY) && this.handler.canExcecute()) {
         this.dragging = true;
      }

      if (this.dragging) {
         if (this.moveX == 0 && this.moveY == 0) {
            this.moveX = mouseX - this.startX;
            this.moveY = mouseY - this.startY;
         } else {
            this.startX = mouseX - this.moveX;
            this.startY = mouseY - this.moveY;
         }
      } else if (this.moveX != 0 || this.moveY != 0) {
         this.moveX = 0;
         this.moveY = 0;
      }

      if ((float) this.startX > (float) (this.sr.getScaledWidth() - 303)) {
         this.startX = this.sr.getScaledWidth() - 303;
      }

      if (this.startX < 3) {
         this.startX = 3;
      }

      if ((float) this.startY > (float) (this.sr.getScaledHeight() - 190)) {
         this.startY = this.sr.getScaledHeight() - 190;
      }

      if ((float) this.startY < 12.0F) {
         this.startY = 12;
      }

      GL11.glPushMatrix();
      erase(false);
      RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/menu.png"), this.startX - 10, this.startY - 18, 320, 216, new Color(255, 255, 255));
      RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/panelright.png"), this.startX + 59, this.startY + 5, 9, 182);
      RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/panelright.png"), this.startX + 59, this.startY + 5, 9, 182);
      RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/panelright.png"), this.startX + 59, this.startY + 5, 9, 182);
      GL11.glEnable(3089);
      RenderUtil.doGlScissor1((float) this.startX, (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 185));
      int y = 0;
      ModuleType[] moduleTypes = ModuleType.values();
      int length = moduleTypes.length;

      for (int i = 0; i < length; ++i) {
         ModuleType moduleType = ModuleType.values()[i];
         String str = moduleType.name().replaceAll("Movement", "Move").replaceAll("MiniGames", "GAMES");
         RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/" + moduleType.name().toUpperCase() + ".png"), this.startX + 4, this.startY + 16 + y, 12, 12, this.selectCategory == i ? new Color(43, 110, 141) : new Color(170, 170, 170));
         FontLoaders.F18.drawCenteredString(str.charAt(0) + str.toLowerCase().substring(1, str.length()), (float) (this.startX + 36), (float) (this.startY + 18 + y), this.selectCategory == i ? (new Color(0, 170, 255)).getRGB() : (new Color(170, 170, 170)).getRGB());
         if (this.isHovered((float) (this.startX + 3), (float) (this.startY + 14 + y), (float) (this.startX + 50), (float) (this.startY + 32 + y), mouseX, mouseY) && this.handler.canExcecute()) {
            this.selectCategory = i;
         }

         y += 25;
      }

      int buttonX = this.startX + 64;
      int buttonY = this.startY + 12;

      int modulePos = this.startY + 8;


      for (int i = 0; i < getModsInCategory(ModuleType.values()[this.selectCategory]).size(); ++i) {
         Module mod = getModsInCategory(ModuleType.values()[this.selectCategory]).get(i);
         if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 185), mouseX, mouseY) && getModsInCategory(ModuleType.values()[this.selectCategory]).size() > 11 && this.isHovered((float) buttonX, (float) (modulePos - 2), (float) (buttonX + 82), (float) (modulePos + 12), mouseX, mouseY)) {
            float wheel = (float) Mouse.getDWheel();
            this.modscrollY += wheel / 10.0F;
         }

         if (getModsInCategory(ModuleType.values()[this.selectCategory]).size() < 12) {
            this.modscrollY = 0.0F;
         }

         if ((double) this.modscrollY > 0.0D) {
            this.modscrollY = 0.0F;
         }

         if (getModsInCategory(ModuleType.values()[this.selectCategory]).size() > 11 && this.modscrollY < (float) ((getModsInCategory(ModuleType.values()[this.selectCategory]).size() - 11) * -16)) {
            this.modscrollY = (float) ((getModsInCategory(ModuleType.values()[this.selectCategory]).size() - 11) * -16);
         }

         if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) buttonX, (float) (modulePos - 2) + this.modscrollY, (float) (buttonX + 82), (float) (modulePos + 12) + this.modscrollY, mouseX, mouseY)) {
            RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/MOD.png"), buttonX, (int) ((float) (modulePos - 2) + this.modscrollY), 82, 14, new Color(180, 180, 180, 80));
         } else {
            RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/MOD.png"), buttonX, (int) ((float) (modulePos - 2) + this.modscrollY), 82, 14, mod.getState() ? new Color(60, 60, 60) : new Color(40, 40, 40));
         }

         RenderUtil.circle((float) (buttonX + 8), (float) (modulePos + 5) + this.modscrollY, 1.5F, mod.getState() ? (new Color(0, 124, 255)).getRGB() : (new Color(153, 153, 153)).getRGB());
         FontLoaders.F18.drawCenteredString(binding ? (mod == this.bmod ? "Binding Key" : mod.getName()) : mod.getName(), (float) (buttonX + 40), (float) (modulePos + 1) + this.modscrollY, mod.getState() ? (new Color(220, 220, 220)).getRGB() : (new Color(90, 90, 90)).getRGB());
         FontLoaders.F18.drawCenteredString(!mod.getValues().isEmpty() ? (mod.openValues ? "-" : "+") : "", (float) (buttonX + 76), (float) (modulePos + 1) + this.modscrollY, (new Color(153, 153, 153)).getRGB());
         if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) buttonX, (float) (modulePos - 2) + this.modscrollY, (float) (buttonX + 82), (float) (modulePos + 12) + this.modscrollY, mouseX, mouseY) && this.handlerMid.canExcecute()) {
            binding = true;
            this.bmod = mod;
         }

         if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) buttonX, (float) (modulePos - 2) + this.modscrollY, (float) (buttonX + 82), (float) (modulePos + 12) + this.modscrollY, mouseX, mouseY) && this.handler.canExcecute()) {
            mod.setState(!mod.getState());
         }

         if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) buttonX, (float) (modulePos - 2) + this.modscrollY, (float) (buttonX + 82), (float) (modulePos + 12) + this.modscrollY, mouseX, mouseY) && this.handlerRight.canExcecute() && !mod.openValues && !mod.getValues().isEmpty()) {
            mod.openValues = !mod.openValues;
            currentMod = mod;
            this.scrollY = 0.0F;

            for (Module m : ModuleManager.instance.getModules()) {
               if (m.openValues && !Objects.equals(m.getName(), mod.getName())) {
                  m.openValues = false;
               }
            }
            Client.instance.configLoader.saveSetting();
         }

         if (mod.openValues) {
            for (Value value : mod.getValues()) {
               if (value instanceof ModeValue) {
                  String name = ((ModeValue<?>) value).getModeAsString();
                  RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/Mode_Boolean_Left.png"), buttonX + 144, (int) ((float) buttonY + this.scrollY - 2.0F), 10, 10);
                  RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/Mode_bg.png"), buttonX + 154, (int) ((float) buttonY + this.scrollY - 4.0F), 54, 14);
                  RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/Mode_Boolean_Right.png"), buttonX + 208, (int) ((float) buttonY + this.scrollY - 2.0F), 10, 10);
                  FontLoaders.F14.drawString(name, (float) (buttonX + 180 - FontLoaders.F14.getStringWidth("" + name) / 2), (float) buttonY + this.scrollY - 1.0F, (new Color(200, 200, 200)).getRGB());
                  FontLoaders.F14.drawString(value.getName(), (float) (buttonX + 90), (float) buttonY + this.scrollY - 1.0F, (new Color(153, 153, 169)).getRGB());
                  if (this.isHovered((float) (this.startX + 151), (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) (buttonX + 144), (float) buttonY + this.scrollY - 1.0F, (float) (buttonX + 153), (float) (buttonY + 7) + this.scrollY, mouseX, mouseY) && this.handler.canExcecute()) {
                     final ModeValue m = (ModeValue) value;
                     final Enum<?> current = (Enum<?>) m.getValue();
                     final int next = (current.ordinal() - 1 <= 0) ? m.getModes().length - 1 : (current.ordinal() - 1);
                     value.setValue(m.getModes()[next]);
                  }

                  if (this.isHovered((float) (this.startX + 151), (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) (buttonX + 208), (float) buttonY + this.scrollY - 1.0F, (float) (buttonX + 217), (float) (buttonY + 7) + this.scrollY, mouseX, mouseY) && this.handler.canExcecute()) {
                     final ModeValue m = (ModeValue) value;
                     final Enum<?> current = (Enum<?>) m.getValue();
                     final int next = (current.ordinal() + 1 >= m.getModes().length) ? 0 : (current.ordinal() + 1);
                     value.setValue(m.getModes()[next]);
                  }

                  buttonY += 18;
               }
            }

            for (Value number : mod.getValues()) {
               if (number instanceof NumberValue) {
                  this.width = 100;
                  double num = ((Double) number.getValue() - ((NumberValue<?>) number).getMinimum().doubleValue()) / (((NumberValue<?>) number).getMaximum().doubleValue() - ((NumberValue<?>) number).getMinimum().doubleValue());
                  double maxX = mouseX - (buttonX + 145);
                  double minPos = maxX / 83.0D;
                  minPos = Math.min(Math.max(0.0D, minPos), 1.0D);
                  double max = (((NumberValue<?>) number).getMaximum().doubleValue() - ((NumberValue<?>) number).getMinimum().doubleValue()) * minPos;
                  double min = ((NumberValue<?>) number).getMinimum().doubleValue() + max;
                  double slider = (double) (buttonX + 145) + 83.0D * num;
                  RenderUtil.drawRect((float) (buttonX + 145), (float) (buttonY + 4) + this.scrollY, (float) (buttonX + 230), (float) (buttonY + 6) + this.scrollY, (new Color(53, 54, 53)).getRGB());
                  RenderUtil.drawRect((double) buttonX + 145.5D, (double) buttonY + 4.5D + (double) this.scrollY, slider + 2.0D, (double) buttonY + 5.5D + (double) this.scrollY, (new Color(0, 100, 242)).getRGB());
                  RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/Slider.png"), (int) slider - 1, (int) ((float) (buttonY + 3) + this.scrollY), 4, 4, new Color(0, 100, 242));
                  FontLoaders.F14.drawString("" + number.getValue(), (float) (buttonX + 229 - FontLoaders.F14.getStringWidth("" + number.getValue())), (float) (buttonY - 4) + this.scrollY, (new Color(153, 153, 169)).getRGB());
                  FontLoaders.F14.drawString(number.getName(), (float) (buttonX + 90), (float) buttonY + this.scrollY, (new Color(153, 153, 169)).getRGB());
                  if (this.isHovered((float) (this.startX + 151), (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 185), mouseX, mouseY) && this.isHovered((float) (buttonX + 145), (float) (buttonY + 1) + this.scrollY, (float) (buttonX + 230), (float) (buttonY + 7) + this.scrollY, mouseX, mouseY) && this.handler.canExcecute()) {
                     this.value = number;
                     this.drag = true;
                  }

                  if (this.drag && number == this.value) {
                     min = (double) Math.round(min * (1.0D / ((NumberValue<?>) number).getIncrement().doubleValue())) / (1.0D / ((NumberValue<?>) number).getIncrement().doubleValue());
                     number.setValue(min);
                     Client.instance.configLoader.saveSetting();
                  } else {
                     min = (double) Math.round((Double) number.getValue() * (1.0D / ((NumberValue<?>) number).getIncrement().doubleValue())) / (1.0D / ((NumberValue<?>) number).getIncrement().doubleValue());
                     number.setValue(min);
                  }

                  buttonY += 18;
               }
            }

            for (Value booleanValue : mod.getValues()) {
               if (booleanValue instanceof BooleanValue) {
                  RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/ValueBoolean_bg.png"), buttonX + 214, (int) ((float) buttonY + this.scrollY - 1.0F), 16, 8, new Color(255, 255, 255));
                  if ((Boolean) booleanValue.getValue()) {
                     RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/ValueBoolean_button.png"), buttonX + 222, (int) ((float) buttonY + this.scrollY - 1.0F), 8, 8, new Color(0, 125, 255));
                  } else {
                     RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/ValueBoolean_button.png"), buttonX + 214, (int) ((float) buttonY + this.scrollY - 1.0F), 8, 8, new Color(153, 153, 153));
                  }

                  if (this.isHovered((float) (this.startX + 151), (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 185), mouseX, mouseY) && this.isHovered((float) (buttonX + 214), (float) buttonY + this.scrollY - 1.0F, (float) (buttonX + 230), (float) (buttonY + 7) + this.scrollY, mouseX, mouseY) && this.handler.canExcecute()) {
                     booleanValue.setValue(!(Boolean) booleanValue.getValue());
                     Client.instance.configLoader.saveSetting();
                  }

                  FontLoaders.F14.drawString(booleanValue.getName(), (float) (buttonX + 90), (float) buttonY + this.scrollY, (new Color(153, 153, 169)).getRGB());
                  buttonY += 18;
               }
            }

            if (getValueList(mod).size() > 10 && buttonY > this.startY + 185 && this.isHovered((float) (this.startX + 151), (float) (this.startY - 8), (float) (this.startX + 300), (float) (this.startY + 185), mouseX, mouseY)) {
               float wheel = (float) Mouse.getDWheel();
               this.scrollY += wheel / 10.0F;
            }

            if ((double) this.scrollY > 0.0D) {
               this.scrollY = 0.0F;
            }

            if (getValueList(mod).size() > 10 && this.scrollY < (float) ((getValueList(mod).size() - 10) * -18)) {
               this.scrollY = (float) ((getValueList(mod).size() - 10) * -18);
            }
         }

         modulePos += 16;
      }

      GL11.glDisable(3089);
      if (this.isHovered((float) (this.startX + 289), (float) (this.startY - 8), (float) (this.startX + 296), (float) (this.startY), mouseX, mouseY)) {
         RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/open.png"), this.startX + 288, this.startY - 8, 10, 10, new Color(255, 0, 0));
         if (this.handler.canExcecute()) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
         }
      } else {
         RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/open.png"), this.startX + 288, this.startY - 8, 10, 10, new Color(0, 125, 255));
      }

       FontLoaders.F18.drawCenteredString(HUD.wm, (float) (this.startX + 28), (float) (this.startY - 6), (new Color(170, 170, 170)).getRGB());
       FontLoaders.F14.drawCenteredString(ModuleType.values()[this.selectCategory].name(), (float) (this.startX + 80), (float) (this.startY - 6), (new Color(153, 153, 159)).getRGB());
      RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/panelbottom.png"), this.startX, this.startY + 5, 301, 9);
      RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/panelright.png"), this.startX + 150, this.startY + 5, 9, 180);
      RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/panelleft.png"), this.startX + 141, this.startY + 5, 9, 180);
      RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/panelleft.png"), this.startX + 292, this.startY + 5, 9, 180);
      RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/panelright.png"), this.startX, this.startY + 5, 9, 180);
      RenderUtil.drawImage(new ResourceLocation("minecraft:client/clickgui/paneltop.png"), this.startX, this.startY + 179, 301, 9);
      GL11.glPopMatrix();
   }

   public boolean isHovered(float x, float y, float width, float height, int mouseX, int mouseY) {
      return (float) mouseX >= x && (float) mouseX <= width && (float) mouseY >= y && (float) mouseY <= height;
   }

   public void onGuiClosed() {
      if (this.mc.entityRenderer.getShaderGroup() != null) {
         this.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
      }

      this.dragging = false;
      this.drag = false;
      this.Mdrag = false;
      super.onGuiClosed();
   }
}
