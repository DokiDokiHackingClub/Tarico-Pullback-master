package dev.tarico.module.modules.utils;

import me.cubk.plugin.PickupFilterManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.modules.ModuleType;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PickupFilter extends Module {
	public ModeValue<Enum<Modes>> mode = new ModeValue<>("Mode", Modes.values(), Modes.Whitelist);
	public BooleanValue<Boolean> all = new BooleanValue<>("All", false);

	public PickupFilter() {
		super("PickupFilter", "Filters item picking", ModuleType.Utils);
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		if (this.all.getValue()) {
			event.setCanceled(true);
			return;
		}
		for (int itemId : PickupFilterManager.items) {
			Item item = Item.getItemById(itemId);
			if (item == null) continue;
			if (mode.getValue() == Modes.Whitelist) {
				if (event.item.getEntityItem().getItem() != item)
					event.setCanceled(true);
			} else {
				if (event.item.getEntityItem().getItem() == item)
					event.setCanceled(true);
			}
		}
	}

	enum Modes {
		Whitelist,
		Blacklist
	}
}
