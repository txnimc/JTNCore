package toni.jtn.mixin;

import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;

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