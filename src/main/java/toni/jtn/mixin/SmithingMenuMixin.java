package toni.jtn.mixin;


import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import toni.jtn.content.runes.ReactiveSmithingRecipe;

@Mixin(value = SmithingMenu.class, remap = false)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    public SmithingMenuMixin(MenuType<?> p_39773_, int p_39774_, Inventory p_39775_, ContainerLevelAccess p_39776_) {
        super(p_39773_, p_39774_, p_39775_, p_39776_);
    }

    @Shadow
    @Nullable
    private RecipeHolder<SmithingRecipe> selectedRecipe;

    @Inject(at = @At("HEAD"), method = "onTake")
    protected void onTake(Player player, ItemStack stack, CallbackInfo ci) {
        if (this.selectedRecipe.value() instanceof ReactiveSmithingRecipe ext) {
            ext.onCraft(this.inputSlots, player, stack);
        }
    }
}
