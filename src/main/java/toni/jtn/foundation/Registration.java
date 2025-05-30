package toni.jtn.foundation;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.AbstractSkullBlock;
import toni.jtn.JTN;
import toni.jtn.content.runes.AddSocketsRecipe;
import toni.jtn.content.runes.LootCategory;
import toni.jtn.content.runes.SocketingRecipe;
import toni.jtn.content.runes.WithdrawalRecipe;
import toni.jtn.content.runes.gem.Gem;
import toni.jtn.content.runes.gem.GemItem;
import toni.jtn.content.runes.gem.GemRegistry;
import toni.jtn.content.runes.gem.Purity;
import toni.jtn.foundation.items.TooltipItem;
import toni.jtn.foundation.registry.DynamicHolder;
import toni.jtn.foundation.codec.SingletonRecipeSerializer;

import java.util.function.Predicate;

public class Registration {
    private static final RegistryHelper R = new RegistryHelper();



    public static final class BuiltInRegs {

        public static final Registry<LootCategory> LOOT_CATEGORY = R.registry("loot_category", LootCategory.Inner.rebuildSortedValueList(), null);

        private static void bootstrap() {}
    }

    public static final class Items {
        public static final Holder<GemItem> GEM = R.item("gem", GemItem::new, p -> { });

        public static final Holder<Item> SIGIL_OF_SOCKETING = R.item("sigil_of_socketing", TooltipItem::new, p -> p.rarity(Rarity.UNCOMMON));

        public static final Holder<Item> SIGIL_OF_WITHDRAWAL = R.item("sigil_of_withdrawal", TooltipItem::new, p -> p.rarity(Rarity.UNCOMMON));


        public static final CreativeModeTab JTN_CREATIVE_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(SIGIL_OF_SOCKETING))
            .title(Component.literal("Journey to Niflheim"))
            .displayItems((context, entries) -> {
                entries.accept(SIGIL_OF_SOCKETING.value());
                entries.accept(SIGIL_OF_WITHDRAWAL.value());
                GEM.value().fillItemCategory(entries);
            })
            .build();

        private static void bootstrap() {
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, JTN.location("item_group"), JTN_CREATIVE_TAB);
        }
    }


    public static final class Components {

        public static final DataComponentType<Integer> SOCKETS = R.component("sockets", b -> b.persistent(Codec.intRange(0, 16)).networkSynchronized(ByteBufCodecs.VAR_INT));

        public static final DataComponentType<ItemContainerContents> SOCKETED_GEMS = R.component("socketed_gems", b -> b.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));

        public static final DataComponentType<DynamicHolder<Gem>> GEM = R.component("gem", b -> b.persistent(GemRegistry.INSTANCE.holderCodec()).networkSynchronized(GemRegistry.INSTANCE.holderStreamCodec()));

        public static final DataComponentType<Purity> PURITY = R.component("purity", b -> b.persistent(Purity.CODEC).networkSynchronized(Purity.STREAM_CODEC));

        private static void bootstrap() {}
    }

    public static final class RecipeSerializers {
        public static final Holder<RecipeSerializer<WithdrawalRecipe>> WITHDRAWAL = R.recipeSerializer("withdrawal", () -> new SingletonRecipeSerializer<>(WithdrawalRecipe::new));
        public static final Holder<RecipeSerializer<SocketingRecipe>> SOCKETING = R.recipeSerializer("socketing", () -> new SingletonRecipeSerializer<>(SocketingRecipe::new));
        public static final Holder<RecipeSerializer<AddSocketsRecipe>> ADD_SOCKETS = R.recipeSerializer("add_sockets", () -> AddSocketsRecipe.Serializer.INSTANCE);

        private static void bootstrap() {}
    }

    public static final class LootCategories {
        public static final LootCategory BOW = register("bow", s -> s.getItem() instanceof BowItem || s.getItem() instanceof CrossbowItem);
        public static final LootCategory BREAKER = register("breaker", s -> s.has(DataComponents.TOOL));
        public static final LootCategory HELMET = register("helmet", armorSlot(EquipmentSlot.HEAD));
        public static final LootCategory CHESTPLATE = register("chestplate", armorSlot(EquipmentSlot.CHEST));
        public static final LootCategory LEGGINGS = register("leggings", armorSlot(EquipmentSlot.LEGS));
        public static final LootCategory BOOTS = register("boots", armorSlot(EquipmentSlot.FEET));
        public static final LootCategory SHIELD = register("shield", s -> s.getItem() instanceof ShieldItem);
        public static final LootCategory TRIDENT = register("trident", s -> s.getItem() instanceof TridentItem);
        public static final LootCategory MELEE_WEAPON = register("melee_weapon", s -> getDefaultModifiers(s).compute(1, EquipmentSlot.MAINHAND) > 1, 2000);
        public static final LootCategory SHEARS = register("shears", s -> s.getItem() instanceof ShearsItem, 2500);
        public static final LootCategory ANY = register("any", Predicates.alwaysTrue(), Integer.MAX_VALUE);
        public static final LootCategory NONE = register("none", Predicates.alwaysFalse(), Integer.MAX_VALUE);

        private static LootCategory register(String path, Predicate<ItemStack> filter, int priority) {
            var loot = new LootCategory(filter, priority);

            Registry.register(
                BuiltInRegs.LOOT_CATEGORY,
                JTN.location(path),
                loot);

            return loot;
        }

        private static LootCategory register(String path, Predicate<ItemStack> filter) {
            return register(path, filter, 1000);
        }

        private static Predicate<ItemStack> armorSlot(EquipmentSlot slot) {
            return stack -> {
                if (stack.is(net.minecraft.world.item.Items.CARVED_PUMPKIN) || stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof AbstractSkullBlock) {
                    return false;
                }

                EquipmentSlot itemSlot = null;
                Equipable equipable = Equipable.get(stack);
                if (equipable != null) {
                    itemSlot = equipable.getEquipmentSlot();
                }

                return itemSlot == slot;
            };
        }

        private static ItemAttributeModifiers getDefaultModifiers(ItemStack stack) {
            return stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, stack.getItem().getDefaultAttributeModifiers());
        }

        private static void bootstrap() {}

    }



    public static void bootstrap() {
        //bus.register(R);

        BuiltInRegs.bootstrap();
//            Attachments.bootstrap();
        Components.bootstrap();
//            Blocks.bootstrap();
        Items.bootstrap();
//            Tiles.bootstrap();
//            Menus.bootstrap();
//            Tabs.bootstrap();
//            Sounds.bootstrap();
//            Triggers.bootstrap();
//            Features.bootstrap();
//            Ingredients.bootstrap();
//            RecipeTypes.bootstrap();
//            LootModifiers.bootstrap();
//            LootConditions.bootstrap();
//            LootPoolEntries.bootstrap();
        RecipeSerializers.bootstrap();
//            ItemSubPredicates.bootstrap();
//            EntitySubPredicates.bootstrap();
//            Stats.bootstrap();
//            Particles.bootstrap();
        LootCategories.bootstrap();
//            DataMaps.bootstrap();

    }
}
