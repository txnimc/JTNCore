package toni.jtn.foundation.mixin;

import com.ibm.icu.impl.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toni.jtn.foundation.accessors.PseudoAccessorItemStack;

import javax.management.Attribute;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Mixin(value = PotionContents.class)
public class PotionUtilsMixin {

//    @Unique
//    private static ItemStack stackActingOn;
//
//    @Inject(method = "addPotionTooltip(Ljava/lang/Iterable;Ljava/util/function/Consumer;FF)V", at = @At("HEAD"))
//    private static void setActingStack(Iterable<MobEffectInstance> effects, Consumer<Component> tooltipAdder, float durationFactor, float ticksPerSecond, CallbackInfo ci) {
//        stackActingOn = stack;
//    }
//
//    @Inject(method = "addPotionTooltip(Ljava/lang/Iterable;Ljava/util/function/Consumer;FF)V", at = @At("RETURN"))
//    private static void clearActingStack(Iterable<MobEffectInstance> effects, Consumer<Component> tooltipAdder, float durationFactor, float ticksPerSecond, CallbackInfo ci) {
//        stackActingOn = null;
//    }
//
//    @ModifyVariable(method = "addPotionTooltip(Ljava/util/List;Ljava/util/List;F)V", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 1, shift = At.Shift.BEFORE), ordinal = 2)
//    private static List<Pair<Attribute, AttributeModifier>> overrideAttributeTooltips(List<Pair<Attribute, AttributeModifier>> attributes, List<MobEffectInstance> mobEffects) {
//        if(stackActingOn != null) {
//            ((PseudoAccessorItemStack) (Object) stackActingOn).quark$capturePotionAttributes(attributes);
//            return Collections.emptyList();
//        }
//        return attributes;
//    }
}