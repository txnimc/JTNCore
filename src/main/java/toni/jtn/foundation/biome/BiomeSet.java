package toni.jtn.foundation.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import toni.jtn.JTN;
import toni.jtn.content.worldgen.JTNWorldGen;

import java.util.Set;

public class BiomeSet {
    private Holder<Biome> Normal;
    private Holder<Biome> Weird;
    private Holder<Biome> VeryWeird;
    private Holder<Biome> Lowlands;
    private Holder<Biome> Beach;
    private Holder<Biome> Mountain;
    private Holder<Biome> MountainWeird;
    private Holder<Biome> MountainVeryWeird;
    private Holder<Biome> Peaks;
    private Holder<Biome> StonyShore;

    public BiomeSet(Set<Holder<Biome>> possibleBiomes, Holder<Biome> normal, Holder<Biome> weird, Holder<Biome> lowlands, Holder<Biome> beach, Holder<Biome> mountain, Holder<Biome> mountainWeird, Holder<Biome> mountainVeryWeird, Holder<Biome> peaks) {
        Normal = normal;
        Weird = weird;
        Mountain = mountain;
        MountainWeird = mountainWeird;
        MountainVeryWeird = mountainVeryWeird;
        Peaks = peaks;
        Lowlands = lowlands;
        Beach = beach;

        StonyShore = JTNWorldGen.getBiome(possibleBiomes, Biomes.STONY_SHORE);
    }

    private BiomeSet() { }

    public static BiomeSet builder(Set<Holder<Biome>> possibleBiomes) {
        var set = new BiomeSet();
        set.StonyShore = JTNWorldGen.getBiome(possibleBiomes, Biomes.STONY_SHORE);

        return set;
    }

    public BiomeSet setNormal(Holder<Biome> biome) {
        Normal = biome;
        return this;
    }

    public BiomeSet setWeird(Holder<Biome> biome) {
        Weird = biome;
        return this;
    }

    public BiomeSet setVeryWeird(Holder<Biome> biome) {
        VeryWeird = biome;
        return this;
    }

    public BiomeSet setMountain(Holder<Biome> biome) {
        Mountain = biome;
        return this;
    }

    public BiomeSet setMountainWeird(Holder<Biome> biome) {
        MountainWeird = biome;
        return this;
    }

    public BiomeSet setMountainVeryWeird(Holder<Biome> biome) {
        MountainVeryWeird = biome;
        return this;
    }

    public BiomeSet setPeaks(Holder<Biome> biome) {
        Peaks = biome;
        return this;
    }

    public BiomeSet setLowlands(Holder<Biome> biome) {
        Lowlands = biome;
        return this;
    }

    public BiomeSet setBeach(Holder<Biome> biome) {
        Beach = biome;
        return this;
    }

    public Holder<Biome> getBiome(Climate.TargetPoint climate) {

        float continentalness = Climate.unquantizeCoord(climate.continentalness());
        float erosion = Climate.unquantizeCoord(climate.erosion());
        float weirdness = Climate.unquantizeCoord(climate.weirdness());
        double PV = NoiseRouterData.peaksAndValleys(weirdness);

        if (PV < -0.85f)
            return valleys(climate, continentalness, erosion);

        if (PV < -0.6f)
            return lowSlice(climate, continentalness, erosion);

        if (PV < 0.2f)
            return midSlice(climate, continentalness, erosion);

        if (PV < 0.7f)
            return highSlice(climate, continentalness, erosion);

        return peaks(climate, continentalness, erosion);
    }

    private Holder<Biome> peaks(Climate.TargetPoint climate, float continentalness, float erosion) {
        if (continentalness > 0.03f)
        {
            if (erosion < -0.375f)
                return Peaks;

            if (erosion < 0.05f)
                return MountainOrWeird(climate);

            return NormalOrWeird(climate);
        }
        else {
            if (erosion < -0.780f)
                return Peaks;

            if (erosion < -0.375f)
                return MountainOrWeird(climate);

            return NormalOrWeird(climate);
        }
    }

    private Holder<Biome> highSlice(Climate.TargetPoint climate, float continentalness, float erosion) {

        if (continentalness > 0.03f)
        {
            if (erosion < -0.780f)
                return Peaks;

            if (erosion < 0.05f)
                return MountainOrWeird(climate);

            return NormalOrWeird(climate);
        }

        return NormalOrWeird(climate);
    }

    private Holder<Biome> midSlice(Climate.TargetPoint climate, float continentalness, float erosion) {
        if (continentalness < -0.15f)
        {
            if (erosion < -0.233f)
                return StonyShore;

            if (erosion < 0.05f)
                return NormalOrWeird(climate);

            return Beach;
        }

        if (erosion < -0.233f)
            return MountainOrWeird(climate);

        if (erosion > 0.55f)
            return Lowlands;

        return NormalOrWeird(climate);
    }

    private Holder<Biome> lowSlice(Climate.TargetPoint climate, float continentalness, float erosion) {
        if (continentalness < -0.15f)
        {
            if (erosion < -0.233f)
                return StonyShore;

            return Beach;
        }

        if (erosion < -0.375f)
            return MountainOrWeird(climate);

        if (erosion < 0.05f && continentalness > 0.03f)
            return MountainOrWeird(climate);

        if (erosion > 0.55f)
            return Lowlands;

        return NormalOrWeird(climate);
    }

    private Holder<Biome> valleys(Climate.TargetPoint climate, float continentalness, float erosion) {

        if (continentalness < 0.03f)
            return NormalOrWeird(climate);

        if (erosion > 0.55f)
            return Lowlands;

        return NormalOrWeird(climate);
    }

    private Holder<Biome> MountainOrWeird(Climate.TargetPoint climate) {
        var weird = Math.abs(Climate.unquantizeCoord(climate.weirdness()));

        return weird > 0.40f ? MountainVeryWeird :  weird > 0.25f ? MountainWeird : Mountain;
    }

    private Holder<Biome> NormalOrWeird(Climate.TargetPoint climate) {
        var weird = Math.abs(Climate.unquantizeCoord(climate.weirdness()));

        return weird > 0.40f ? VeryWeird : weird > 0.25f ? Weird : Normal;
    }
}