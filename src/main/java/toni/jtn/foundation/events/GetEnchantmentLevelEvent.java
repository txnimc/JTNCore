package toni.jtn.foundation.events;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired whenever the enchantment level of a particular item is requested for gameplay purposes.<br>
 * It is called from EnchantmentHelper#getEnchantmentLevel(Enchantment, LivingEntity) (Enchantment) and EnchantmentHelper#getEnchantments(ItemStack).
 * <p>
 * It is not fired for interactions with NBT, which means these changes will not reflect in the item tooltip.
 * <p>
 * This event is not cancellable.<br>
 * This event does not have a result.
 */
public interface GetEnchantmentLevelEvent  {


    Event<GetEnchantmentLevelEvent> GET_ENCHANTMENT_LEVEL = EventFactory.createArrayBacked(GetEnchantmentLevelEvent.class,
        (listeners) -> (enchantments, stack) -> {
            for (GetEnchantmentLevelEvent listener : listeners) {
                Map<Enchantment, Integer> result = listener.onEnchantRequest(enchantments, stack);
                return result;
            }

            return enchantments;
        });

    Map<Enchantment, Integer> onEnchantRequest(Map<Enchantment, Integer> enchantments, ItemStack stack);
}