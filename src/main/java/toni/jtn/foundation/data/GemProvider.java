package toni.jtn.foundation.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import toni.jtn.JTN;
import toni.jtn.content.runes.gem.Gem;
import toni.jtn.content.runes.gem.GemClass;
import toni.jtn.content.runes.gem.Purity;
import toni.jtn.content.runes.gem.bonus.EnchantmentBonus;
import toni.jtn.foundation.Registration.LootCategories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class GemProvider extends FabricCodecDataProvider<Gem> {

//    public static final GemClass ARMOR = new GemClass("armor", LootCategories.HELMET, LootCategories.CHESTPLATE, LootCategories.LEGGINGS, LootCategories.BOOTS);
//    public static final GemClass LIGHT_WEAPON = new GemClass("light_weapon", LootCategories.MELEE_WEAPON, LootCategories.TRIDENT);
//    public static final GemClass CORE_ARMOR = new GemClass("core_armor", LootCategories.CHESTPLATE, LootCategories.LEGGINGS);
//    public static final GemClass RANGED_WEAPON = new GemClass("ranged_weapon", LootCategories.BOW, LootCategories.TRIDENT);
//    public static final GemClass LOWER_ARMOR = new GemClass("lower_armor", LootCategories.LEGGINGS, LootCategories.BOOTS);
//    public static final GemClass WEAPONS = new GemClass("weapons", LootCategories.MELEE_WEAPON, LootCategories.TRIDENT, LootCategories.BOW);
//    public static final GemClass WEAPON_OR_TOOL = new GemClass("weapon_or_tool", LootCategories.MELEE_WEAPON, LootCategories.TRIDENT, LootCategories.BOW, LootCategories.BREAKER);
//    public static final GemClass NON_TRIDENT_WEAPONS = new GemClass("weapons", LootCategories.MELEE_WEAPON, LootCategories.BOW);

    public static List<GemDataEntry> gems = new ArrayList<>();

    protected GemProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture, PackOutput.Target outputType, String directoryName, Codec<Gem> codec) {
        super(dataOutput, registriesFuture, outputType, directoryName, codec);
    }


    public static void bootstrap() {
        addGem("unbreaking", "Rune of Reinforcement", "", (p, c) -> c
            .unique()
            .minPurity(Purity.CRACKED)
            .bonus(LootCategories.ANY, EnchantmentBonus.builder(p)
                .enchantment(Enchantments.UNBREAKING)
                .defaultValues()));

        addGem("efficiency", "Rune of the Earthbreaker", "", (p, c) -> c
            .unique()
            .minPurity(Purity.CRACKED)
            .bonus(LootCategories.BREAKER, EnchantmentBonus.builder(p)
                .enchantment(Enchantments.EFFICIENCY)
                .defaultValues()));

        addGem("fortune", "Rune of Prosperity", "", (p, c) -> c
            .unique()
            .minPurity(Purity.CRACKED)
            .bonus(LootCategories.BREAKER, EnchantmentBonus.builder(p)
                .enchantment(Enchantments.FORTUNE)
                .defaultValues()));

        addGem("looting", "Rune of Abundance", "", (p, c) -> c
            .unique()
            .minPurity(Purity.CRACKED)
            .bonus(LootCategories.MELEE_WEAPON, EnchantmentBonus.builder(p)
                .enchantment(Enchantments.LOOTING)
                .defaultValues()));

        addGem("sharpness", "Rune of Ferocity", "", (p, c) -> c
            .unique()
            .minPurity(Purity.CRACKED)
            .bonus(LootCategories.MELEE_WEAPON, EnchantmentBonus.builder(p)
                .enchantment(Enchantments.SHARPNESS)
                .defaultValues()));

        addGem("protection", "Rune of Fortitude", "", (p, c) -> c
            .unique()
            .minPurity(Purity.CRACKED)
            .bonus(LootCategories.CHESTPLATE, EnchantmentBonus.builder(p)
                .enchantment(Enchantments.PROTECTION)
                .defaultValues()));
    }


    @Override
    protected void configure(BiConsumer<ResourceLocation, Gem> biConsumer, HolderLookup.Provider provider) {
        for(var gemEntry : gems) {
            var builder = new Gem.Builder();
            gemEntry.config.apply(provider, builder);
            biConsumer.accept(JTN.location(gemEntry.id), builder.build());
        }
    }


    @Override
    public String getName() {
        return "";
    }

    private static void addGem(String id, String name, String desc, BiFunction<HolderLookup.Provider, Gem.Builder, Gem.Builder> config) {
        gems.add(new GemDataEntry(id, name, desc, config));
    }

    public static record GemDataEntry(String id, String name, String desc, BiFunction<HolderLookup.Provider, Gem.Builder, Gem.Builder> config) {}
}