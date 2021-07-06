package com.mrbysco.barsfordays;

import com.mrbysco.barsfordays.client.OverlayHandler;
import com.mrbysco.barsfordays.commands.BarCommand;
import com.mrbysco.barsfordays.handler.LoginHandler;
import com.mrbysco.barsfordays.network.NetworkForDays;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class BarsForDays {
    private static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    public BarsForDays() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(new LoginHandler());

        MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.register(new OverlayHandler());
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        NetworkForDays.init();
    }

    public void onCommandRegister(RegisterCommandsEvent event) {
        BarCommand.initializeCommands(event.getDispatcher());
    }
}
