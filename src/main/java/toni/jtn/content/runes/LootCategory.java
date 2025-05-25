package toni.jtn.content.runes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;


import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.event.registry.RegistryIdRemapCallback;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;

import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import toni.jtn.JTN;
import toni.jtn.foundation.Registration;
import toni.jtn.foundation.Registration.LootCategories;
import toni.jtn.foundation.codec.JTNCodecs;

public final class LootCategory {

    public static final Codec<LootCategory> CODEC = Codec.lazyInitialized(LootCategory::legacyResolverCodec);
    public static final Codec<LootCategory> OPTIONAL_CODEC = Codec.lazyInitialized(Registration.BuiltInRegs.LOOT_CATEGORY::byNameCodec);
    public static final Codec<Set<LootCategory>> SET_CODEC = JTNCodecs.setOf(CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, LootCategory> STREAM_CODEC = ByteBufCodecs.registry(Registration.BuiltInRegs.LOOT_CATEGORY.key());

    private static List<LootCategory> sortedCategories = new ArrayList<>();

    private final Predicate<ItemStack> validator;
    //private final EntitySlotGroup slots;
    private final int priority;

    @Nullable
    private String descId;

    public LootCategory(Predicate<ItemStack> validator, /* EntitySlotGroup slots, */ int priority) {
        this.validator = Preconditions.checkNotNull(validator);
        //this.slots = Preconditions.checkNotNull(slots);
        this.priority = priority;
    }

    public LootCategory(Predicate<ItemStack> validator /* , EntitySlotGroup slots */) {
        this(validator, 1000);
    }

    public String getDescId() {
        return this.getOrCreateDescriptionId();
    }

    public String getDescIdPlural() {
        return this.getDescId() + ".plural";
    }

    public ResourceLocation getKey() {
        return Registration.BuiltInRegs.LOOT_CATEGORY.getKey(this);
    }

    public int priority() {
        return this.priority;
    }

//    /**
//     * Returns the relevant equipment slot for this items.
//     * The passed items should be of the type this category represents.
//     */
//    public EntitySlotGroup getSlots() {
//        return this.slots;
//    }

    public boolean isValid(ItemStack stack) {
        return this.validator.test(stack);
    }

    @Deprecated(forRemoval = true)
    public boolean isArmor() {
        return this == LootCategories.HELMET || this == LootCategories.CHESTPLATE || this == LootCategories.LEGGINGS || this == LootCategories.BOOTS;
    }

    @Deprecated(forRemoval = true)
    public boolean isBreaker() {
        return this == LootCategories.BREAKER;
    }

    @Deprecated(forRemoval = true)
    public boolean isRanged() {
        return this == LootCategories.BOW || this == LootCategories.TRIDENT;
    }

    @Deprecated(forRemoval = true)
    public boolean isDefensive() {
        return this.isArmor() || this == LootCategories.SHIELD;
    }

    @Deprecated(forRemoval = true)
    public boolean isMelee() {
        return this == LootCategories.MELEE_WEAPON || this == LootCategories.TRIDENT;
    }

    @Deprecated(forRemoval = true)
    public boolean isMeleeOrShield() {
        return this.isMelee() || this == LootCategories.SHIELD;
    }

    public boolean isNone() {
        return this == LootCategories.NONE;
    }

    @Override
    public String toString() {
        return String.format("LootCategory[%s]", this.getKey());
    }

    protected String getOrCreateDescriptionId() {
        if (this.descId == null) {
            this.descId = Util.makeDescriptionId("loot_category", this.getKey());
        }

        return this.descId;
    }

    public static <T> MapCodec<Map<LootCategory, T>> mapCodec(Codec<T> codec) {
        return Codec.simpleMap(LootCategory.CODEC, codec, Registration.BuiltInRegs.LOOT_CATEGORY::keys);
    }

    /**
     * Determines the loot category for an items, by iterating all the categories and selecting the first matching one.
     * <p>
     * TODO: Cache this result as a CachedObject sensitive to all component changes.
     *
     * @param stack The items to find the category for.
     * @return The first valid loot category, or {@link LootCategories#NONE} if no categories were valid.
     */
    public static LootCategory forItem(ItemStack stack) {
        if (sortedCategories.isEmpty()) {
            throw new UnsupportedOperationException("Attempted to resolve the loot category for an items before loot categories were registered!");
        }

        if (stack.isEmpty()) {
            return LootCategories.NONE;
        }
//
//        LootCategory override = BuiltInRegistries.ITEM.getData(DataMaps.LOOT_CATEGORY_OVERRIDES, stack.getItemHolder().getKey());
//        if (override != null) {
//            return override;
//        }

        for (LootCategory c : sortedCategories) {
            if (c.isValid(stack)) {
                return c;
            }
        }
        return LootCategories.NONE;
    }

    /**
     * Legacy resolver codec to assist with backwards compat.
     * <p>
     * Accepts a string as "jtn:path" instead of discarding it.
     */
    @Deprecated(forRemoval = true)
    private static Codec<LootCategory> legacyResolverCodec() {
        return Codec.either(
                Codec.stringResolver(ResourceLocation::getPath, LootCategory::readLocWithApothNamespace),
                ResourceLocation.CODEC)
            .xmap(Either::unwrap, Either::right)
            .xmap(Registration.BuiltInRegs.LOOT_CATEGORY::get, Registration.BuiltInRegs.LOOT_CATEGORY::getKey)
            .validate(
                cat -> cat == Registration.LootCategories.NONE
                    ? DataResult.error(() -> "Loot Category must not be jtn:none")
                    : DataResult.success(cat));
    }

    @Nullable
    private static ResourceLocation readLocWithApothNamespace(String path) {
        try {
            return path.contains(":") ? ResourceLocation.parse(path) : JTN.location(path);
        }
        catch (ResourceLocationException resourcelocationexception) {
            return null;
        }
    }

    @ApiStatus.Internal
    public static class Inner {
        public static RegistryEntryAddedCallback<LootCategory> rebuildSortedValueList() {
            return (i, rs, entry) -> {
                var list = new ArrayList<LootCategory>();
                for (LootCategory cat : Registration.BuiltInRegs.LOOT_CATEGORY) {
                    list.add(cat);
                }
                Collections.sort(list, Comparator.comparing(LootCategory::priority));
                LootCategory.sortedCategories = list;
            };
        }
    }
}
