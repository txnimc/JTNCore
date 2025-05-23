package toni.jtn.foundation.codec;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;


import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * A Cached Object is an immutable object which is stored in ItemStack NBT, but stored in Object form.<br>
 * The live object is (or should be considered) immutable.<br>
 * Changes to the ItemStack's NBT will result in the object being deserialized again.
 * <p>
 * Note: Will be removed in 1.20.5 in favor of Data Components.
 *
 * @param <T> The type of object being cached.
 */
public final class CachedObject<T> {

    public static final int HAS_NEVER_BEEN_INITIALIZED = -2;

    protected final ResourceLocation id;
    protected final Function<ItemStack, T> deserializer;
    protected final ToIntFunction<ItemStack> hasher;

    protected volatile T data = null;
    protected volatile int lastNbtHash = HAS_NEVER_BEEN_INITIALIZED;

    /**
     * Creates a new CachedObject.
     *
     * @param id           The ID of this object.
     * @param deserializer The deserialization function. May return null. The stack passed to the function may be empty.
     * @param hasher       A Function which can generate a hash from the relevant itemstack data.
     */
    public CachedObject(ResourceLocation id, Function<ItemStack, T> deserializer, ToIntFunction<ItemStack> hasher) {
        this.id = id;
        this.deserializer = deserializer;
        this.hasher = hasher;
    }

    /**
     * Retrieves the stored value from this CachedObject, computing it if necessary from the passed itemstack.
     *
     * @param stack The itemstack owning this CachedObject.
     * @return The cached result.
     */
    @Nullable
    public T get(ItemStack stack) {
        if (this.lastNbtHash == HAS_NEVER_BEEN_INITIALIZED) {
            this.compute(stack);
            return this.data;
        }

        if (this.hasher.applyAsInt(stack) != this.lastNbtHash) {
            this.compute(stack);
        }

        return this.data;
    }

    /**
     * Resets this CachedObject to the initial state, deleting all cached data.
     */
    public void reset() {
        this.data = null;
        this.lastNbtHash = HAS_NEVER_BEEN_INITIALIZED;
    }

    /**
     * Computes the cached value from the parent itemstack.
     *
     * @param stack The itemstack owning this CachedObject.
     */
    protected void compute(ItemStack stack) {
        synchronized (this) {
            this.data = this.deserializer.apply(stack);
            this.lastNbtHash = this.hasher.applyAsInt(stack);
        }
    }

    /**
     * Creates a hashing function that hashes a specific subkey.
     */
    public static ToIntFunction<ItemStack> hashComponents(DataComponentType<?>... types) {
        List<DataComponentType<?>> typeList = Arrays.asList(types);
        return stack -> Arrays.hashCode(typeList.stream().map(stack::get).filter(Objects::nonNull).toArray());
    }

    /**
     * A CachedObjectSource is any parent object capable of holding CachedObjects.<br>
     * Currently this is limited to just ItemStack. This interface is applied via mixin.
     * <p>
     * Cast ItemStack to this interface to access CachedObjects.
     */
    public interface CachedObjectSource {

        /**
         * Gets a cached value, creating the necessary CachedObject (and computing the value) if necessary.
         *
         * @param <T>          The type of object being requested.
         * @param id           The ID of the cached object type.
         * @param deserializer The cached object deserializer.
         * @param hasher       The hash function.
         * @return The object, as produced by the deserializer, which will also be stored in the internal cache.
         */
        public <T> T getOrCreate(ResourceLocation id, Function<ItemStack, T> deserializer, ToIntFunction<ItemStack> hasher);

        /**
         * Helper which hides the cast to CachedObjectSource.
         *
         * @see #getOrCreate(ResourceLocation, Function, ToIntFunction)
         */
        public static <T> T getOrCreate(ItemStack stack, ResourceLocation id, Function<ItemStack, T> deserializer, ToIntFunction<ItemStack> hasher) {
            return ((CachedObjectSource) (Object) stack).getOrCreate(id, deserializer, hasher);
        }
    }

}
