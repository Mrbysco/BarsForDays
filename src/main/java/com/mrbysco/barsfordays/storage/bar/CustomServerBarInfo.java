package com.mrbysco.barsfordays.storage.bar;

import com.google.common.collect.Sets;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class CustomServerBarInfo extends ServerBarInfo {
	private final ResourceLocation id;
	private final Set<UUID> players = Sets.newHashSet();
	private int value;
	private int max = 100;

	public CustomServerBarInfo(ResourceLocation location, ITextComponent component) {
		super(component, BarInfo.Color.WHITE, BarInfo.Overlay.PROGRESS);
		this.id = location;
		this.setPercent(0.0F);
	}

	public ResourceLocation getTextId() {
		return this.id;
	}

	public void addPlayer(ServerPlayerEntity playerEntity) {
		super.addPlayer(playerEntity);
		this.players.add(playerEntity.getUUID());
	}

	public void addOfflinePlayer(UUID uuid) {
		this.players.add(uuid);
	}

	public void removePlayer(ServerPlayerEntity playerEntity) {
		super.removePlayer(playerEntity);
		this.players.remove(playerEntity.getUUID());
	}

	public void removeAllPlayers() {
		super.removeAllPlayers();
		this.players.clear();
	}

	public int getValue() {
		return this.value;
	}

	public int getMax() {
		return this.max;
	}

	public void setValue(int value) {
		this.value = value;
		this.setPercent(MathHelper.clamp((float)value / (float)this.max, 0.0F, 1.0F));
	}

	public void setMax(int max) {
		this.max = max;
		this.setPercent(MathHelper.clamp((float)this.value / (float)max, 0.0F, 1.0F));
	}

	public final ITextComponent getDisplayName() {
		return TextComponentUtils.wrapInSquareBrackets(this.getName()).withStyle((style) -> {
			return style.withColor(this.getColor().getFormatting()).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.getTextId().toString()))).withInsertion(this.getTextId().toString());
		});
	}

	public boolean setPlayers(Collection<ServerPlayerEntity> playerEntities) {
		Set<UUID> set = Sets.newHashSet();
		Set<ServerPlayerEntity> set1 = Sets.newHashSet();

		for(UUID uuid : this.players) {
			boolean flag = false;

			for(ServerPlayerEntity serverplayerentity : playerEntities) {
				if (serverplayerentity.getUUID().equals(uuid)) {
					flag = true;
					break;
				}
			}

			if (!flag) {
				set.add(uuid);
			}
		}

		for(ServerPlayerEntity serverplayerentity1 : playerEntities) {
			boolean flag1 = false;

			for(UUID uuid2 : this.players) {
				if (serverplayerentity1.getUUID().equals(uuid2)) {
					flag1 = true;
					break;
				}
			}

			if (!flag1) {
				set1.add(serverplayerentity1);
			}
		}

		for(UUID uuid1 : set) {
			for(ServerPlayerEntity serverplayerentity3 : this.getPlayers()) {
				if (serverplayerentity3.getUUID().equals(uuid1)) {
					this.removePlayer(serverplayerentity3);
					break;
				}
			}

			this.players.remove(uuid1);
		}

		for(ServerPlayerEntity serverplayerentity2 : set1) {
			this.addPlayer(serverplayerentity2);
		}

		return !set.isEmpty() || !set1.isEmpty();
	}

	public CompoundNBT save() {
		CompoundNBT compoundnbt = new CompoundNBT();
		compoundnbt.putString("Name", ITextComponent.Serializer.toJson(this.name));
		compoundnbt.putBoolean("Visible", this.isVisible());
		compoundnbt.putDouble("Scale", getScale());
		compoundnbt.putInt("posX", getXPos());
		compoundnbt.putInt("posY", getYPos());
		compoundnbt.putBoolean("centerX", isCenterX());
		compoundnbt.putBoolean("centerY", isCenterY());
		compoundnbt.putBoolean("invertX", isXInverted());
		compoundnbt.putBoolean("invertY", isYInverted());
		compoundnbt.putInt("Value", this.value);
		compoundnbt.putInt("Max", this.max);
		compoundnbt.putString("Color", this.getColor().getName());
		compoundnbt.putString("Overlay", this.getOverlay().getName());
		ListNBT listnbt = new ListNBT();

		for(UUID uuid : this.players) {
			listnbt.add(NBTUtil.createUUID(uuid));
		}

		compoundnbt.put("Players", listnbt);
		return compoundnbt;
	}

	public static CustomServerBarInfo load(CompoundNBT nbt, ResourceLocation location) {
		CustomServerBarInfo CustomBarInfo = new CustomServerBarInfo(location, ITextComponent.Serializer.fromJson(nbt.getString("Name")));
		CustomBarInfo.setVisible(nbt.getBoolean("Visible"));
		CustomBarInfo.setScale(nbt.getDouble("Scale"));
		CustomBarInfo.setXPos(nbt.getInt("posX"));
		CustomBarInfo.setYPos(nbt.getInt("posY"));
		CustomBarInfo.setCenterX(nbt.getBoolean("centerX"));
		CustomBarInfo.setCenterY(nbt.getBoolean("centerY"));
		CustomBarInfo.setXInverted(nbt.getBoolean("invertX"));
		CustomBarInfo.setYInverted(nbt.getBoolean("invertY"));
		CustomBarInfo.setValue(nbt.getInt("Value"));
		CustomBarInfo.setMax(nbt.getInt("Max"));
		CustomBarInfo.setColor(BarInfo.Color.byName(nbt.getString("Color")));
		CustomBarInfo.setOverlay(BarInfo.Overlay.byName(nbt.getString("Overlay")));
		ListNBT listnbt = nbt.getList("Players", 11);

		for(int i = 0; i < listnbt.size(); ++i) {
			CustomBarInfo.addOfflinePlayer(NBTUtil.loadUUID(listnbt.get(i)));
		}

		return CustomBarInfo;
	}

	public void onPlayerConnect(ServerPlayerEntity serverPlayerEntity) {
		if (this.players.contains(serverPlayerEntity.getUUID())) {
			this.addPlayer(serverPlayerEntity);
		}

	}

	public void onPlayerDisconnect(ServerPlayerEntity serverPlayerEntity) {
		super.removePlayer(serverPlayerEntity);
	}
}
