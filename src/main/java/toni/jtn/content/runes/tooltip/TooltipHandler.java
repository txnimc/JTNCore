package toni.jtn.content.runes.tooltip;

import com.mojang.datafixers.util.Either;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.StringUtil;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import toni.jtn.JTN;
import toni.jtn.JTNClient;
import toni.jtn.content.runes.SocketHelper;
import toni.jtn.foundation.Registration;
import toni.jtn.foundation.events.ModifyComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TooltipHandler {
    private static final Component GEM_SOCKET_MARKER = Component.literal("JTN_SOCKET_MARKER");

    public static void tooltips(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> components) {
        JTNClient.StackStorage.hoveredItem = stack;
        int sockets = SocketHelper.getSockets(stack);
        if (sockets > 0) {
            components.add(GEM_SOCKET_MARKER.copy());
        }
    }

    public static void modifyComponents(ModifyComponents.ModifyComponentsEvent e) {
        int sockets = SocketHelper.getSockets(e.stack);
        if (sockets == 0) return;
        List<Either<FormattedText, TooltipComponent>> list = e.tooltipElements;
        int rmvIdx = -1;
        for (int i = 0; i < list.size(); i++) {
            Optional<FormattedText> o = list.get(i).left();
            if (o.isPresent() && o.get() instanceof Component comp && comp.getContents() instanceof PlainTextContents.LiteralContents tc) {
                if (GEM_SOCKET_MARKER.getString().equals(tc.text())) {
                    rmvIdx = i;
                    list.remove(i);
                    break;
                }
            }
        }
        if (rmvIdx == -1) return;
        e.tooltipElements.add(rmvIdx, Either.right(new SocketTooltipRenderer.SocketComponent(e.stack, SocketHelper.getGems(e.stack))));
    }


    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font font) {
        List<Either<FormattedText, TooltipComponent>> elements = textElements.stream()
            .map((Function<FormattedText, Either<FormattedText, TooltipComponent>>) Either::left)
            .collect(Collectors.toCollection(ArrayList::new));

        itemComponent.ifPresent(c -> elements.add(1, Either.right(c)));

        var event = new ModifyComponents.ModifyComponentsEvent(stack, screenWidth, screenHeight, elements, -1);
        ModifyComponents.MODIFY_COMPONENTS.invoker().modifyComponents(event);
        if (event.isCanceled())
            return List.of();

        if (!Screen.hasShiftDown()) {
            boolean removed = event.tooltipElements.removeIf(elem -> {
                if (!elem.left().isPresent())
                    return false;

                var left = (MutableComponent) elem.left().get();
                if (left == null)
                    return false;

                if (left.getStyle().getColor() != null && left.getStyle().getColor().getValue() == 43520 && left.getContents() instanceof PlainTextContents.LiteralContents tc && tc.text() == " ")
                    return true;

                if (left.getContents() instanceof TranslatableContents trans && trans.getKey().equals("item.modifiers.mainhand"))
                    return true;

                if (left.getContents() instanceof TranslatableContents trans && trans.getKey().equals("attribute.modifier.plus.0"))
                    return true;

                if (left.getContents() instanceof PlainTextContents contents && left.getSiblings().isEmpty() &&StringUtil.isNullOrEmpty(contents.text()))
                    return true;

                return false;
            });
        }

        // text wrapping
        int tooltipTextWidth = event.tooltipElements.stream()
            .mapToInt(either -> either.map(font::width, component -> 0))
            .max()
            .orElse(0);

        boolean needsWrap = false;

        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) { // if the tooltip doesn't fit on the screen
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }
        if (event.maxWidth > 0 && tooltipTextWidth > event.maxWidth) {
            tooltipTextWidth = event.maxWidth;
            needsWrap = true;
        }

        int tooltipTextWidthF = tooltipTextWidth;
        if (needsWrap) {
            return event.tooltipElements.stream()
                .flatMap(either -> either.map(
                    text -> splitLine(text, font, tooltipTextWidthF),
                    component -> Stream.of(ClientTooltipComponent.create(component))
                ))
                .toList();
        }
        return event.tooltipElements.stream()
            .map(either -> either.map(
                text -> ClientTooltipComponent.create(text instanceof Component ? ((Component) text).getVisualOrderText() : Language.getInstance().getVisualOrder(text)),
                ClientTooltipComponent::create
            ))
            .toList();
    }

    private static Stream<ClientTooltipComponent> splitLine(FormattedText text, Font font, int maxWidth) {
        if (text instanceof Component component && component.getString().isEmpty()) {
            return Stream.of(component.getVisualOrderText()).map(ClientTooltipComponent::create);
        }
        return font.split(text, maxWidth).stream().map(ClientTooltipComponent::create);
    }


    public static void renderContainerScreen(Screen ins, GuiGraphics graphics, int x, int y, float v) {
        var screen = (AbstractContainerScreen<?>) ins;

        ItemStack carried = screen.getMenu().getCarried();
        ItemStack hover = screen.hoveredSlot == null ? ItemStack.EMPTY : screen.hoveredSlot.getItem();
        if (carried.is(Registration.Items.GEM.value()) && SocketHelper.canSocketGemInItem(hover, carried)) {
            List<Component> tooltip = new ArrayList<>();
            // We want the hovered item's name to be white by default, so we need to wrap it in a component specifying white.
            Component itemName = Component.translatable("%s", hover.getHoverName()).withStyle(ChatFormatting.WHITE);
            tooltip.add(JTN.lang("misc", "right_click_to_socket", carried.getHoverName(), itemName).withStyle(ChatFormatting.GRAY));
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 400);
            graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, x, y);
            graphics.pose().popPose();
        }
    }

}
