package toni.jtn.foundation.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import toni.jtn.foundation.config.AllConfigs;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LanguageProvider extends FabricLanguageProvider {
    protected LanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        AllConfigs.generateTranslations(translationBuilder);

        GemProvider.gems.forEach(gem -> {
            translationBuilder.add("item.jtn.gem.jtn:" + gem.id(), gem.name());
            translationBuilder.add("item.jtn.gem.jtn:" + gem.id() + ".desc", gem.desc());
        });

        langEntries.forEach(translationBuilder::add);
    }

    @Override
    public String getName() {
        return "JTN Data Gen";
    }


    public Map<String, String> langEntries = Map.<String, String>ofEntries(
        Map.entry("item.jtn.sigil_of_socketing", "Sigil of Socketing"),
        Map.entry("item.jtn.sigil_of_socketing.desc", "Adds one socket to an item"),
        Map.entry("item.jtn.sigil_of_withdrawal", "Sigil of Withdrawal"),
        Map.entry("item.jtn.sigil_of_withdrawal.desc", "Removes all socketed gems from an item"),
        Map.entry("socket.jtn.empty", "Empty Rune Slot"),
        Map.entry("text.jtn.socketable_into", "Fits In:"),
        Map.entry("text.jtn.dot_prefix", "• %s"),
        Map.entry("text.jtn.when_socketed", "When Forged:"),
        Map.entry("text.jtn.when_socketed_in", "When Forged into:"),
        Map.entry("text.jtn.when_socketed_typed", "When Forged into %s:"),
        Map.entry("item.jtn.gem.cracked", "Cracked %s"),
        Map.entry("item.jtn.gem.chipped", "Chipped %s"),
        Map.entry("item.jtn.gem.flawed", "Lesser %s"),
        Map.entry("item.jtn.gem.normal", "%s"),
        Map.entry("item.jtn.gem.flawless", "Greater %s"),
        Map.entry("item.jtn.gem.perfect", "Flawless %s"),
        Map.entry("bonus.jtn:durability.desc", "Ignores %s%% of durability damage"),
        Map.entry("bonus.jtn:enchantment.raw", "%s %s"),
        Map.entry("bonus.jtn:enchantment.desc", "+%s %s to %s"),
        Map.entry("bonus.jtn:enchantment.desc.mustExist", "+%s %s to existing %s"),
        Map.entry("bonus.jtn:enchantment.desc.global", "+%s %s to all existing enchantments"),
        Map.entry("misc.jtn.level", "level"),
        Map.entry("misc.jtn.level.many", "levels"),
        Map.entry("loot_category.jtn.none", "Nothing"),
        Map.entry("loot_category.jtn.none.plural", "Nothing"),
        Map.entry("loot_category.jtn.bow", "Bow"),
        Map.entry("loot_category.jtn.bow.plural", "Bows"),
        Map.entry("loot_category.jtn.crossbow", "Crossbow"),
        Map.entry("loot_category.jtn.crossbow.plural", "Crossbows"),
        Map.entry("loot_category.jtn.breaker", "Mining Tool"),
        Map.entry("loot_category.jtn.breaker.plural", "Mining Tools"),
        Map.entry("loot_category.jtn.pickaxe", "Pickaxe"),
        Map.entry("loot_category.jtn.pickaxe.plural", "Pickaxes"),
        Map.entry("loot_category.jtn.shovel", "Shovel"),
        Map.entry("loot_category.jtn.shovel.plural", "Shovels"),
        Map.entry("loot_category.jtn.armor", "Armor"),
        Map.entry("loot_category.jtn.armor.plural", "Armor"),
        Map.entry("loot_category.jtn.shield", "Shield"),
        Map.entry("loot_category.jtn.shield.plural", "Shields"),
        Map.entry("loot_category.jtn.trident", "Trident"),
        Map.entry("loot_category.jtn.trident.plural", "Tridents"),
        Map.entry("loot_category.jtn.melee_weapon", "Melee Weapon"),
        Map.entry("loot_category.jtn.melee_weapon.plural", "Melee Weapons"),
        Map.entry("loot_category.jtn.helmet", "Helmet"),
        Map.entry("loot_category.jtn.helmet.plural", "Helmets"),
        Map.entry("loot_category.jtn.chestplate", "Chestplate"),
        Map.entry("loot_category.jtn.chestplate.plural", "Chestplates"),
        Map.entry("loot_category.jtn.leggings", "Leggings"),
        Map.entry("loot_category.jtn.leggings.plural", "Leggings"),
        Map.entry("loot_category.jtn.boots", "Boots"),
        Map.entry("loot_category.jtn.boots.plural", "Boots"),
        Map.entry("gem_class.melee_weapon", "Melee Weapons"),
        Map.entry("gem_class.ranged_weapon", "Ranged Weapons"),
        Map.entry("gem_class.light_weapon", "Light Weapons"),
        Map.entry("gem_class.core_armor", "Core Armor"),
        Map.entry("gem_class.lower_armor", "Lower Armor"),
        Map.entry("gem_class.armor", "Armor"),
        Map.entry("gem_class.breaker", "Mining Tools"),
        Map.entry("gem_class.pickaxe", "Pickaxes"),
        Map.entry("gem_class.light_ranged", "Light or Ranged Weapons"),
        Map.entry("gem_class.weapon_or_tool", "Weapons or Tools"),
        Map.entry("gem_class.weapons", "Weapons"),
        Map.entry("gem_class.helmet", "Helmets"),
        Map.entry("gem_class.chestplate", "Chestplates"),
        Map.entry("gem_class.leggings", "Leggings"),
        Map.entry("gem_class.boots", "Boots"),
        Map.entry("gem_class.shield", "Shields"),
        Map.entry("gem_class.bow", "Bows"),
        Map.entry("gem_class.trident", "Tridents"),
        Map.entry("gem_class.anything", "Anything"),
        Map.entry("gem_class.any", "Anything")
    );
}