package toni.jtn.content.runes.util;

//import dev.shadowsoffire.jtn.loot.LootCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import toni.jtn.content.runes.LootCategory;

public class ApothSmithingRecipe extends SmithingTransformRecipe {

    public static final int TEMPLATE = 0, BASE = 1, ADDITION = 2;

    public ApothSmithingRecipe(Ingredient pBase, Ingredient pAddition, ItemStack pResult) {
        super(Ingredient.EMPTY, pBase, pAddition, pResult);
    }

    @Override
    public boolean isBaseIngredient(ItemStack pStack) {
        return !LootCategory.forItem(pStack).isNone();
    }
}
