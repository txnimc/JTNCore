package toni.jtn.content.runes.gem;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;

import toni.jtn.JTN;
import toni.jtn.content.runes.gem.ExtraGemBonusRegistry.ExtraGemBonus;
import toni.jtn.content.runes.gem.bonus.GemBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import toni.jtn.foundation.registry.DynamicHolder;
import toni.jtn.foundation.registry.DynamicRegistry;

public class GemRegistry extends DynamicRegistry<Gem> {

    public static final GemRegistry INSTANCE = new GemRegistry();

    public GemRegistry() {
        super(JTN.LOGGER, "gems", true, false);
    }

    @Override
    protected void registerBuiltinCodecs() {
        this.registerDefaultCodec(JTN.location("gem"), Gem.CODEC);
    }

    @Override
    protected void validateItem(ResourceLocation key, Gem item) {
        super.validateItem(key, item);
        for (Purity p : Purity.values()) {
            if (p.isAtLeast(item.getMinPurity())) {
                boolean atLeastOne = false;
                for (GemBonus bonus : item.bonuses) {
                    if (bonus.supports(p)) {
                        atLeastOne = true;
                    }
                }
                Preconditions.checkArgument(atLeastOne, "No bonuses provided for supported purity %s. At least one bonus must be provided, or the minimum purity should be raised.", p.getName());
            }
        }
    }

    @Override
    protected void onReload() {
        super.onReload();
        for (Gem gem : this.getValues()) {
            DynamicHolder<Gem> holder = this.holder(gem);
            for (ExtraGemBonus extraBonus : ExtraGemBonusRegistry.getBonusesFor(holder)) {
                for (GemBonus bonus : extraBonus.bonuses()) {
                    try {
                        gem.appendExtraBonus(bonus);
                    }
                    catch (Exception ex) {
                        ResourceLocation extraBonusKey = ExtraGemBonusRegistry.INSTANCE.getKey(extraBonus);
                        this.logger.warn("Failed to apply extra gem bonus for class {} to gem {}.", bonus.getGemClass().key(), holder.getId());
                        this.logger.warn("Exception while applying ExtraGemBonus %s: ".formatted(extraBonusKey), ex);
                    }
                }
            }
        }
    }

    /**
     * Creates a new {@link ItemStack} containing the provided {@link Gem}.
     * <p>
     * The provided purity will be automatically clamped based on {@link Gem#getMinPurity()}.
     * 
     * @deprecated Use {@link Gem#toStack(Purity)} instead.
     */
    @Deprecated
    public static ItemStack createGemStack(Gem gem, Purity purity) {
        return gem.toStack(purity);
    }
}
