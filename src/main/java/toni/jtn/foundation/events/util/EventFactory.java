package toni.jtn.foundation.events.util;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import toni.jtn.foundation.events.GetEnchantmentLevelEvent;

import java.util.HashMap;
import java.util.Map;

public class EventFactory {
    public static int getEnchantmentLevelSpecific(int level, ItemStack stack, Enchantment ench) {
        var enchMap = new HashMap<Enchantment, Integer>();
        enchMap.put(ench, level);
        var eventResult = GetEnchantmentLevelEvent.GET_ENCHANTMENT_LEVEL.invoker().onEnchantRequest(enchMap, stack);
        return eventResult.get(ench);
    }

    public static Map<Enchantment, Integer> getEnchantmentLevel(Map<Enchantment, Integer> enchantments, ItemStack stack) {
        enchantments = new HashMap<>(enchantments);
        GetEnchantmentLevelEvent.GET_ENCHANTMENT_LEVEL.invoker().onEnchantRequest(enchantments, stack);
        return enchantments;
    }

    public static ItemEnchantments modifyEnchantments(ItemEnchantments.Mutable mutable, ItemStack stack) {
        var enchMap = new HashMap<Enchantment, Integer>();
        GetEnchantmentLevelEvent.GET_ENCHANTMENT_LEVEL.invoker().onEnchantRequest(enchMap, stack);
        for (var enchant : enchMap.entrySet()) {
            var holder = Holder.direct(enchant.getKey());
            var val = mutable.getLevel(holder);
            mutable.set(holder, val + enchant.getValue());
        }

        return mutable.toImmutable();
    }
}
