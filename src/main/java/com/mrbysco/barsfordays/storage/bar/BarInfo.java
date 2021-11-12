package com.mrbysco.barsfordays.storage.bar;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.UUID;

public abstract class BarInfo {
	private final UUID id;
	protected ITextComponent name;
	protected float percent;
	protected BarInfo.Color color;
	protected BarInfo.Overlay overlay;
	protected double scale = 1.0;
	protected int xPos = 0;
	protected int yPos = 0;
	protected boolean centerX = false;
	protected boolean centerY = false;
	protected boolean invertX = false;
	protected boolean invertY = false;

	public BarInfo(UUID uuid, ITextComponent textComponent, BarInfo.Color color, BarInfo.Overlay overlay) {
		this.id = uuid;
		this.name = textComponent;
		this.color = color;
		this.overlay = overlay;
		this.percent = 1.0F;
	}

	public UUID getId() {
		return this.id;
	}

	public ITextComponent getName() {
		return this.name;
	}

	public void setName(ITextComponent textComponent) {
		this.name = textComponent;
	}

	public float getPercent() {
		return this.percent;
	}

	public void setPercent(float percent) {
		this.percent = percent;
	}

	public BarInfo.Color getColor() {
		return this.color;
	}

	public void setColor(BarInfo.Color color) {
		this.color = color;
	}

	public BarInfo.Overlay getOverlay() {
		return this.overlay;
	}

	public void setOverlay(BarInfo.Overlay overlay) {
		this.overlay = overlay;
	}

	public double getScale() {
		return this.scale;
	}

	public void setScale(double newScale) {
		this.scale = newScale;
	}

	public int getXPos() {
		return xPos;
	}

	public void setXPos(int xPos) {
		this.xPos = xPos;
	}

	public int getYPos() {
		return yPos;
	}

	public void setYPos(int yPos) {
		this.yPos = yPos;
	}

	public boolean isCenterX() {
		return centerX;
	}

	public void setCenterX(boolean centerX) {
		this.centerX = centerX;
	}

	public boolean isCenterY() {
		return centerY;
	}

	public void setCenterY(boolean centerY) {
		this.centerY = centerY;
	}

	public boolean isXInverted() {
		return invertX;
	}

	public void setXInverted(boolean invertX) {
		this.invertX = invertX;
	}

	public boolean isYInverted() {
		return invertY;
	}

	public void setYInverted(boolean invertY) {
		this.invertY = invertY;
	}

	public enum Color {
		PINK("pink", TextFormatting.RED),
		BLUE("blue", TextFormatting.BLUE),
		RED("red", TextFormatting.DARK_RED),
		GREEN("green", TextFormatting.GREEN),
		YELLOW("yellow", TextFormatting.YELLOW),
		PURPLE("purple", TextFormatting.DARK_BLUE),
		WHITE("white", TextFormatting.WHITE);

		private final String name;
		private final TextFormatting formatting;

		Color(String name, TextFormatting formatting) {
			this.name = name;
			this.formatting = formatting;
		}

		public TextFormatting getFormatting() {
			return this.formatting;
		}

		public String getName() {
			return this.name;
		}

		public static BarInfo.Color byName(String name) {
			for(BarInfo.Color bossinfo$color : values()) {
				if (bossinfo$color.name.equals(name)) {
					return bossinfo$color;
				}
			}

			return WHITE;
		}
	}

	public enum Overlay {
		PROGRESS("progress"),
		NOTCHED_6("notched_6"),
		NOTCHED_10("notched_10"),
		NOTCHED_12("notched_12"),
		NOTCHED_20("notched_20");

		private final String name;

		Overlay(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static BarInfo.Overlay byName(String name) {
			for(BarInfo.Overlay bossinfo$overlay : values()) {
				if (bossinfo$overlay.name.equals(name)) {
					return bossinfo$overlay;
				}
			}

			return PROGRESS;
		}
	}
}
