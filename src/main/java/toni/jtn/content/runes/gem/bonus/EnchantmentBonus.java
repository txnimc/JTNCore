package toni.jtn.content.runes.gem.bonus;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import toni.jtn.content.runes.gem.GemClass;
import toni.jtn.content.runes.gem.GemInstance;
import toni.jtn.content.runes.gem.GemView;
import toni.jtn.content.runes.gem.Purity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import toni.jtn.foundation.codec.JTNCodecs;
import toni.jtn.foundation.events.AttributeTooltipContext;

public class EnchantmentBonus extends GemBonus {

    public static Codec<EnchantmentBonus> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            gemClass(),
            Enchantment.CODEC.fieldOf("enchantment").forGetter(a -> a.ench),
            Mode.CODEC.optionalFieldOf("mode", Mode.SINGLE).forGetter(a -> a.mode),
            Purity.mapCodec(Codec.intRange(1, 127)).fieldOf("values").forGetter(a -> a.values))
        .apply(inst, EnchantmentBonus::new));

    protected final Holder<Enchantment> ench;
    protected final Mode mode;
    protected final Map<Purity, Integer> values;

    public EnchantmentBonus(GemClass gemClass, Holder<Enchantment> ench, Mode mode, Map<Purity, Integer> values) {
        super(gemClass);
        this.ench = ench;
        this.values = values;
        this.mode = mode;
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        int level = this.values.get(gem.purity());
        String desc = "bonus." + this.getTypeKey() + ".raw";
        if (this.mode == Mode.GLOBAL) {
            desc += ".global";
        }
        else if (this.mode == Mode.EXISTING) {
            desc += ".mustExist";
        }
        Component enchName = this.ench.value().description().plainCopy();
        return Component.translatable(desc, enchName, Component.translatable("enchantment.level." + level)).withStyle(ChatFormatting.GREEN);
    }

    @Override
    public void getEnchantmentLevels(GemInstance gem, Map<Enchantment, Integer> event) {
        int level = this.values.get(gem.purity());
        var existing = event.getOrDefault(this.ench.value(), 0);
        event.put(this.ench.value(), existing + level);
        //enchantments.upgrade(this.ench, enchantments.getLevel(this.ench) + level);

//        ItemEnchantments.Mutable enchantments = event.getEnchantments();
//        int level = this.values.get(gem.purity());
//        if (this.mode == Mode.GLOBAL) {
//            for (Holder<Enchantment> e : enchantments.keySet()) {
//                int current = enchantments.getLevel(e);
//                if (current > 0) {
//                    enchantments.upgrade(e, current + level);
//                }
//            }
//        }
//        else if (this.mode == Mode.EXISTING) {
//            int current = enchantments.getLevel(this.ench);
//            if (current > 0) {
//                enchantments.upgrade(this.ench, current + level);
//            }
//        }
//        else {
//            enchantments.upgrade(this.ench, enchantments.getLevel(this.ench) + level);
//        }
    }

    @Override
    public boolean supports(Purity purity) {
        return this.values.containsKey(purity);
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }

    public static Builder builder(HolderLookup.Provider p) {
        return new Builder(p);
    }

    public static enum Mode {
        SINGLE,
        EXISTING,
        GLOBAL;

        public static final Codec<Mode> CODEC = JTNCodecs.enumCodec(Mode.class);
    }

    public static class Builder extends GemBonus.Builder {
        private final HolderLookup.Provider provider;
        private Holder<Enchantment> enchantment;
        private Mode mode;
        private Map<Purity, Integer> values;

        public Builder(HolderLookup.Provider p) {
            this.provider = p;
            this.values = new HashMap<>();
            this.mode = Mode.SINGLE;
        }

        public Builder enchantment(Holder<Enchantment> enchantment) {
            this.enchantment = enchantment;
            return this;
        }

        public Builder enchantment(ResourceKey<Enchantment> enchantment) {
            this.enchantment = provider.lookup(Registries.ENCHANTMENT).get().getOrThrow(enchantment);
            return this;
        }

        public Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder value(Purity purity, int value) {
            if (value < 1 || value > 127) {
                throw new IllegalArgumentException("EnchantmentBonus is limited to values between 1 and 127 (inclusive).");
            }
            this.values.put(purity, value);
            return this;
        }

        @Override
        public EnchantmentBonus build(GemClass gemClass) {
            return new EnchantmentBonus(gemClass, this.enchantment, this.mode, this.values);
        }

        public GemBonus.Builder defaultValues() {
            return this.value(Purity.CRACKED, 1)
                .value(Purity.CHIPPED, 2)
                .value(Purity.FLAWED, 3)
                .value(Purity.NORMAL, 4)
                .value(Purity.FLAWLESS, 5)
                .value(Purity.PERFECT, 10);
        }

        public GemBonus.Builder values(int cracked, int chipped, int flawed, int normal, int flawless, int perfect) {
            return this.value(Purity.CRACKED, cracked)
                .value(Purity.CHIPPED, chipped)
                .value(Purity.FLAWED, flawed)
                .value(Purity.NORMAL, normal)
                .value(Purity.FLAWLESS, flawless)
                .value(Purity.PERFECT, perfect);
        }
    }

}
