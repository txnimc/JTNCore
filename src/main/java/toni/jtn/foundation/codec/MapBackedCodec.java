package toni.jtn.foundation.codec;


import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.collect.BiMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import net.minecraft.resources.ResourceLocation;
import toni.jtn.JTN;

/**
 * Map backed codec with optional default functionality.
 * <p>
 * Serialized objects are expected to declare their serializer in the top-level 'type' key.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapBackedCodec<V extends CodecProvider<? super V>> implements Codec<V> {

    protected final String name;
    protected final BiMap<ResourceLocation, Codec<? extends V>> registry;
    protected final Supplier<Codec<? extends V>> defaultCodec;

    /**
     * @param defaultCodec A supplier for the default codec. The supplier may not be null, but may return null.
     */
    public MapBackedCodec(String name, BiMap<ResourceLocation, Codec<? extends V>> registry, Supplier<Codec<? extends V>> defaultCodec) {
        this.name = name;
        this.registry = registry;
        this.defaultCodec = defaultCodec;
    }

    public MapBackedCodec(String name, BiMap<ResourceLocation, Codec<? extends V>> registry) {
        this(name, registry, () -> null);
    }

    @Override
    public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
        Optional<T> type = ops.get(input, "type").resultOrPartial(str -> {});
        Optional<ResourceLocation> key = type.map(t -> ResourceLocation.CODEC.decode(ops, t).resultOrPartial(JTN.LOGGER::error).get().getFirst());

        Codec codec = key.<Codec>map(this.registry::get).orElse(this.defaultCodec.get());

        if (codec == null) {
            return DataResult.error(() -> "Failure when parsing a " + this.name + ". Unrecognized type: " + key.map(ResourceLocation::toString).orElse("null"));
        }
        return codec.decode(ops, input);
    }

    @Override
    public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {
        Codec<V> codec = (Codec<V>) input.getCodec();
        ResourceLocation key = this.registry.inverse().get(codec);
        if (key == null) {
            return DataResult.error(() -> "Attempted to serialize an element of type " + this.name + " with an unregistered codec! Object: " + input);
        }
        T encodedKey = ResourceLocation.CODEC.encodeStart(ops, key).getOrThrow(IllegalStateException::new);
        T encodedObj = codec.encode(input, ops, prefix).getOrThrow(IllegalStateException::new);
        return ops.mergeToMap(encodedObj, ops.createString("type"), encodedKey);
    }
}