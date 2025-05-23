package toni.jtn.foundation.events;
import java.util.Optional;

import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired whenever the enchantment level of a particular items is requested for gameplay purposes.<br>
 * It is called from IItemStackExtension#getEnchantmentLevel(Enchantment) and IItemStackExtension#getAllEnchantments().
 * <p>
 * It is not fired for interactions with NBT, which means these changes will not reflect in the items tooltip.
 */
public class GetEnchantmentLevelEvent {
    /**
     * -- GETTER --
     *  Returns the items stack that is being queried against.
     */
    @Getter
    protected final ItemStack stack;
    /**
     * -- GETTER --
     *  Returns the mutable enchantment->level map.
     */
    @Getter
    protected final ItemEnchantments.Mutable enchantments;
    @Nullable
    protected final Holder<Enchantment> targetEnchant;
    /**
     * -- GETTER --
     *
     */
    @Getter
    protected final RegistryLookup<Enchantment> lookup;

    public GetEnchantmentLevelEvent(ItemStack stack, ItemEnchantments.Mutable enchantments, @Nullable Holder<Enchantment> targetEnchant, RegistryLookup<Enchantment> lookup) {
        this.stack = stack;
        this.enchantments = enchantments;
        this.targetEnchant = targetEnchant;
        this.lookup = lookup;
    }

    /**
     * This method returns the specific enchantment being queried from IItemStackExtension#getEnchantmentLevel(Enchantment).
     * <p>
     * If this is value is present, you only need to adjust the level of that enchantment.
     * <p>
     * If this value is null, then the event was fired from IItemStackExtension#getAllEnchantments() and all enchantments should be populated.
     *
     * @return The specific enchantment being queried, or null, if all enchantments are being requested.
     */
    @Nullable
    public Holder<Enchantment> getTargetEnchant() {
        return this.targetEnchant;
    }

    /**
     * Helper method around {@link #getTargetEnchant()} that checks if the target is the specified enchantment, or if the target is null.
     *
     * @param ench The enchantment to check.
     * @return If modifications to the passed enchantment are relevant for this event.
     * @see #getTargetEnchant() for more information about the target enchantment.
     */
    public boolean isTargetting(Holder<Enchantment> ench) {
        return this.targetEnchant == null || this.targetEnchant.is(ench);
    }

    /**
     * Helper method around {@link #getTargetEnchant()} that checks if the target is the specified enchantment, or if the target is null.
     *
     * @param ench The enchantment to check.
     * @return If modifications to the passed enchantment are relevant for this event.
     * @see #getTargetEnchant() for more information about the target enchantment.
     */
    public boolean isTargetting(ResourceKey<Enchantment> ench) {
        return this.targetEnchant == null || this.targetEnchant.is(ench);
    }

    /**
     * Attempts to resolve a {@link Holder.Reference} for a target enchantment.
     * Since enchantments are data, they are not guaranteed to exist.
     *
     * @param key The target resource key
     * @return If the holder was available, an Optional containing it; otherwise an empty Optional.
     */
    public Optional<Holder.Reference<Enchantment>> getHolder(ResourceKey<Enchantment> key) {
        return this.lookup.get(key);
    }

}