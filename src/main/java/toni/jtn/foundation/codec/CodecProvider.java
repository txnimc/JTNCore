package toni.jtn.foundation.codec;

import com.mojang.serialization.Codec;

/**
 * A Codec Provider is an object which supplies the codec that was used to create it.
 *
 * @param <R> The registry (base) type of the object
 */
public interface CodecProvider<R> {

    /**
     * @return The codec used to de/serialize this object to/from disk.
     * @implNote The return value of this method must be invariant.
     */
    Codec<? extends R> getCodec();

}