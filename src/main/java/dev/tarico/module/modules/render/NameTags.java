package dev.tarico.module.modules.render;

import dev.tarico.event.EventTarget;
import dev.tarico.management.FriendManager;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.gui.font.CFontRenderer;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.modules.combat.AntiBot;
import dev.tarico.module.modules.combat.Teams;
import dev.tarico.module.modules.fun.FarmHunt;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.utils.client.RenderUtil;
import dev.tarico.utils.math.MathUtils;
import dev.tarico.utils.render.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.http.util.TextUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NameTags extends Module {

	public BooleanValue<Boolean> armor = new BooleanValue<>("Armor", true);
	public BooleanValue<Boolean> font = new BooleanValue<>("Font",true);
	public BooleanValue<Boolean> scaleing = new BooleanValue<Boolean>("Scale", true);
	public BooleanValue<Boolean> player_only = new BooleanValue<>("Player Only", false);
	public ModeValue<Enum<Modes>> mode = new ModeValue<>("Mode",Modes.values(),Modes.Sigma);

	private NumberValue<Double> factor = new NumberValue<Double>("Size", 1.0, 0.1, 3.0, 0.1);

	public NameTags() {
		super("NameTags", "Render entitys nametags", ModuleType.Render);
	}

	enum Modes{
		Normal,
		Sigma
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {

		if(mode.getValue() == Modes.Normal){
			for(EntityPlayer player : mc.theWorld.playerEntities) {
				if(player != null && !player.equals(mc.thePlayer) && player.isEntityAlive() ) {
					double x = interpolate(player.lastTickPosX, player.posX, event.partialTicks) - mc.getRenderManager().viewerPosX;
					double y = interpolate(player.lastTickPosY, player.posY, event.partialTicks) - mc.getRenderManager().viewerPosY;
					double z = interpolate(player.lastTickPosZ, player.posZ, event.partialTicks) - mc.getRenderManager().viewerPosZ;
					renderNameTag(player, x, y, z, event.partialTicks);
				}
			}
		}else if(mode.getValue() == Modes.Sigma) {
			for (Object object : mc.theWorld.loadedEntityList) {
				if (object instanceof EntityPlayer || (!player_only.getValue() && object instanceof EntityLivingBase)) {
					if (mode.getValue() == Modes.Sigma) {
						if (((EntityLivingBase) object).isInvisible())
							return;
						if (ModuleManager.instance.getModule(FarmHunt.class).getState() && !FarmHunt.entityLivingBases.contains(object))
							return;
						EntityLivingBase entity = (EntityLivingBase) object;
						FontRenderer fontRenderer = mc.fontRendererObj;
						GL11.glPushMatrix();
						RenderManager renderManager = mc.getRenderManager();
						GL11.glTranslated(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks - renderManager.viewerPosX, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks - renderManager.viewerPosY + (double) entity.getEyeHeight() + 0.55, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks - renderManager.viewerPosZ);
						GL11.glRotatef(-renderManager.playerViewY, 0F, 1F, 0F);
						GL11.glRotatef(renderManager.playerViewX, 1F, 0F, 0F);
						float distance = mc.thePlayer.getDistanceToEntity(entity) / 4F;
						if (distance < 1F) {
							distance = 1F;
						}
						float scale = (distance / 150F) * 1.3F;
						RenderUtil.disableGlCap(GL11.GL_LIGHTING, GL11.GL_DEPTH_TEST);
						RenderUtil.enableGlCap(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						int hpBarColor = new Color(255, 255, 255, 170).getRGB();
						String name = entity.getDisplayName().getUnformattedText();
						String tag = entity.getName();
						if (name.startsWith("§")) {
							hpBarColor = ColorUtils.colorCode(name.substring(1, 2), 170);
						}
						if (entity instanceof EntityPlayer) {
							String ID = entity.getName();
							if (FriendManager.isFriend(ID)) {
								tag = tag + "§b[Friend]";
							}
							if (AntiBot.isServerBot(entity)) {
								tag = tag + "§e[ServerBot]";
							}
							if (Teams.isOnSameTeam(entity)) {
								tag = tag + "§a[Team]";
							}
						}
						int bgColor = new Color(50, 50, 50, 170).getRGB();
						int width = tag.length() <= 4 ? fontRenderer.getStringWidth("Czfsb") / 2 : fontRenderer.getStringWidth(tag) / 2;
						float maxWidth = (width + 4F) - (-width - 4F);
						float healthPercent = entity.getHealth() / entity.getMaxHealth();
						GL11.glScalef(-scale * 2, -scale * 2, scale * 2);
						if (font.getValue()) {
							RenderUtil.drawRect(-width - 4F, -FontLoaders.F18.getHeight() * 3F, width + 4F, 1, bgColor);
							RenderUtil.drawShadow3(-width - 4F, -FontLoaders.F18.getHeight() * 3F, width + 4F, 1);
						} else {
							RenderUtil.drawRect(-width - 4F, -fontRenderer.FONT_HEIGHT * 3F, width + 4F, 1, bgColor);
							RenderUtil.drawShadow3(-width - 4F, -fontRenderer.FONT_HEIGHT * 3F, width + 4F, 1);
						}
						if (healthPercent > 1) {
							healthPercent = 1F;
						}
						RenderUtil.drawRect(-width - 4F, -1F, (-width - 4F) + (maxWidth * healthPercent), 1F, hpBarColor);
						RenderUtil.drawRect((-width - 4F) + (maxWidth * healthPercent), -1F, width + 4F, 1F, bgColor);
						if (font.getValue()) {
							FontLoaders.F18.drawString(tag, -width, -FontLoaders.F18.getHeight() * 2 - 4, Color.WHITE.getRGB());
							GL11.glScalef(0.5F, 0.5F, 0.5F);
							FontLoaders.F18.drawString("Health: " + entity.getHealth(), -width * 2, -FontLoaders.F18.getHeight() * 2, Color.WHITE.getRGB());
						} else {
							fontRenderer.drawString(tag, -width, -fontRenderer.FONT_HEIGHT * 2 - 4, Color.WHITE.getRGB());
							GL11.glScalef(0.5F, 0.5F, 0.5F);
							fontRenderer.drawString("Health: " + entity.getHealth(), -width * 2, -fontRenderer.FONT_HEIGHT * 2, Color.WHITE.getRGB());
						}
						RenderUtil.resetCaps();
						GlStateManager.resetColor();
						GL11.glColor4f(1F, 1F, 1F, 1F);
						GL11.glPopMatrix();
					}

				}
			}
		}
	}

	private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
		double tempY = y;
		tempY += (player.isSneaking() ? 0.5D : 0.7D);
		Entity camera = mc.getRenderViewEntity();
		CFontRenderer normal = FontLoaders.F24;
		assert camera != null;
		double originalPositionX = camera.posX;
		double originalPositionY = camera.posY;
		double originalPositionZ = camera.posZ;
		camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
		camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
		camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);

		String displayTag = getDisplayTag(player);
		double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
		int width = normal.getStringWidth(displayTag) / 2 ;
		double scale = (0.0018 + factor.getValue() * (distance * factor.getValue())) / 1300.0;
//        RenderUtil.drawBlurRect(-width - 2, -(FontLoaders.F24.getHeight() + 1), width + 2F, 0f,12);
		if (distance <= 8) {
			scale =(0.0018 + factor.getValue() * (8 * factor.getValue())) / 1300.0;
		}

		if(!scaleing.getValue()) {
			scale = factor.getValue() / 100.0;
		}


		GlStateManager.pushMatrix();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(1, -1500000);
		GlStateManager.disableLighting();
		GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
		GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-scale, -scale, scale);
		GlStateManager.disableDepth();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableBlend();



		GlStateManager.enableBlend();

			RenderUtil.drawShadow(-width- 2, -(normal.getHeight()+ 1), width + 2F + width +2, 0f + (normal.getHeight() + 1));
			RenderUtil.drawRect(-width - 2, -(normal.getHeight() + 1), width + 2F, 2f, new Color(0,0,0,120).getRGB());
			RenderUtil.drawRect(-width - 2, 0f, -width - 2 + player.getHealth()/player.getMaxHealth() * (width +width + 4), 2F, new Color(220,0,0,200).getRGB());
		GlStateManager.disableBlend();

		normal.drawString(displayTag, -width, -(normal.getHeight() - 1), this.getDisplayColour(player));

		ItemStack renderMainHand = player.getCurrentEquippedItem();
		if(renderMainHand != null && renderMainHand.hasEffect() && (renderMainHand.getItem() instanceof ItemTool || renderMainHand.getItem() instanceof ItemArmor)) {
			renderMainHand.stackSize = 1;
		}

		if(renderMainHand != null && armor.getValue()) {
			GlStateManager.pushMatrix();
			int xOffset = -8;
			for (ItemStack stack : player.inventory.armorInventory) {
				if (stack != null) {
					xOffset -= 8;
				}
			}

			xOffset -= 8;
			ItemStack renderOffhand = player.getCurrentEquippedItem();
			if(renderOffhand.hasEffect() && (renderOffhand.getItem() instanceof ItemTool || renderOffhand.getItem() instanceof ItemArmor)) {
				renderOffhand.stackSize = 1;
			}

			this.renderItemStack(renderOffhand, xOffset,  -(FontLoaders.F20.getHeight() + 1)*2);
			xOffset += 16;

			for (ItemStack stack : player.inventory.armorInventory) {
				if (stack != null) {
					ItemStack armourStack = stack.copy();
					if (armourStack.hasEffect() && (armourStack.getItem() instanceof ItemTool || armourStack.getItem() instanceof ItemArmor)) {
						armourStack.stackSize = 1;
					}

					this.renderItemStack(armourStack, xOffset,  -(FontLoaders.F20.getHeight() + 1)*2);
					xOffset += 16;
				}
			}

//            this.renderItemStack(renderMainHand, xOffset,  -(FontLoaders.Baloo24.getHeight() + 1)*2);

			GlStateManager.popMatrix();
		}

		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		camera.posX = originalPositionX;
		camera.posY = originalPositionY;
		camera.posZ = originalPositionZ;
		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.disablePolygonOffset();
		GlStateManager.doPolygonOffset(1, 1500000);
		GlStateManager.popMatrix();


	}

	private void renderItemStack(ItemStack stack, int x, int y) {
		GlStateManager.pushMatrix();
		GlStateManager.depthMask(true);
//        GlStateManager.clear(GL11.GL_ACCUM);

		RenderHelper.enableStandardItemLighting();
		mc.getRenderItem().zLevel = -150.0F;
		GlStateManager.disableAlpha();
		GlStateManager.enableDepth();
		GlStateManager.disableCull();

		mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, y);

		mc.getRenderItem().zLevel = 0.0F;
		RenderHelper.disableStandardItemLighting();

		GlStateManager.enableCull();
		GlStateManager.enableAlpha();
		GlStateManager.scale(2F, 2F, 2F);
		GlStateManager.popMatrix();
	}

	private String getDisplayTag(EntityPlayer player) {
		String name = player.getDisplayName().getFormattedText();
		if(name.contains(mc.getSession().getUsername())) {
			name = "You";
		}

		float health = getHealth(player);
		String pingStr = "";
		String idString = "";

		if(Math.floor(health) == health) {
			name = name+ " " + (health > 0 ? (int) Math.floor(health) : "dead");
		} else {
			name = name + " " + (health > 0 ? (int) health : "dead");
		}
		return pingStr + idString + name;
	}

	private int getDisplayColour(EntityPlayer player) {
		int colour = 0xFFAAAAAA;
		if(FriendManager.isFriend(player.getName())) {
			return 0xFF55C0ED;
		} else if (player.isInvisible()) {
			colour = 0xFFef0147;
		} else if (player.isSneaking()) {
			colour = 0xFF9d1995;
		}
		return colour;
	}

	private double interpolate(double previous, double current, float delta) {
		return (previous + (current - previous) * delta);
	}

	public static boolean isLiving(final Entity entity) {
		return entity instanceof EntityLivingBase;
	}

	public static float getHealth(final Entity entity) {
		if (isLiving(entity)) {
			final EntityLivingBase livingBase = (EntityLivingBase)entity;
			return livingBase.getHealth() + livingBase.getAbsorptionAmount();
		}
		return 0.0f;
	}

}
