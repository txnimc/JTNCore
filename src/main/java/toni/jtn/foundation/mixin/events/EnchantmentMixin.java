package toni.jtn.foundation.mixin.events;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(at = @At("HEAD"), method = "getMaxLevel", cancellable = true)
    private void getMaxLevel(CallbackInfoReturnable<Integer> info) {
        int maximumLevel = 10;
        info.setReturnValue(maximumLevel);
    }

//    @Inject(at = @At("HEAD"), method = "getFullname", cancellable = true)
//    private static void getName(Holder<Enchantment> enchantment, int level, CallbackInfoReturnable<Component> cir) {
//        if (level > 5) {
//            MutableComponent mutableText = ((Enchantment)enchantment.value()).description().copy();
//            if (enchantment.is(EnchantmentTags.CURSE)) {
//                ComponentUtils.mergeStyles(mutableText, Style.EMPTY.withColor(ChatFormatting.RED));
//            } else {
//                ComponentUtils.mergeStyles(mutableText, Style.EMPTY.withColor(ChatFormatting.GRAY));
//            }
//
//            if (level != 1 || ((Enchantment)enchantment.value()).getMaxLevel() != 1) {
//                if (!LimitlessEnchantments.SHOW_ACTUAL_NUMBERS_BOOLEAN)
//                    mutableText.append(ScreenTexts.SPACE).append(LimitlessEnchantments.convertToRoman(level));
//                else
//                    mutableText.append(ScreenTexts.SPACE).append(LimitlessEnchantments.convertToRoman(level) + " (" + level + ")");
//            }
//
//            info.setReturnValue(mutableText);
//        }
//    }
}