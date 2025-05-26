package toni.jtn.foundation.mixin.events.enchantment;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.jtn.foundation.events.util.EventFactory;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getItemEnchantmentLevel", at = @At("RETURN"), cancellable = true)
    private static void initializeEnchLevelEvent(Holder<Enchantment> enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir, @Local ItemEnchantments itemEnchantments){
        if (stack.isEmpty()) return;
        int result = EventFactory.getEnchantmentLevelSpecific(cir.getReturnValue(), stack, enchantment.value());
        cir.setReturnValue(result);
    }


    @ModifyVariable(
        method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;)V",
        at = @At(value = "STORE", opcode = Opcodes.ASTORE))
    private static ItemEnchantments onRunIteration(ItemEnchantments itemEnchantments, @Local(argsOnly = true) ItemStack stack) {
        if (stack.isEmpty()) return itemEnchantments;
        var mutable = new ItemEnchantments.Mutable(itemEnchantments);
        return EventFactory.modifyEnchantments(mutable, stack);
    }

    @ModifyVariable(
        method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V",
        at = @At(value = "STORE", opcode = Opcodes.ASTORE))
    private static ItemEnchantments onRunIteration2(ItemEnchantments itemEnchantments, @Local(argsOnly = true) ItemStack stack) {
        if (stack.isEmpty()) return itemEnchantments;
        var mutable = new ItemEnchantments.Mutable(itemEnchantments);
        return EventFactory.modifyEnchantments(mutable, stack);
    }

}
