package toni.jtn.foundation.registry;


import net.minecraft.resources.ResourceLocation;
import toni.jtn.foundation.codec.CodecProvider;

import java.util.HashMap;
import java.util.IdentityHashMap;

/**
 * An implementation of BiMap which uses a normal {@link HashMap} for the forward map, and
 * uses an {@link IdentityHashMap} for the inverse.
 * <p>
 * This preserves the structure that registry values should be compared by identity when retrieving their keys.
 */
public class DynRegBiMap<R extends CodecProvider<? super R>> extends AbstractBiMap<ResourceLocation, R> {

    public DynRegBiMap() {
        super(new HashMap<>(), new IdentityHashMap<>());
    }

}