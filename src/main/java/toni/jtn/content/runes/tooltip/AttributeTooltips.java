package toni.jtn.content.runes.tooltip;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import toni.jtn.JTN;
import toni.jtn.JTNClient;
import toni.jtn.content.runes.SocketedGems;
import toni.jtn.foundation.accessors.PseudoAccessorItemStack;
import toni.jtn.foundation.events.ModifyComponents;

import java.util.*;

/**
 * @author WireSegal
 *         Created at 10:34 AM on 9/1/19.
 */
public class AttributeTooltips {

    public static final ResourceLocation TEXTURE_UPGRADE = JTN.location("textures/attribute/upgrade.png");
    public static final ResourceLocation TEXTURE_DOWNGRADE = JTN.location("textures/attribute/downgrade.png");

    private static final Map<ResourceLocation, AttributeIconEntry> attributes = new HashMap<>();

    public static void receiveAttributes(Map<String, AttributeIconEntry> map) {
        attributes.clear();
        for(Map.Entry<String, AttributeIconEntry> entry : map.entrySet()) {
            attributes.put(ResourceLocation.parse(entry.getKey()), entry.getValue());
        }
    }

    @Nullable
    private static AttributeIconEntry getIconForAttribute(Attribute attribute) {
        ResourceLocation loc = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        if(loc != null)
            return attributes.get(loc);
        return null;
    }

    private static MutableComponent format(Holder<Attribute> attribute, double value, AttributeDisplayType displayType) {
        switch(displayType) {
            case DIFFERENCE -> {
                return Component.literal((value > 0 ? "+" : "") + ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format((value))
                    .formatted(value < 0 ? ChatFormatting.RED : ChatFormatting.WHITE));
            }
            case PERCENTAGE -> {
                return Component.literal((value > 0 ? "+" : "") + ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format((value * 100) + "%")
                    .formatted(value < 0 ? ChatFormatting.RED : ChatFormatting.WHITE));
            }
            case MULTIPLIER -> {
                AttributeSupplier supplier = DefaultAttributes.getSupplier(EntityType.PLAYER);
                double scaledValue = value / supplier.getBaseValue(attribute);
                return Component.literal(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(scaledValue) + "x")
                    .withStyle(scaledValue < 1 ? ChatFormatting.RED : ChatFormatting.WHITE);
            }
            default -> {
                return Component.literal(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(value))
                    .withStyle(value < 0 ? ChatFormatting.RED : ChatFormatting.WHITE);
            }
        }
    }

    public static void makeTooltip(ModifyComponents.ModifyComponentsEvent e) {
        var stack = e.stack;

        if(!Screen.hasShiftDown()) {
            Map<AttributeSlot, MutableComponent> attributeTooltips = Maps.newHashMap();

            boolean onlyInvalid = true;
            Multimap<Holder<Attribute>, AttributeModifier> baseCheck = null;
            boolean allAreSame = true;

            for(AttributeSlot slot : AttributeSlot.values()) {
                if(canShowAttributes(stack, slot)) {
                    Multimap<Holder<Attribute>, AttributeModifier> slotAttributes = getModifiers(stack, slot);

                    if(baseCheck == null)
                        baseCheck = slotAttributes;
                    else if(slot.hasCanonicalSlot() && allAreSame && !slotAttributes.equals(baseCheck))
                        allAreSame = false;

                    if(!slotAttributes.isEmpty() && !slot.hasCanonicalSlot())
                        allAreSame = false;

                    onlyInvalid = extractAttributeValues(stack, attributeTooltips, onlyInvalid, slot, slotAttributes);
                }
            }

            AttributeSlot primarySlot = getPrimarySlot(stack);

            int i = 1;
            for(AttributeSlot slot : AttributeSlot.values()) {
                if(attributeTooltips.containsKey(slot)) {
                    int tooltipSlot = (slot == primarySlot ? 1 : i);
                    e.tooltipElements.add(tooltipSlot, Either.right(new AttributeComponentRenderer.AttributeComponent(stack, slot)));
                    i++;

                    if(allAreSame)
                        break;
                }
            }
        }
    }

    private static Multimap<Holder<Attribute>, AttributeModifier> getModifiersOnEquipped(Player player, ItemStack stack, Multimap<Holder<Attribute>, AttributeModifier> attributes, AttributeSlot slot) {
        if(slot.hasCanonicalSlot()) {
            ItemStack equipped = player.getItemBySlot(slot.getCanonicalSlot());
            if(!equipped.equals(stack) && !equipped.isEmpty()) {
                equipped.getTooltipLines(Item.TooltipContext.of(player.level()), player, TooltipFlag.Default.NORMAL);
                return getModifiers(equipped, slot);
            }
        }
        return ImmutableMultimap.of();
    }

