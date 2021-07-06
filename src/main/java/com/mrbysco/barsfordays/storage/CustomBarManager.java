package com.mrbysco.barsfordays.storage;

import com.google.common.collect.Maps;
import com.mrbysco.barsfordays.Reference;
import com.mrbysco.barsfordays.storage.bar.CustomServerBarInfo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public class CustomBarManager extends WorldSavedData {
	private static final String DATA_NAME = Reference.MOD_ID + "_world_data";

	private final Map<ResourceLocation, CustomServerBarInfo> barMap = Maps.newHashMap();

	public CustomBarManager() {
		super(DATA_NAME);
	}

	@Nullable
	public CustomServerBarInfo get(ResourceLocation location) {
		return this.barMap.get(location);
	}

	public CustomServerBarInfo create(ResourceLocation location, ITextComponent textComponent) {
		CustomServerBarInfo customBarInfo = new CustomServerBarInfo(location, textComponent);
		this.barMap.put(location, customBarInfo);
		return customBarInfo;
	}

	public void remove(CustomServerBarInfo barInfo) {
		this.barMap.remove(barInfo.getTextId());
	}

	public Collection<ResourceLocation> getIds() {
		return this.barMap.keySet();
	}

	public Collection<CustomServerBarInfo> getEvents() {
		return this.barMap.values();
	}

	@Override
	public void load(CompoundNBT tag) {
		for(String s : tag.getAllKeys()) {
			ResourceLocation resourcelocation = new ResourceLocation(s);
			this.barMap.put(resourcelocation, CustomServerBarInfo.load(tag.getCompound(s), resourcelocation));
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		CompoundNBT compoundnbt = new CompoundNBT();

		for(CustomServerBarInfo barInfo : this.barMap.values()) {
			compoundnbt.put(barInfo.getTextId().toString(), barInfo.save());
		}

		return compoundnbt;
	}

	public void onPlayerConnect(ServerPlayerEntity playerEntity) {
		for(CustomServerBarInfo barInfo : this.barMap.values()) {
			barInfo.onPlayerConnect(playerEntity);
		}
	}

	public void onPlayerDisconnect(ServerPlayerEntity playerEntity) {
		for(CustomServerBarInfo barInfo : this.barMap.values()) {
			barInfo.onPlayerDisconnect(playerEntity);
		}
	}

	public static CustomBarManager get(World world) {
		if (!(world instanceof ServerWorld)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerWorld overworld = world.getServer().getLevel(World.OVERWORLD);

		DimensionSavedDataManager storage = overworld.getDataStorage();
		return storage.computeIfAbsent(CustomBarManager::new, DATA_NAME);
	}
}
