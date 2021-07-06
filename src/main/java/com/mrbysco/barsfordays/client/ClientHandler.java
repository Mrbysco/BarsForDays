package com.mrbysco.barsfordays.client;

import com.mrbysco.barsfordays.Reference;
import com.mrbysco.barsfordays.network.message.UpdateCustomBarPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = Reference.MOD_ID, value = Dist.CLIENT )
public class ClientHandler {
	public static void handleUpdatePacket(UpdateCustomBarPacket packet) {
		OverlayHandler.update(packet);
	}

	@SubscribeEvent
	public static void onLogIn(ClientPlayerNetworkEvent.LoggedOutEvent event) {
		OverlayHandler.reset();
	}

}
