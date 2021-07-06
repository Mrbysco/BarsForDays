package com.mrbysco.barsfordays.handler;

import com.mrbysco.barsfordays.network.NetworkForDays;
import com.mrbysco.barsfordays.storage.CustomBarManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class LoginHandler {
	@SubscribeEvent
	public void onLogin(PlayerLoggedInEvent event) {
		PlayerEntity player = event.getPlayer();
		if(!player.level.isClientSide) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
			CustomBarManager barManager = CustomBarManager.get(serverPlayer.getServer().getLevel(World.OVERWORLD));
			barManager.onPlayerConnect(serverPlayer);
			barManager.setDirty(true);
		}
	}

	@SubscribeEvent
	public void onLogin(PlayerLoggedOutEvent event) {
		PlayerEntity player = event.getPlayer();
		if(!player.level.isClientSide) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
			CustomBarManager barManager = CustomBarManager.get(serverPlayer.getServer().getLevel(World.OVERWORLD));
			barManager.onPlayerDisconnect(serverPlayer);
			barManager.setDirty(true);
		}
	}
}
