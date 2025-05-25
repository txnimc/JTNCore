package toni.jtn;

import com.mojang.datafixers.util.Either;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.jetbrains.annotations.Nullable;
import toni.jtn.content.runes.SocketHelper;
import toni.jtn.content.runes.SocketTooltipRenderer;
import toni.jtn.content.runes.TooltipHandler;
import toni.jtn.foundation.Registration;
import toni.jtn.foundation.events.ModifyComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JTNClient implements ClientModInitializer {
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

            return null;
        });

        ScreenEvents.BEFORE_INIT.register(((minecraft, screen, i, i1) -> {
            if (screen instanceof AbstractContainerScreen<?> containerScreen) {
                ScreenEvents.afterRender(containerScreen).register(TooltipHandler::renderContainerScreen);
            }
        }));

        ModifyComponents.MODIFY_COMPONENTS.register(TooltipHandler::modifyComponents);
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
