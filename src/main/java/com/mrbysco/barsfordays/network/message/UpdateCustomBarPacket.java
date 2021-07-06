package com.mrbysco.barsfordays.network.message;

import com.mrbysco.barsfordays.client.ClientHandler;
import com.mrbysco.barsfordays.storage.bar.BarInfo;
import com.mrbysco.barsfordays.storage.bar.ServerBarInfo;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdateCustomBarPacket {
	public UUID id;
	public UpdateCustomBarPacket.Operation operation;
	public ITextComponent name;
	public float pct;
	public double scale;
	public int posX;
	public int posY;
	public boolean centerX;
	public boolean centerY;
	public boolean invertX;
	public boolean invertY;
	public BarInfo.Color color;
	public BarInfo.Overlay overlay;

	public UpdateCustomBarPacket(UpdateCustomBarPacket.Operation operation, ServerBarInfo customInfo) {
		this.operation = operation;
		this.id = customInfo.getId();
		this.name = customInfo.getName();
		this.pct = customInfo.getPercent();
		this.scale = customInfo.getScale();
		this.posX = customInfo.getXPos();
		this.posY = customInfo.getYPos();
		this.centerX = customInfo.isCenterX();
		this.centerY = customInfo.isCenterY();
		this.invertX = customInfo.isXInverted();
		this.invertY = customInfo.isYInverted();
		this.color = customInfo.getColor();
		this.overlay = customInfo.getOverlay();
	}

	public UpdateCustomBarPacket(PacketBuffer buf) {
		this.id = buf.readUUID();
		this.operation = buf.readEnum(UpdateCustomBarPacket.Operation.class);
		switch(this.operation) {
			case ADD:
				this.name = buf.readComponent();
				this.pct = buf.readFloat();
				this.color = buf.readEnum(BarInfo.Color.class);
				this.overlay = buf.readEnum(BarInfo.Overlay.class);
			case REMOVE:
			default:
				break;
			case UPDATE_PCT:
				this.pct = buf.readFloat();
				break;
			case UPDATE_NAME:
				this.name = buf.readComponent();
				break;
			case UPDATE_STYLE:
				this.color = buf.readEnum(BarInfo.Color.class);
				this.overlay = buf.readEnum(BarInfo.Overlay.class);
				break;
			case UPDATE_POSITION:
				this.scale = buf.readDouble();
				this.posX = buf.readInt();
				this.posY = buf.readInt();
				this.centerX = buf.readBoolean();
				this.centerY = buf.readBoolean();
				this.invertX = buf.readBoolean();
				this.invertY = buf.readBoolean();
		}
	}

	public void encode(PacketBuffer packetBuffer) {
		packetBuffer.writeUUID(this.id);
		packetBuffer.writeEnum(this.operation);
		switch(this.operation) {
			case ADD:
				packetBuffer.writeComponent(this.name);
				packetBuffer.writeFloat(this.pct);
				packetBuffer.writeEnum(this.color);
				packetBuffer.writeEnum(this.overlay);
			case REMOVE:
			default:
				break;
			case UPDATE_PCT:
				packetBuffer.writeFloat(this.pct);
				break;
			case UPDATE_NAME:
				packetBuffer.writeComponent(this.name);
				break;
			case UPDATE_STYLE:
				packetBuffer.writeEnum(this.color);
				packetBuffer.writeEnum(this.overlay);
				break;
			case UPDATE_POSITION:
				packetBuffer.writeDouble(this.scale);
				packetBuffer.writeInt(this.posX);
				packetBuffer.writeInt(this.posY);
				packetBuffer.writeBoolean(this.centerX);
				packetBuffer.writeBoolean(this.centerY);
				packetBuffer.writeBoolean(this.invertX);
				packetBuffer.writeBoolean(this.invertY);
		}
	}

	public static UpdateCustomBarPacket decode(final PacketBuffer packetBuffer) {
		return new UpdateCustomBarPacket(packetBuffer);
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				ClientHandler.handleUpdatePacket(this);
			}
		});
		ctx.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public UUID getId() {
		return this.id;
	}

	@OnlyIn(Dist.CLIENT)
	public UpdateCustomBarPacket.Operation getOperation() {
		return this.operation;
	}

	@OnlyIn(Dist.CLIENT)
	public ITextComponent getName() {
		return this.name;
	}

	@OnlyIn(Dist.CLIENT)
	public float getPercent() {
		return this.pct;
	}

	@OnlyIn(Dist.CLIENT)
	public BarInfo.Color getColor() {
		return this.color;
	}

	@OnlyIn(Dist.CLIENT)
	public BarInfo.Overlay getOverlay() {
		return this.overlay;
	}

	@OnlyIn(Dist.CLIENT)
	public double getScale() {
		return scale;
	}

	@OnlyIn(Dist.CLIENT)
	public int getPosX() {
		return posX;
	}

	@OnlyIn(Dist.CLIENT)
	public int getPosY() {
		return posY;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isCenterX() {
		return centerX;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isCenterY() {
		return centerY;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isXInverted() {
		return invertX;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isYInverted() {
		return invertY;
	}

	public static enum Operation {
		ADD,
		REMOVE,
		UPDATE_PCT,
		UPDATE_NAME,
		UPDATE_STYLE,
		UPDATE_POSITION;
	}
}
