package toni.jtn;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import toni.jtn.content.lore.PoemHandler;
import toni.jtn.foundation.Registration;
import toni.jtn.foundation.config.AllConfigs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import toni.lib.utils.PlatformUtils;


public class JTN implements ModInitializer, ClientModInitializer
{
    public static final String MODNAME = "Journey to Niflheim";
    public static final String ID = "jtn";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);

    public static MinecraftServer server;

    public JTN() { }


    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> JTN.server = server);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> JTN.server = null);

        AllConfigs.register((type, spec) -> {
            NeoForgeConfigRegistry.INSTANCE.register(JTN.ID, type, spec);
        });

        PlayerBlockBreakEvents.AFTER.register(PoemHandler::sendYggdrvald);

        Registration.bootstrap();
    }

    @Override
    public void onInitializeClient() {
        ConfigScreenFactoryRegistry.INSTANCE.register(JTN.ID, ConfigurationScreen::new);
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            JTNClient.ticks++;
        });

        //TooltipComponentCallback.EVENT.register();
    }




    public static @Nullable Player getClientPlayer() {
        if (PlatformUtils.isDedicatedServer())
            return null;

        return JTNClient.getClientPlayer();
    }

    public static ResourceLocation location(String path) { return ResourceLocation.fromNamespaceAndPath(ID, path); }
    public static MutableComponent lang(String type, String path, Object... args) { return Component.translatable(langKey(type, path), args); }
    public static String langKey(String type, String path) { return type + "." + ID + "." + path;}
}
