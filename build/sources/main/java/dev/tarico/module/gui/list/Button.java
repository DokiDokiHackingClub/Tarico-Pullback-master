package dev.tarico.module.gui.list;

import com.google.common.collect.Lists;
import dev.tarico.Client;
import dev.tarico.module.gui.font.CFontRenderer;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.modules.Module;
import dev.tarico.module.value.Value;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;

public class Button {
	public Module cheat;
	public Window parent;
	public int x;
	public int y;
	public int index;
	public int remander;
	public double opacity = 0.0;
	public ArrayList<ValueButton> buttons = Lists.newArrayList();
	public boolean expand;
	CFontRenderer font = FontLoaders.F18;

	public Button(Module cheat, int x, int y) {
		this.cheat = cheat;
		this.x = x;
		this.y = y;
		int y2 = y + 14;
		for (Value v : cheat.getValues()) {
			this.buttons.add(new ValueButton(v, x + 5, y2));
			y2 += 15;
		}
		this.buttons.add(new KeyBindButton(cheat, x + 5, y2));
	}

	public void render(int mouseX, int mouseY) {
		CFontRenderer font = FontLoaders.F20;
		if (this.index != 0) {
			Button b2 = this.parent.buttons.get(this.index - 1);
			this.y = b2.y + 15 + (b2.expand ? 15 * b2.buttons.size() : 0);
		}
		int i = 0;
		while (i < this.buttons.size()) {
			this.buttons.get((int) i).y = this.y + 14 + 15 * i;
			this.buttons.get((int) i).x = this.x + 5;
			++i;
		}
		Gui.drawRect(this.x - 5, this.y - 5, this.x + 85, this.y + font.getStringHeight() + 2,
				RenderUtil.reAlpha(1, 0.5f));
		if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + font.getStringHeight() + 4) {
			Gui.drawRect(this.x - 5, this.y - 5, this.x + 85, (int) (this.y + font.getStringHeight() + 2), RenderUtil.reAlpha(1, 0.5f));
		}
		if (this.cheat.getState()) {
			font.drawString(this.cheat.getName(), this.x, this.y, Client.instance.mainColor.getColor().getRGB());
		} else {
			font.drawString(this.cheat.getName(), this.x, this.y, -1);
		}

		if (this.expand) {
			this.buttons.forEach(b -> b.render(mouseX, mouseY));
		}
	}

	public void key(char typedChar, int keyCode) {
		this.buttons.forEach(b -> b.key(typedChar, keyCode));
	}

	public void click(int mouseX, int mouseY, int button) {
		if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
				&& mouseY < this.y + font.getStringHeight() + 4) {
			if (button == 0) {
				this.cheat.toggle();
			}
			if (button == 1 && !this.buttons.isEmpty()) {
				boolean bl = this.expand = !this.expand;
			}
		}
		if (this.expand) {
			this.buttons.forEach(b -> b.click(mouseX, mouseY, button));
		}
	}

	public void setParent(Window parent) {
		this.parent = parent;
		int i = 0;
		while (i < this.parent.buttons.size()) {
			if (this.parent.buttons.get(i) == this) {
				this.index = i;
				this.remander = this.parent.buttons.size() - i;
				break;
			}
			++i;
		}
	}
}
