package com.mrbysco.barsfordays.storage.bar;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrbysco.barsfordays.network.NetworkForDays;
import com.mrbysco.barsfordays.network.message.UpdateCustomBarPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ServerBarInfo extends BarInfo {
	private final Set<ServerPlayerEntity> players = Sets.newHashSet();
	private final Set<ServerPlayerEntity> unmodifiablePlayers = Collections.unmodifiableSet(this.players);
	private boolean visible = true;

	public ServerBarInfo(ITextComponent textComponent, BarInfo.Color color, BarInfo.Overlay overlay) {
		super(MathHelper.createInsecureUUID(), textComponent, color, overlay);
	}

	public void setPercent(float percent) {
		if (percent != this.percent) {
			super.setPercent(percent);
			this.broadcast(UpdateCustomBarPacket.Operation.UPDATE_PCT);
		}
	}

	public void setScale(double newScale) {
		if (newScale != this.scale) {
			super.setScale(newScale);
			this.broadcast(UpdateCustomBarPacket.Operation.UPDATE_POSITION);
		}
	}

	public void setXPos(int xPos) {
		if (xPos != this.xPos) {
			super.setXPos(xPos);
			this.broadcast(UpdateCustomBarPacket.Operation.UPDATE_POSITION);
		}
	}

	public void setYPos(int yPos) {
		if (yPos != this.yPos) {
			super.setYPos(yPos);
			this.broadcast(UpdateCustomBarPacket.Operation.UPDATE_POSITION);
		}
	}

	public void setCenterX(boolean centerX) {
		if (centerX != this.centerX) {
			super.setCenterX(centerX);
			this.broadcast(UpdateCustomBarPacket.Operation.UPDATE_POSITION);
		}
	}

	public void setCenterY(boolean centerY) {
		if (centerY != this.centerY) {
			super.setCenterY(centerY);
			this.broadcast(UpdateCustomBarPacket.Operation.UPDATE_POSITION);
		}
	}

	public void setXInverted(boolean invertX) {
		if (invertX != this.invertX) {
			super.setXInverted(invertX);
			this.broadcast(UpdateCustomBarPacket.Operation.UPDATE_POSITION);
		}
	}

	public void setYInverted(boolean invertY) {
		if (invertY != this.invertY) {
			super.setYInverted(invertY);
			this.broadcast(UpdateCustomBarPacket.Operation.UPDATE_POSITION);
		}
	}

	public void setColor(BarInfo.Color color) {
		if (color != this.color) {
			super.setColor(color);
			this.broadcast(UpdateCustomBarPacket.Operation.UPDATE_STYLE);
		}
	}

	public void setOverlay(BarInfo.Overlay overlay) {
		if (overlay != this.overlay) {
			super.setOverlay(overlay);
			this.broadcast(UpdateCustomBarPacket.Operation.UPDATE_STYLE);
		}
	}

	public void setName(ITextComponent textComponent) {
		if (!Objects.equal(textComponent, this.name)) {
			super.setName(textComponent);
			this.broadcast(UpdateCustomBarPacket.Operation.UPDATE_NAME);
		}
	}

	private void broadcast(UpdateCustomBarPacket.Operation operation) {
		if (this.visible) {
			UpdateCustomBarPacket supdatebossinfopacket = new UpdateCustomBarPacket(operation, this);

			for(ServerPlayerEntity serverplayerentity : this.players) {
				NetworkForDays.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverplayerentity), supdatebossinfopacket);
			}
		}
	}

	public void addPlayer(ServerPlayerEntity playerEntity) {
		if (this.players.add(playerEntity) && this.visible) {
			NetworkForDays.CHANNEL.send(PacketDistributor.PLAYER.with(() -> playerEntity), new UpdateCustomBarPacket(UpdateCustomBarPacket.Operation.ADD, this));
			NetworkForDays.CHANNEL.send(PacketDistributor.PLAYER.with(() -> playerEntity), new UpdateCustomBarPacket(UpdateCustomBarPacket.Operation.UPDATE_POSITION, this));
		}
	}

	public void removePlayer(ServerPlayerEntity playerEntity) {
		if (this.players.remove(playerEntity) && this.visible) {
			NetworkForDays.CHANNEL.send(PacketDistributor.PLAYER.with(() -> playerEntity), new UpdateCustomBarPacket(UpdateCustomBarPacket.Operation.REMOVE, this));
		}
	}

	public void removeAllPlayers() {
		if (!this.players.isEmpty()) {
			for(ServerPlayerEntity serverplayerentity : Lists.newArrayList(this.players)) {
				this.removePlayer(serverplayerentity);
			}
		}
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		if (visible != this.visible) {
			this.visible = visible;

			for(ServerPlayerEntity serverplayerentity : this.players) {
				NetworkForDays.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverplayerentity), new UpdateCustomBarPacket(visible ? UpdateCustomBarPacket.Operation.ADD : UpdateCustomBarPacket.Operation.REMOVE, this));
			}
		}
	}

	public Collection<ServerPlayerEntity> getPlayers() {
		return this.unmodifiablePlayers;
	}
}
