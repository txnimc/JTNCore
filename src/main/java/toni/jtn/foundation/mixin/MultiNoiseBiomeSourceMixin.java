package toni.jtn.foundation.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.jtn.JTN;
import toni.jtn.foundation.accessors.IMultiNoiseBiomeSourceAccessor;

import java.util.Set;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 1500)
public class MultiNoiseBiomeSourceMixin implements IMultiNoiseBiomeSourceAccessor {

    @Unique
    private Set<Holder<Biome>> jtn$possibleBiomesCache;


    @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("RETURN"), cancellable = true)
    public void onGenerateBiome(int i, int j, int k, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        //var ret = JTN.modifyBiome((MultiNoiseBiomeSource) (Object) this, i, j, k, sampler);
        //cir.setReturnValue(ret);
        //cir.cancel();
    }

    @Unique
    public Set<Holder<Biome>> jtn$getPossibleBiomesCache() {
        return jtn$possibleBiomesCache;
    }

    @Unique
    public void jtn$setPossibleBiomesCache(Set<Holder<Biome>> val) {
        jtn$possibleBiomesCache = val;
    }
}