    private static Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack stack, AttributeSlot slot) {
        //var capturedModifiers = ((PseudoAccessorItemStack) (Object) stack).quark$getCapturedAttributes();

        Multimap<Holder<Attribute>, AttributeModifier> modifiers = ArrayListMultimap.create();
        if (!slot.hasCanonicalSlot())
            return modifiers;

        stack.forEachModifier(slot.getCanonicalSlot(), (attr, modifier) -> {
            modifiers.put(attr, modifier);
        });
//        if(capturedModifiers.containsKey(slot)) {
//            var map = capturedModifiers.get(slot);
//            if(slot == AttributeSlot.MAINHAND) {
//                if(!map.containsKey(Attributes.ATTACK_DAMAGE))
//                    map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(JTN.location("null"),  0, AttributeModifier.Operation.ADD_VALUE));
//
//                if(!map.containsKey(Attributes.ATTACK_SPEED) && map.containsKey(Attributes.ATTACK_DAMAGE))
//                    map.put(Attributes.ATTACK_SPEED, new AttributeModifier(JTN.location("null"),  0, AttributeModifier.Operation.ADD_VALUE));
//
//                if(!map.containsKey(Attributes.ATTACK_KNOCKBACK))
//                    map.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(JTN.location("null"),  0, AttributeModifier.Operation.ADD_VALUE));
//            }
//
//            return map;
//        }
        return modifiers;
    }

    private static boolean extractAttributeValues(ItemStack stack, Map<AttributeSlot, MutableComponent> attributeTooltips, boolean onlyInvalid, AttributeSlot slot, Multimap<Holder<Attribute>, AttributeModifier> slotAttributes) {
        boolean anyInvalid = false;
        for(Holder<Attribute> attr : slotAttributes.keySet()) {
            AttributeIconEntry entry = getIconForAttribute(attr.value());
            if(entry != null) {
                onlyInvalid = false;
                Minecraft mc = Minecraft.getInstance();
                double attributeValue = getAttribute(mc.player, slot, stack, slotAttributes, attr);
                if(attributeValue != 0) {
                    if(!attributeTooltips.containsKey(slot))
                        attributeTooltips.put(slot, Component.literal(""));
                    attributeTooltips.get(slot).append(format(attr, attributeValue, entry.displayTypes().get(slot)).getString()).append("/");
                }
            } else if(!anyInvalid) {
                anyInvalid = true;
                if(!attributeTooltips.containsKey(slot))
                    attributeTooltips.put(slot, Component.literal(""));
                attributeTooltips.get(slot).append("[+]");
            }
        }
        return onlyInvalid;
    }

    private static int renderAttribute(GuiGraphics guiGraphics, Holder<Attribute> attribute, AttributeSlot slot, int x, int y, ItemStack stack, Multimap<Holder<Attribute>, AttributeModifier> slotAttributes, Minecraft mc, boolean forceRenderIfZero, Multimap<Holder<Attribute>, AttributeModifier> equippedSlotAttributes, @Nullable Set<Holder<Attribute>> equippedAttrsToRender) {
        AttributeIconEntry entry = getIconForAttribute(attribute.value());
        if(entry != null) {
            if(equippedAttrsToRender != null)
                equippedAttrsToRender.remove(attribute);

            double value = getAttribute(mc.player, slot, stack, slotAttributes, attribute);
            if(value != 0 || forceRenderIfZero) {

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                guiGraphics.blit(entry.texture(), x, y, 0, 0, 9, 9, 9, 9);

                MutableComponent valueStr = format(attribute, value, entry.displayTypes().get(slot));

                if(slot.hasCanonicalSlot()) {
                    AttributeIconEntry.CompareType compareType = entry.comparison();
                    EquipmentSlot equipSlot = slot.getCanonicalSlot();

                    if(mc.player != null) {
                        ItemStack equipped = mc.player.getItemBySlot(equipSlot);
                        if(!equipped.equals(stack) && !equipped.isEmpty()) {
                            if(!equippedSlotAttributes.isEmpty()) {
                                double otherValue = getAttribute(mc.player, slot, equipped, equippedSlotAttributes, attribute);

                                ChatFormatting color = compareType.getColor(value, otherValue);

                                if(color != ChatFormatting.WHITE) {
                                    int xp = x - 2;
                                    int yp = y - 2;
                                    if(JTNClient.ticks % 20 < 10)
                                        yp++;

                                    guiGraphics.blit(color == ChatFormatting.RED ? TEXTURE_DOWNGRADE : TEXTURE_UPGRADE, xp, yp, 0, 0, 13, 13, 13, 13);
                                }

                                valueStr = valueStr.withStyle(color);
                            }
                        }
                    }
                }

                guiGraphics.drawString(mc.font, valueStr, x + 12, y + 1, -1);
                x += mc.font.width(valueStr) + 20;
            }
        }

        return x;
    }

    private static AttributeSlot getPrimarySlot(ItemStack stack) {
        if(stack.getItem() instanceof PotionItem || stack.getItem() instanceof TippedArrowItem)
            return AttributeSlot.POTION;
        return AttributeSlot.fromCanonicalSlot(EquipmentSlot.MAINHAND);
    }

    private static boolean canShowAttributes(ItemStack stack, AttributeSlot slot) {
        if(stack.isEmpty())
            return false;

        return true;
//        if(slot == AttributeSlot.POTION)
//            return (ItemNBTHelper.getInt(stack, "HideFlags", 0) & 32) == 0;
//
//        return (ItemNBTHelper.getInt(stack, "HideFlags", 0) & 2) == 0;
    }

    private static double getAttribute(Player player, AttributeSlot slot, ItemStack stack, Multimap<Holder<Attribute>, AttributeModifier> map, Holder<Attribute> key) {
        if(player == null) // apparently this can happen
            return 0;

        Collection<AttributeModifier> collection = map.get(key);
        if(collection.isEmpty())
            return 0;

        double value = 0;

        AttributeIconEntry entry = getIconForAttribute(key.value());
        if(entry == null)
            return 0;

        AttributeDisplayType displayType = entry.displayTypes().get(slot);

        if(displayType != AttributeDisplayType.PERCENTAGE) {
            if(slot != AttributeSlot.POTION || !key.equals(Attributes.ATTACK_DAMAGE)) { // ATTACK_DAMAGE
                AttributeInstance attribute = player.getAttribute(key);
                if(attribute != null)
                    value = attribute.getBaseValue();
            }
        }

        for(AttributeModifier modifier : collection) {
            if(modifier.operation() == AttributeModifier.Operation.ADD_VALUE)
                value += modifier.amount();
        }

        double rawValue = value;

        for(AttributeModifier modifier : collection) {
            if(modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                value += rawValue * modifier.amount();
        }

        for(AttributeModifier modifier : collection) {
            if(modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                value += value * modifier.amount();
        }

//        if(key.equals(Attributes.ATTACK_DAMAGE) && slot == AttributeSlot.MAINHAND)
//            value += EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
//        if(key.equals(Attributes.ATTACK_KNOCKBACK) && slot == AttributeSlot.MAINHAND)
//            value += Quark.ZETA.itemExtensions.get(stack).getEnchantmentLevelZeta(stack, Enchantments.KNOCKBACK);

        if(displayType == AttributeDisplayType.DIFFERENCE) {
            if(slot != AttributeSlot.POTION || !key.equals(Attributes.ATTACK_DAMAGE)) {
                AttributeInstance attribute = player.getAttribute(key);
                if(attribute != null)
                    value -= attribute.getBaseValue();
            }
        }

        return value;
    }

    public record AttributeComponentRenderer(ItemStack stack, AttributeSlot slot) implements ClientTooltipComponent {

        public static record AttributeComponent(ItemStack stack, AttributeSlot slot) implements TooltipComponent {}

        @Override
        public void renderImage(@NotNull Font font, int tooltipX, int tooltipY, @NotNull GuiGraphics guiGraphics) {
            PoseStack pose = guiGraphics.pose();

            if(!Screen.hasShiftDown()) {
                pose.pushPose();
                pose.translate(0, 0, 500);

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                Minecraft mc = Minecraft.getInstance();
                //fixme port 1.20 check if this even does anything
//				pose.translate(0F, 0F, mc.getItemRenderer().blitOffset);

                int y = tooltipY - 1;

                AttributeSlot primarySlot = getPrimarySlot(stack);
                boolean showSlots = false;
                int x = tooltipX;

                if(canShowAttributes(stack, slot)) {
                    Multimap<Holder<Attribute>, AttributeModifier> slotAttributes = getModifiers(stack, slot);
                    Multimap<Holder<Attribute>, AttributeModifier> presentOnEquipped = getModifiersOnEquipped(mc.player, stack, slotAttributes, slot);
                    Set<Holder<Attribute>> equippedAttrsToRender = new LinkedHashSet<>(presentOnEquipped.keySet());

                    for(Holder<Attribute> attr : slotAttributes.keySet()) {
                        if(getIconForAttribute(attr.value()) != null) {
                            if(slot != primarySlot) {
                                showSlots = true;
                                break;
                            }
                        }
                    }

                    boolean anyToRender = false;
                    for(Holder<Attribute> attr : slotAttributes.keySet()) {
                        double value = getAttribute(mc.player, slot, stack, slotAttributes, attr);
                        if(value != 0) {
                            anyToRender = true;
                            break;
                        }
                    }

                    if(anyToRender) {
                        if(showSlots) {
                            RenderSystem.setShader(GameRenderer::getPositionTexShader);
                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                            guiGraphics.blit(JTNClient.GENERAL_ICONS, x, y, 193 + slot.ordinal() * 9, 35, 9, 9, 256, 256);
                            x += 20;
                        }

                        for(Holder<Attribute> key : slotAttributes.keySet())
                            x = renderAttribute(guiGraphics, key, slot, x, y, stack, slotAttributes, mc, false, presentOnEquipped, equippedAttrsToRender);
                        for(Holder<Attribute> key : equippedAttrsToRender)
                            x = renderAttribute(guiGraphics, key, slot, x, y, stack, slotAttributes, mc, true, presentOnEquipped, null);

                        for(Holder<Attribute> key : slotAttributes.keys()) {
                            if(getIconForAttribute(key.value()) == null) {
                                guiGraphics.drawString(font, "[+]", x + 1, y + 1, 0xFFFF55, true);
                                break;
                            }
                        }
                    }
                }

                pose.popPose();

            }
        }

        @Override
        public int getHeight() {
            return 10;
        }

        @Override
        public int getWidth(@NotNull Font font) {
            int width = 0;

            if(canShowAttributes(stack, slot)) {
                Minecraft mc = Minecraft.getInstance();
                Multimap<Holder<Attribute>, AttributeModifier> slotAttributes = getModifiers(stack, slot);
                Multimap<Holder<Attribute>, AttributeModifier> presentOnEquipped = getModifiersOnEquipped(mc.player, stack, slotAttributes, slot);
                Set<Holder<Attribute>> equippedAttrsToRender = new LinkedHashSet<>(presentOnEquipped.keySet());

                AttributeSlot primarySlot = getPrimarySlot(stack);
                boolean showSlots = false;

                for(Holder<Attribute> attr : slotAttributes.keySet()) {
                    if(getIconForAttribute(attr.value()) != null) {
                        if(slot != primarySlot) {
                            showSlots = true;
                            break;
                        }
                    }
                }

                boolean anyToRender = false;
                for(Holder<Attribute> attr : slotAttributes.keySet()) {
                    double value = getAttribute(mc.player, slot, stack, slotAttributes, attr);
                    if(value != 0) {
                        anyToRender = true;
                        break;
                    }
                }

                if(anyToRender) {
                    if(showSlots)
                        width += 20;

                    for(Holder<Attribute> key : slotAttributes.keySet()) {
                        AttributeIconEntry icons = getIconForAttribute(key.value());
                        if(icons != null) {
                            equippedAttrsToRender.remove(key);

                            double value = getAttribute(mc.player, slot, stack, slotAttributes, key);

                            if(value != 0) {

                                MutableComponent valueStr = format(key, value, icons.displayTypes().get(slot));
                                width += font.width(valueStr) + 20;
                            }
                        }
                    }

                    for(Holder<Attribute> key : equippedAttrsToRender) {
                        AttributeIconEntry icons = getIconForAttribute(key.value());
                        if(icons != null) {
                            double value = getAttribute(mc.player, slot, stack, slotAttributes, key);
                            MutableComponent valueStr = format(key, value, icons.displayTypes().get(slot));
                            width += font.width(valueStr) + 20;
                        }
                    }

                    for(Holder<Attribute> key : slotAttributes.keys()) {
                        if(getIconForAttribute(key.value()) == null) {
                            width += font.width("[+]") + 8;
                            break;
                        }
                    }
                }
            }

            return width - 8;
        }

    }

}