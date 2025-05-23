package toni.jtn.content.runes.gem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import toni.jtn.JTN;
import toni.jtn.content.runes.LootCategory;
import toni.jtn.content.runes.gem.ExtraGemBonusRegistry.ExtraGemBonus;
import toni.jtn.content.runes.gem.bonus.GemBonus;
import toni.jtn.foundation.codec.CodecProvider;
import toni.jtn.foundation.registry.DynamicHolder;
import toni.jtn.foundation.registry.DynamicRegistry;

/**
 * Registry of additional gem bonuses. This can be used to add conditional bonuses to gems, or modify
 * existing gems to add bonuses only when certain mods are present.
 * <p>
 * It can also be used by addon mods to add bonuses to Apotheosis-native gems.
 */
public class ExtraGemBonusRegistry extends DynamicRegistry<ExtraGemBonus> {

    public static final ExtraGemBonusRegistry INSTANCE = new ExtraGemBonusRegistry();

    protected Multimap<DynamicHolder<Gem>, ExtraGemBonus> extraBonuses = HashMultimap.create();

    public ExtraGemBonusRegistry() {
        super(JTN.LOGGER, "extra_gem_bonuses", true, false);
    }

    @Override
    protected void beginReload() {
        super.beginReload();
        this.extraBonuses = HashMultimap.create();
    }

    @Override
    protected void onReload() {
        super.onReload();
        for (ExtraGemBonus extraBonus : this.getValues()) {
            this.extraBonuses.put(extraBonus.gem, extraBonus);
        }
    }

    public static Collection<ExtraGemBonus> getBonusesFor(DynamicHolder<Gem> gem) {
        return INSTANCE.extraBonuses.get(gem);
    }

    @Override
    protected void registerBuiltinCodecs() {
        this.registerDefaultCodec(JTN.location("extra_gem_bonus"), ExtraGemBonus.CODEC);
    }

    public static record ExtraGemBonus(DynamicHolder<Gem> gem, List<GemBonus> bonuses) implements CodecProvider<ExtraGemBonus> {

        public static final Codec<ExtraGemBonus> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                GemRegistry.INSTANCE.holderCodec().fieldOf("gem").forGetter(ExtraGemBonus::gem),
                GemBonus.CODEC.listOf().fieldOf("bonuses").forGetter(ExtraGemBonus::bonuses))
            .apply(inst, ExtraGemBonus::new));

        @Override
        public Codec<? extends ExtraGemBonus> getCodec() {
            return CODEC;
        }

        public static Builder builder(DynamicHolder<Gem> gem) {
            return new Builder(gem);
        }

        public static class Builder {

            protected final DynamicHolder<Gem> gem;
            protected List<GemBonus> bonuses = new ArrayList<>();

            public Builder(DynamicHolder<Gem> gem) {
                this.gem = gem;
            }

            public Builder bonus(LootCategory cat, GemBonus.Builder builder) {
                return this.bonus(new GemClass(cat), builder);
            }

            public Builder bonus(GemClass gClass, GemBonus.Builder builder) {
                this.bonuses.add(builder.build(gClass));
                return this;
            }

            public Builder bonus(GemBonus bonus) {
                this.bonuses.add(bonus);
                return this;
            }

            public ExtraGemBonus build() {
                return new ExtraGemBonus(this.gem, this.bonuses);
            }
        }

    }

}
