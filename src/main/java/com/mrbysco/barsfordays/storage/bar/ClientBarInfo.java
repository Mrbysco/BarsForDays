package com.mrbysco.barsfordays.storage.bar;

import com.mrbysco.barsfordays.network.message.UpdateCustomBarPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class ClientBarInfo extends BarInfo {
	protected float targetPercent;
	protected long setTime;

	public ClientBarInfo(UpdateCustomBarPacket packet) {
		super(packet.getId(), packet.getName(), packet.getColor(), packet.getOverlay());
		this.targetPercent = packet.getPercent();
		this.percent = packet.getPercent();
		this.setTime = Util.getMillis();
		this.setScale(packet.getScale());
		this.setXPos(packet.getPosX());
		this.setYPos(packet.getPosY());
		this.setCenterX(packet.isCenterX());
		this.setCenterY(packet.isCenterY());
	}

	public void setPercent(float percent) {
		this.percent = this.getPercent();
		this.targetPercent = percent;
		this.setTime = Util.getMillis();
	}

	public float getPercent() {
		long i = Util.getMillis() - this.setTime;
		float f = MathHelper.clamp((float)i / 100.0F, 0.0F, 1.0F);
		return MathHelper.lerp(f, this.percent, this.targetPercent);
	}

	public void update(UpdateCustomBarPacket packet) {
		switch(packet.getOperation()) {
			case UPDATE_NAME:
				this.setName(packet.getName());
				break;
			case UPDATE_PCT:
				this.setPercent(packet.getPercent());
				break;
			case UPDATE_STYLE:
				this.setColor(packet.getColor());
				this.setOverlay(packet.getOverlay());
				break;
			case UPDATE_POSITION:
				this.setScale(packet.getScale());
				this.setXPos(packet.getPosX());
				this.setYPos(packet.getPosY());
				this.setCenterX(packet.isCenterX());
				this.setCenterY(packet.isCenterY());
				this.setXInverted(packet.isXInverted());
				this.setYInverted(packet.isYInverted());
		}
	}
}
