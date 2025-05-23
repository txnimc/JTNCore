package toni.jtn.content.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.joml.Vector3i;
import toni.jtn.foundation.accessors.IMultiNoiseBiomeSourceAccessor;
import toni.jtn.foundation.biome.BiomeSet;

import java.util.Set;

public class JTNWorldGen {

    public static BiomeSet plains;
    public static BiomeSet desert;
    public static BiomeSet snow;
    public static BiomeSet forest;
    public static BiomeSet jungle;

    public static Holder<Biome> ocean;
    public static Holder<Biome> frozenOcean;
    public static Holder<Biome> coldOcean;

    public static Holder<Biome> modifyBiome(MultiNoiseBiomeSource source, int i, int j, int k, Climate.Sampler sampler) {
        if (((IMultiNoiseBiomeSourceAccessor) source).jtn$getPossibleBiomesCache() == null || jungle == null || plains == null || forest == null || desert == null || snow == null) {
            var biomes = source.possibleBiomes();
            ((IMultiNoiseBiomeSourceAccessor) source).jtn$setPossibleBiomesCache(biomes);

            ocean = getBiome(biomes, Biomes.OCEAN);
            frozenOcean = getBiome(biomes, Biomes.FROZEN_OCEAN);
            coldOcean = getBiome(biomes, Biomes.COLD_OCEAN);

            plains = BiomeSet.builder(biomes)
                .setNormal(getBiome(biomes, Biomes.BIRCH_FOREST))
                .setWeird(getBiome(biomes, "plains"))
                .setVeryWeird(getBiome(biomes, Biomes.SUNFLOWER_PLAINS))
                .setBeach(getBiome(biomes, Biomes.BEACH))
                .setLowlands(getBiome(biomes, Biomes.OLD_GROWTH_BIRCH_FOREST))
                .setMountain(getBiome(biomes, "yosemite_lowlands"))
                .setMountainWeird(getBiome(biomes, "birch_taiga"))
                .setMountainVeryWeird(getBiome(biomes, Biomes.OLD_GROWTH_BIRCH_FOREST))
                .setPeaks(getBiome(biomes, "yosemite_cliffs"));

            forest = BiomeSet.builder(biomes)
                .setNormal(getBiome(biomes, Biomes.OLD_GROWTH_SPRUCE_TAIGA))
                .setWeird(getBiome(biomes, Biomes.OLD_GROWTH_SPRUCE_TAIGA))
                .setVeryWeird(getBiome(biomes, "siberian_taiga"))
                .setBeach(getBiome(biomes, Biomes.BEACH))
                .setLowlands(getBiome(biomes, Biomes.OLD_GROWTH_SPRUCE_TAIGA))
                .setMountain(getBiome(biomes,  Biomes.OLD_GROWTH_SPRUCE_TAIGA))
                .setMountainWeird(getBiome(biomes, "siberian_taiga"))
                .setMountainVeryWeird(getBiome(biomes, Biomes.OLD_GROWTH_SPRUCE_TAIGA))
                .setPeaks(getBiome(biomes, Biomes.OLD_GROWTH_SPRUCE_TAIGA));

            desert = BiomeSet.builder(biomes)
                .setNormal(getBiome(biomes, "bryce_canyon"))
                .setWeird(getBiome(biomes, "bryce_canyon"))
                .setVeryWeird(getBiome(biomes, "bryce_canyon"))
                .setBeach(getBiome(biomes, Biomes.BEACH))
                .setLowlands(getBiome(biomes, "bryce_canyon"))
                .setMountain(getBiome(biomes,  "bryce_canyon"))
                .setMountainWeird(getBiome(biomes, "bryce_canyon"))
                .setMountainVeryWeird(getBiome(biomes, "bryce_canyon"))
                .setPeaks(getBiome(biomes, "bryce_canyon"));

            snow = BiomeSet.builder(biomes)
                .setNormal(getBiome(biomes, Biomes.SNOWY_PLAINS))
                .setWeird(getBiome(biomes, "alpine_grove"))
                .setVeryWeird(getBiome(biomes, Biomes.SNOWY_TAIGA))
                .setBeach(getBiome(biomes, Biomes.SNOWY_BEACH))
                .setLowlands(getBiome(biomes, Biomes.SNOWY_TAIGA))
                .setMountain(getBiome(biomes,  "alpine_grove"))
                .setMountainWeird(getBiome(biomes, "glacial_chasm"))
                .setMountainVeryWeird(getBiome(biomes, "glacial_chasm"))
                .setPeaks(getBiome(biomes, "glacial_chasm"));

            jungle = BiomeSet.builder(biomes)
                .setNormal(getBiome(biomes, "amethyst_canyon"))
                .setWeird(getBiome(biomes, "amethyst_canyon"))
                .setVeryWeird(getBiome(biomes, "amethyst_rainforest"))
                .setBeach(getBiome(biomes, Biomes.BEACH))
                .setLowlands(getBiome(biomes, "amethyst_rainforest"))
                .setMountain(getBiome(biomes,  "amethyst_canyon"))
                .setMountainWeird(getBiome(biomes, "amethyst_canyon"))
                .setMountainVeryWeird(getBiome(biomes, "amethyst_canyon"))
                .setPeaks(getBiome(biomes, "amethyst_canyon"));

            System.out.println("Loaded JTN Biome Sets!");
        }

        var climate = sampler.sample(i, j, k);
        var largeHumidity = Climate.unquantizeCoord(climate.humidity());
        var smallHumidity = Climate.unquantizeCoord(sampler.sample(i * 3, j * 3, k * 3).humidity());
        var location = new Vector3i(QuartPos.toBlock(i), 0, QuartPos.toBlock(k));
        var distance = location.distance(0, 0, 0);
        var rawDistance = distance;
        distance += smallHumidity * 250;

        var isValidBadlands = isInsideEllipse(location, largeHumidity * 350, 6500, 4000);
        var isBorderZone = isInsideEllipse(location, largeHumidity * 350, 6850, 4350);

        if (distance > 10000)
            return ocean;

        // custom ocean biome
        var original = source.getNoiseBiome(climate);
        if (original.is((key) -> key.location().getPath().contains("ocean"))) {
            if (distance > 8000 || isValidBadlands)
                return ocean;

            return smallHumidity > 0 ? coldOcean : frozenOcean;
        }

        // don't touch river or cave biomes
        if (original.is((key) -> key.location().getPath().contains("river") || key.location().getPath().contains("cave"))) {
            return original;
        }

        if (distance > 8000)
            return jungle.getBiome(climate);

        if (rawDistance < 400)
            return plains.getBiome(climate);


        if (distance < 1500)
            return forestOrPlains(smallHumidity, climate);

        // badlands ellipse
        if (isValidBadlands) {
            if (smallHumidity > -0.25f) {
                return largeHumidity > 0 ? desert.getBiome(climate) : forest.getBiome(climate);
            }
            else {
                return forestOrPlains(largeHumidity, climate);
            }
        }

        // forest or plains bordering before snow starts
        if (isBorderZone)
            return forestOrPlains(largeHumidity, climate);

        // snow
        if (smallHumidity > 0f) {
            return Math.abs(largeHumidity) > 0.4f ? forest.getBiome(climate) : snow.getBiome(climate);
        }
        else {
            return Math.abs(largeHumidity) > 0.4f ? plains.getBiome(climate) : snow.getBiome(climate);
        }
    }

    private static boolean isInsideEllipse(Vector3i location, float waviness, float sizeX, float sizeZ) {
        return Math.pow(location.x + waviness, 2.0) / Math.pow(sizeX, 2.0) + Math.pow(location.z + waviness, 2.0) / Math.pow(sizeZ, 2.0) <= 1.0;
    }

    public static Holder<Biome> forestOrPlains(float humidity, Climate. TargetPoint climate) {
        return humidity > 0 ? forest.getBiome(climate) : plains.getBiome(climate);
    }

    public static Holder<Biome> getBiome(Set<Holder<Biome>> possibleBiomes, String location) {
        try {
            var ret = possibleBiomes.stream().filter(b -> b.is((key) -> key.location().getPath().equals(location))).findFirst().orElse(null);
            if (ret == null)
                throw new IllegalArgumentException("No such biome!: " + location);

            return ret;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public static Holder<Biome> getBiome(Set<Holder<Biome>> possibleBiomes, ResourceKey<Biome> location) {
        try {
            var ret = possibleBiomes.stream().filter(b -> b.is(location)).findFirst().orElse(null);
            if (ret == null)
                throw new IllegalArgumentException("No such biome: " + location);

            return ret;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }
}
