package toni.jtn.foundation.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import toni.jtn.JTN;
import toni.jtn.content.runes.gem.Gem;
import toni.jtn.foundation.Registration;

public class JTNDatagen  implements DataGeneratorEntrypoint {

    @Override
    public String getEffectiveModId() {
        return JTN.ID;
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        GemProvider.bootstrap();

        var pack = fabricDataGenerator.createPack();
        pack.addProvider(LanguageProvider::new);
        pack.addProvider(((o, f) -> new GemProvider(o, f, PackOutput.Target.DATA_PACK, "gems", Gem.CODEC)));

        pack.addProvider((o, f) -> new FabricModelProvider(o) {
            @Override
            public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {

            }

            @Override
            public void generateItemModels(ItemModelGenerators itemModelGenerators) {
                GemProvider.gems.forEach(gem -> {
                    ModelTemplates.FLAT_ITEM.create(
                        JTN.location("item/gems/" + gem.id()),
                        TextureMapping.layer0(JTN.location("item/gems/" + gem.id())),
                        itemModelGenerators.output);
                });
            }
        });
    }
}