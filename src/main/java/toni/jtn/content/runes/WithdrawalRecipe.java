package toni.jtn.content.runes;

import toni.jtn.content.runes.gem.GemInstance;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import toni.jtn.content.runes.util.ApothSmithingRecipe;
import toni.jtn.foundation.Registration;

public class WithdrawalRecipe extends ApothSmithingRecipe implements ReactiveSmithingRecipe {

    public WithdrawalRecipe() {
        super(Ingredient.EMPTY, Ingredient.of(Registration.Items.SIGIL_OF_WITHDRAWAL.value()), ItemStack.EMPTY);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(SmithingRecipeInput inv, Level level) {
        ItemStack base = inv.getItem(BASE);
        ItemStack sigils = inv.getItem(ADDITION);
        return base.getCount() == 1 && sigils.is(Registration.Items.SIGIL_OF_WITHDRAWAL.value()) && SocketHelper.getGems(base).stream().anyMatch(GemInstance::isValid);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(SmithingRecipeInput inv, HolderLookup.Provider regs) {
        ItemStack out = inv.getItem(BASE).copy();
        if (out.isEmpty()) {
            return ItemStack.EMPTY;
        }
        SocketHelper.setGems(out, SocketedGems.EMPTY);
        return out;
    }

    @Override
    public void onCraft(Container inv, Player player, ItemStack output) {
        ItemStack base = inv.getItem(BASE);
        SocketedGems gems = SocketHelper.getGems(base);
        for (GemInstance gem : gems) {
            ItemStack stack = gem.gemStack();
            if (!stack.isEmpty()) {
                if (!player.addItem(stack)) {
                    Block.popResource(player.level(), player.blockPosition(), stack);
                }
            }
        }
        SocketHelper.setGems(base, SocketedGems.EMPTY); // shouldn't be necessary, since base will be deleted, but we do this anyway to safeguard against infinite loops.
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.RecipeSerializers.WITHDRAWAL.value();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

}
