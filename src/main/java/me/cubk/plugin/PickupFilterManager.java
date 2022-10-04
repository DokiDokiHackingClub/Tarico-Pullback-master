package me.cubk.plugin;

import dev.tarico.management.FileManager;
import dev.tarico.utils.client.Helper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;

import java.util.LinkedList;

public class PickupFilterManager {

	public static LinkedList<Integer> items = new LinkedList<Integer>();

	public static void addItem(int id) {
		try {
			if (Item.getItemById(id) == null) {
				Helper.sendMessage("Can not get item.");
				return;
			}
			for (int itemId : items) {
				if (itemId == id) {
					Helper.sendMessage("Item already added.");
					return;
				}
			}
			items.add(id);
			FileManager.savePickupFilter();
			Helper.sendMessage(String.format("\u00a77ID: \u00a73%s \u00a77 Name: \u00a73%s \u00a77- added.",
					id, I18n.format(Item.getItemById(id).getUnlocalizedName())));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		;
	}

	public static void removeItem(int id) {
		for (int itemId : items) {
			if (itemId == id) {
				items.remove((Object) (id));
				Helper.sendMessage(String.format("\u00a77ID: \u00a73%s \u00a77- Removed.", id));
				FileManager.savePickupFilter();
				return;
			}
		}
		Helper.sendMessage("Item not found.");
	}

	public static void clear() {
		if (items.isEmpty())
			return;
		items.clear();
		FileManager.savePickupFilter();
		Helper.sendMessage("\u00a7dPickupFilter \u00a77list clear.");
	}
}
