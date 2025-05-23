package toni.jtn.content.runes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import toni.jtn.content.runes.util.ApothSmithingRecipe;
import toni.jtn.foundation.Registration;

/**
 * Handles the socketing of items in the Smithing Table.
 */
public class SocketingRecipe extends ApothSmithingRecipe {

    public SocketingRecipe() {
        super(Ingredient.EMPTY, Ingredient.of(Registration.Items.GEM.value()), ItemStack.EMPTY);
    }

    @Override
    public boolean matches(SmithingRecipeInput inv, Level pLevel) {
        ItemStack input = inv.getItem(BASE);
        ItemStack gemStack = inv.getItem(ADDITION);
        return SocketHelper.canSocketGemInItem(input, gemStack);
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput inv, HolderLookup.Provider regs) {
        ItemStack input = inv.getItem(BASE);
        ItemStack gemStack = inv.getItem(ADDITION);
        return SocketHelper.socketGemInItem(input, gemStack);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.RecipeSerializers.SOCKETING.value();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
