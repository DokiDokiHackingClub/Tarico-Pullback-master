package dev.tarico.module.gui.list;

import dev.tarico.module.gui.font.CFontRenderer;
import dev.tarico.module.gui.font.FontLoaders;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.module.value.Value;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ValueButton {
	public Value value;
	public String name;
	public boolean custom;
	public boolean change;
	public int x;
	public int y;
	public double opacity;

	public ValueButton(final Value value, final int x, final int y) {
		this.custom = false;
		this.opacity = 0.0;
		this.value = value;
		this.x = x;
		this.y = y;
		this.name = "";
		if (this.value instanceof BooleanValue) {
			this.change = (boolean) ((BooleanValue) this.value).getValue();
		} else if (this.value instanceof ModeValue) {
			this.name = new StringBuilder().append(((ModeValue) this.value).getValue()).toString();
		} else if (value instanceof NumberValue) {
			final NumberValue v = (NumberValue) value;
			this.name = String.valueOf(this.name)
					+ (v.isInteger() ? ((Number) v.getValue()).intValue() : ((Number) v.getValue()).doubleValue());
		}
		this.opacity = 0.0;
	}

	public void render(final int mouseX, final int mouseY) {
		CFontRenderer mfont = FontLoaders.F16;
		if (!this.custom) {
			if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
					&& mouseY < this.y + FontLoaders.F18.getStringHeight() + 5) {
				if (this.opacity + 10.0 < 200.0) {
					this.opacity += 10.0;
				} else {
					this.opacity = 200.0;
				}
			} else if (this.opacity - 6.0 > 0.0) {
				this.opacity -= 6.0;
			} else {
				this.opacity = 0.0;
			}
			if (this.value instanceof BooleanValue) {
				this.change = (boolean) ((BooleanValue) this.value).getValue();
			} else if (this.value instanceof ModeValue) {
				this.name = new StringBuilder().append(((ModeValue) this.value).getValue()).toString();
			} else if (this.value instanceof NumberValue) {
				final NumberValue v = (NumberValue) this.value;
				this.name = new StringBuilder().append(
								v.isInteger() ? ((Number) v.getValue()).intValue() : ((Number) v.getValue()).doubleValue())
						.toString();
				if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
						&& mouseY < this.y + mfont.getStringHeight() + 5 && Mouse.isButtonDown(0)) {
					final double min = v.getMinimum().doubleValue();
					final double max = v.getMaximum().doubleValue();
					final double inc = v.getIncrement().doubleValue();
					final double valAbs = mouseX - (this.x + 1.0);
					double perc = valAbs / 68.0;
					perc = Math.min(Math.max(0.0, perc), 1.0);
					final double valRel = (max - min) * perc;
					double val = min + valRel;
					val = Math.round(val * (1.0 / inc)) / (1.0 / inc);
					v.setValue(val);
				}
			}
			RenderUtil.drawRect(0.0, 0.0, 0.0, 0.0, 0);
//			RenderUtil.R2DUtils.drawRect(this.x - 10, this.y - 4, this.x + 80, this.y + 11,
//					new Color(220, 220, 220).getRGB());
            Gui.drawRect(this.x - 10, this.y - 4, this.x + 80, this.y + 11, RenderUtil.reAlpha(1, 0.3f));
            mfont.drawString(this.value.getName(), this.x - 5, this.y + 1, -1);// ����
            mfont.drawString(this.name, this.x + 76 - mfont.getStringWidth(this.name), this.y + 2,
                    -1);// ModeValue����ֵ
			if (this.value instanceof NumberValue) {
				final NumberValue v = (NumberValue) this.value;
				final double render = 68.0f * (((Number) v.getValue()).floatValue() - v.getMinimum().floatValue())
						/ (v.getMaximum().floatValue() - v.getMinimum().floatValue());
				RenderUtil.drawRect(this.x, this.y + mfont.getStringHeight() + 3,
						(float) (this.x + render + 1), this.y + mfont.getStringHeight() + 4,
						new Color(68, 68, 68).getRGB());
			}
			if (this.change) {
				Gui.drawRect(this.x + 70, this.y, this.x + 77, this.y + 7, new Color(108, 108, 108).getRGB());
			}
		}
	}

	public void key(final char typedChar, final int keyCode) {
	}

	public void click(final int mouseX, final int mouseY, final int button) {
		if (!this.custom && mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
				&& mouseY < this.y + FontLoaders.F18.getStringHeight() + 5) {
			if (this.value instanceof BooleanValue) {
				final BooleanValue v = (BooleanValue) this.value;
				v.setValue(!(boolean) v.getValue());
				return;
			}
			if (this.value instanceof ModeValue) {
				final ModeValue m = (ModeValue) this.value;
				final Enum current = (Enum) m.getValue();
				final int next = (current.ordinal() + 1 >= m.getModes().length) ? 0 : (current.ordinal() + 1);
				this.value.setValue(m.getModes()[next]);
			}
		}
	}
}
