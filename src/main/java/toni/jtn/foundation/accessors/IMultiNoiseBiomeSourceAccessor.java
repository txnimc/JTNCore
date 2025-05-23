package toni.jtn.foundation.accessors;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;

public interface IMultiNoiseBiomeSourceAccessor {
    public Set<Holder<Biome>> jtn$getPossibleBiomesCache();
    public void jtn$setPossibleBiomesCache(Set<Holder<Biome>> val);
}
