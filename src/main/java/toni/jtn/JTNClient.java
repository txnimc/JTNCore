package toni.jtn;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.jetbrains.annotations.Nullable;
import toni.jtn.content.runes.gem.ExtraGemBonusRegistry;
import toni.jtn.content.runes.tooltip.AttributeTooltipManager;
import toni.jtn.content.runes.tooltip.AttributeTooltips;
import toni.jtn.content.runes.tooltip.SocketTooltipRenderer;
import toni.jtn.content.runes.tooltip.TooltipHandler;
import toni.jtn.foundation.events.ModifyComponents;
import toni.jtn.foundation.network.IPayloadContext;
import toni.jtn.foundation.network.PayloadHelper;
import toni.jtn.foundation.network.PayloadProvider;

public class JTNClient implements ClientModInitializer {
    public static final ResourceLocation GENERAL_ICONS = JTN.location("textures/gui/general_icons.png");
    public static long ticks = 0;


    @Override
    public void onInitializeClient() {
        ConfigScreenFactoryRegistry.INSTANCE.register(JTN.ID, ConfigurationScreen::new);
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            JTNClient.ticks++;
        });

        ItemTooltipCallback.EVENT.register(TooltipHandler::tooltips);
        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof SocketTooltipRenderer.SocketComponent comp)
                return new SocketTooltipRenderer(comp);

            if (data instanceof AttributeTooltips.AttributeComponentRenderer.AttributeComponent attr)
                return new AttributeTooltips.AttributeComponentRenderer(attr.stack(), attr.slot());

            return null;
        });

        ScreenEvents.BEFORE_INIT.register(((minecraft, screen, i, i1) -> {
            if (screen instanceof AbstractContainerScreen<?> containerScreen) {
                ScreenEvents.afterRender(containerScreen).register(TooltipHandler::renderContainerScreen);
            }
        }));

        ModifyComponents.MODIFY_COMPONENTS.register(TooltipHandler::modifyComponents);
        ModifyComponents.MODIFY_COMPONENTS.register(AttributeTooltips::makeTooltip);

        PayloadHelper.registerProvidersClient();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new AttributeTooltipManager());

    }


    public static void registerNetworkingHandler(PayloadProvider prov) {
        ClientPlayNetworking.registerGlobalReceiver(prov.getType(), (payload, context) -> {
            context.client().execute(() -> {
                new PayloadHelper.PayloadHandler(prov).handle(payload, new IPayloadContext.Client(context));
            });
        });
    }

    public static @Nullable Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static float getColorTicks() {
        return (ticks + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false)) / 0.5F;
    }

    public static class StackStorage {
        public static ItemStack hoveredItem = ItemStack.EMPTY;
    }
}
