package toni.jtn.content.runes.gem;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import toni.jtn.JTN;
import toni.jtn.foundation.Registration;
import toni.jtn.foundation.events.AttributeTooltipContext;
import toni.jtn.foundation.registry.DynamicHolder;

public class GemItem extends Item {

    public static final String HAS_REFRESHED = "has_refreshed";
    public static final String UUID_ARRAY = "uuids";
    public static final String GEM = "gem";

    public GemItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        UnsocketedGem inst = UnsocketedGem.of(stack);
        if (!inst.isValid()) {
            tooltip.add(Component.literal("Errored gem with no bonus!").withStyle(ChatFormatting.GRAY));
            return;
        }
        inst.addInformation(tooltip::add, AttributeTooltipContext.of(JTN.getClientPlayer(), ctx, flag));
    }

    @Override
    public Component getName(ItemStack pStack) {
        UnsocketedGem inst = UnsocketedGem.of(pStack);
        if (!inst.isValid()) {
            return super.getName(pStack);
        }
        MutableComponent comp = Component.translatable(this.getDescriptionId(pStack));
        comp = Component.translatable("items.apotheosis.gem." + inst.purity().getSerializedName(), comp);
        return comp.withStyle(Style.EMPTY.withColor(inst.purity().getColor()));
    }

    @Override
    public String getDescriptionId(ItemStack pStack) {
        DynamicHolder<Gem> gem = getGem(pStack);
        if (!gem.isBound()) {
            return super.getDescriptionId();
        }
        return super.getDescriptionId(pStack) + "." + gem.getId();
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        UnsocketedGem inst = UnsocketedGem.of(pStack);
        return inst.isValid() && inst.isPerfect();
    }

//    @Override
//    public boolean canBeHurtBy(ItemStack stack, DamageSource src) {
//        return super.canBeHurtBy(stack, src) && !src.is(DamageTypes.FALLING_ANVIL);
//    }

    //@Override
    public void fillItemCategory(CreativeModeTab.Output out) {
        GemRegistry.INSTANCE.getValues().stream().sorted(Comparator.comparing(Gem::getId)).forEach(gem -> {
            Arrays.stream(Purity.values()).forEach(purity -> {
                if (purity.isAtLeast(gem.getMinPurity())) {
                    ItemStack stack = new ItemStack(this);
                    setGem(stack, gem);
                    setPurity(stack, purity);
                    out.accept(stack);
                }
            });
        });
    }
//
//    @Override
//    @Nullable
//    public String getCreatorModId(ItemStack stack) {
//        UnsocketedGem inst = UnsocketedGem.of(stack);
//        if (inst.isValid()) {
//            return inst.gem().getId().getNamespace();
//        }
//        return super.getCreatorModId(stack);
//    }

    /**
     * Retrieves the underlying Gem instance of this gem stack.
     *
     * @param gem The gem stack
     * @returns A DynamicHolder targetting the gem, which may be unbound if the gem is missing or invalid.
     */
    public static DynamicHolder<Gem> getGem(ItemStack gem) {
        return gem.getOrDefault(Registration.Components.GEM, GemRegistry.INSTANCE.emptyHolder());
    }

    /**
     * Sets the ID of the gem stored in this gem stack.
     *
     * @param gemStack The gem stack
     * @param gem      The Gem to store
     */
    public static void setGem(ItemStack gemStack, Gem gem) {
        gemStack.set(Registration.Components.GEM, GemRegistry.INSTANCE.holder(gem));
    }

    public static Purity getPurity(ItemStack stack) {
        return stack.getOrDefault(Registration.Components.PURITY, Purity.CRACKED);
    }

    public static void setPurity(ItemStack stack, Purity purity) {
        stack.set(Registration.Components.PURITY, purity);
    }
}
