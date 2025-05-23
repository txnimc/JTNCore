package toni.jtn.content.runes.gem;

import java.util.Arrays;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import toni.jtn.content.runes.LootCategory;
import toni.jtn.foundation.Registration;

/**
 * A Gem Class is the set of types of items it may be applied to.
 * This comes in the form of a named group of LootCategories.
 */
public record GemClass(String key, HolderSet<LootCategory> types) {

    public static Codec<GemClass> EXPLICIT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.STRING.fieldOf("key").forGetter(GemClass::key),
        RegistryCodecs.homogeneousList(Registration.BuiltInRegs.LOOT_CATEGORY.key()).fieldOf("types").forGetter(GemClass::types))
        .apply(inst, GemClass::new));

    public static Codec<GemClass> CODEC = Codec.either(EXPLICIT_CODEC, LootCategory.CODEC)
        .xmap(e -> e.map(Function.identity(), GemClass::new), GemClass::toEither);

    public GemClass(LootCategory category) {
        this(category.getKey().getPath(), category);
    }

    public GemClass(String key, LootCategory... types) {
        this(key, HolderSet.direct(Arrays.stream(types).map(Registration.BuiltInRegs.LOOT_CATEGORY::wrapAsHolder).toList()));
    }

    public GemClass(String key, HolderSet<LootCategory> types) {
        this.key = key;
        this.types = types;
        Preconditions.checkArgument(!Strings.isNullOrEmpty(this.key), "Invalid GemClass with null key");
        Preconditions.checkArgument(this.types != null && this.types.size() > 0, "Invalid GemClass with null or empty types");
    }

    private static Either<GemClass, LootCategory> toEither(GemClass gc) {
        if (gc.types.size() == 1) {
            return Either.right(gc.types.iterator().next().value());
        }
        return Either.left(gc);
    }
}
