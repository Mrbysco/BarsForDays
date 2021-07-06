package com.mrbysco.barsfordays.network;

import com.mrbysco.barsfordays.Reference;
import com.mrbysco.barsfordays.network.message.UpdateCustomBarPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkForDays {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Reference.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	private static int id = 0;

	public static void init(){
		CHANNEL.registerMessage(id++, UpdateCustomBarPacket.class, UpdateCustomBarPacket::encode, UpdateCustomBarPacket::decode, UpdateCustomBarPacket::handle);
	}
}
