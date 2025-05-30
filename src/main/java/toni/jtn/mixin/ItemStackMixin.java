package toni.jtn.mixin;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import toni.jtn.content.runes.tooltip.AttributeSlot;
import toni.jtn.foundation.accessors.PseudoAccessorItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ItemStack.class)
public class ItemStackMixin implements PseudoAccessorItemStack {


    @Unique
    private Map<AttributeSlot, Multimap<Attribute, AttributeModifier>> capturedAttributes = new HashMap<>();

    @Override
    public Map<AttributeSlot, Multimap<Attribute, AttributeModifier>> quark$getCapturedAttributes() {
        return capturedAttributes;
    }

    @Override
    public void quark$capturePotionAttributes(List<Pair<Attribute, AttributeModifier>> attributes) {
        Multimap<Attribute, AttributeModifier> attributeContainer = LinkedHashMultimap.create();
        for(var pair : attributes) {
            attributeContainer.put(pair.getFirst(), pair.getSecond());
        }
        capturedAttributes.put(AttributeSlot.POTION, attributeContainer);
    }

}
