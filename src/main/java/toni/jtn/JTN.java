package toni.jtn;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import toni.jtn.content.lore.PoemHandler;
import toni.jtn.content.runes.SocketHelper;
import toni.jtn.content.runes.gem.ExtraGemBonusRegistry;
import toni.jtn.content.runes.gem.GemRegistry;
import toni.jtn.content.runes.gem.bonus.GemBonus;
import toni.jtn.foundation.Registration;
import toni.jtn.foundation.config.AllConfigs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import toni.jtn.foundation.events.GetEnchantmentLevelEvent;
import toni.jtn.foundation.network.PayloadHelper;
import toni.jtn.foundation.registry.ReloadListenerPayloads;
import toni.lib.utils.PlatformUtils;


public class JTN implements ModInitializer
{
    public static final String MODNAME = "Journey to Niflheim";
    public static final String ID = "jtn";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);

    public static MinecraftServer server;

    public JTN() { }


    @Override
    public void onInitialize() {
        Registration.bootstrap();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> JTN.server = server);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> JTN.server = null);

        AllConfigs.register((type, spec) -> {
            NeoForgeConfigRegistry.INSTANCE.register(JTN.ID, type, spec);
        });

        //PlayerBlockBreakEvents.AFTER.register(PoemHandler::sendYggdrvald);

        PayloadHelper.registerPayload(new ReloadListenerPayloads.Start.Provider());
        PayloadHelper.registerPayload(new ReloadListenerPayloads.Content.Provider<>());
        PayloadHelper.registerPayload(new ReloadListenerPayloads.End.Provider());

        PayloadHelper.registerProviders();

        GemBonus.initCodecs();

        GetEnchantmentLevelEvent.GET_ENCHANTMENT_LEVEL.register(((enchantments, stack) -> {
            SocketHelper.getGems(stack).getEnchantmentLevels(enchantments);
            return enchantments;
        }));

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(JTN.location("extra_gem_bonuses"), ExtraGemBonusRegistry.INSTANCE::injectRegistries);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(JTN.location("gems"), GemRegistry.INSTANCE::injectRegistries);

        ExtraGemBonusRegistry.INSTANCE.registerToBus();
        GemRegistry.INSTANCE.registerToBus();
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
