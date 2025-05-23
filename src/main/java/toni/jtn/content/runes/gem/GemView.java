package toni.jtn.content.runes.gem;

import net.minecraft.world.item.ItemStack;
import toni.jtn.foundation.registry.DynamicHolder;

/**
 * Superclass of both {@link GemInstance} and {@link UnsocketedGem}, used to pass either class to a single method.
 */
public interface GemView {

    /**
     * Returns the gem specified by Components#GEM on the {@link #gemStack()}.
     */
    DynamicHolder<Gem> gem();

    /**
     * Returns the purity specified by Components#PURITY on the {@link #gemStack()}.
     * <p>
     * If the gem {@linkplain DynamicHolder#isBound() is bound}, and the purity would be invalid, it will be clamped to an in-range value.
     */
    Purity purity();

    /**
     * Returns the source items stack for the gem.
     */
    ItemStack gemStack();
}
