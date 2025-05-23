package toni.jtn.foundation.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import toni.jtn.JTN;
import toni.jtn.foundation.config.AllConfigs;

import java.util.concurrent.CompletableFuture;

public class ConfigLangDatagen extends FabricLanguageProvider {
    protected ConfigLangDatagen(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        AllConfigs.generateTranslations(translationBuilder);
    }

    @Override
    public String getName() {
        return "JTN Data Gen";
    }
}