package toni.jtn.foundation;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.event.registry.RegistryIdRemapCallback;
import net.fabricmc.fabric.impl.registry.sync.ListenableRegistry;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import toni.jtn.JTN;
import toni.jtn.content.runes.LootCategory;
import toni.lib.utils.VersionUtils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class RegistryHelper {
    public <T extends Item> Holder<T> item(String path, Function<Item.Properties, T> constructor, Consumer<Item.Properties> configure) {
        var settings = new Item.Properties();
        configure.accept(settings);
        var item = constructor.apply(settings);

        Registry.register(BuiltInRegistries.ITEM, JTN.location(path), (Item) item);

        return Holder.direct(item);
    }


    public <T> DataComponentType<T> component(String path, UnaryOperator<DataComponentType.Builder<T>> operator) {
        DataComponentType<T> type = operator.apply(DataComponentType.builder()).build();

        Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            JTN.location(path),
            type
        );

        return type;
    }


    public <T> Registry<T> registry(String registryPath, RegistryEntryAddedCallback<T> added, RegistryIdRemapCallback<T> remap) {
        ResourceKey<Registry<T>> registryKey = ResourceKey.createRegistryKey(JTN.location(registryPath));

        var registry = new DefaultedMappedRegistry<>(JTN.location("none").toString(), registryKey, Lifecycle.stable(), false);

        if (remap != null)
            RegistryIdRemapCallback.event(registry).register(remap);

        if (added != null)
            RegistryEntryAddedCallback.event(registry).register(added);

        FabricRegistryBuilder.from(registry)
            .attribute(RegistryAttribute.MODDED)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

        return registry;
    }

    public <C extends RecipeInput, U extends Recipe<C>, T extends RecipeSerializer<U>> Holder<T> recipeSerializer(String path, Supplier<T> factory) {
        var recipe = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            JTN.location(path),
            factory.get()
        );

        return Holder.direct(recipe);
    }
}
