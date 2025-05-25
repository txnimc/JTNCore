package toni.jtn.content.runes.gem;

import toni.jtn.foundation.Registration;
import toni.jtn.foundation.Registration.LootCategories;

public class GemProvider {

    public static final GemClass ARMOR = new GemClass("armor", LootCategories.HELMET, LootCategories.CHESTPLATE, LootCategories.LEGGINGS, LootCategories.BOOTS);
    public static final GemClass LIGHT_WEAPON = new GemClass("light_weapon", LootCategories.MELEE_WEAPON, LootCategories.TRIDENT);
    public static final GemClass CORE_ARMOR = new GemClass("core_armor", LootCategories.CHESTPLATE, LootCategories.LEGGINGS);
    public static final GemClass RANGED_WEAPON = new GemClass("ranged_weapon", LootCategories.BOW, LootCategories.TRIDENT);
    public static final GemClass LOWER_ARMOR = new GemClass("lower_armor", LootCategories.LEGGINGS, LootCategories.BOOTS);
    public static final GemClass WEAPONS = new GemClass("weapons", LootCategories.MELEE_WEAPON, LootCategories.TRIDENT, LootCategories.BOW);
    public static final GemClass WEAPON_OR_TOOL = new GemClass("weapon_or_tool", LootCategories.MELEE_WEAPON, LootCategories.TRIDENT, LootCategories.BOW, LootCategories.BREAKER);
    public static final GemClass NON_TRIDENT_WEAPONS = new GemClass("weapons", LootCategories.MELEE_WEAPON, LootCategories.BOW);


    public static void generate() {

    }
}
