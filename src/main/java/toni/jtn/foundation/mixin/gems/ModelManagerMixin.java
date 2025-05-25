package toni.jtn.foundation.mixin.gems;

import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.jtn.JTN;
import toni.jtn.content.runes.gem.GemModel;

import java.util.Map;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {

    @Inject(method = "loadModels", at = @At(value = "INVOKE", target = "net/minecraft/util/profiling/ProfilerFiller.popPush (Ljava/lang/String;)V", ordinal = 1))
    private void jtn$initGemModel(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, ModelBakery modelBakery, CallbackInfoReturnable<ModelManager.ReloadState> cir){
        ModelResourceLocation key = new ModelResourceLocation(JTN.location("gem"), "inventory");
        BakedModel oldModel = modelBakery.getBakedTopLevelModels().get(key);
        if (oldModel != null) {
            modelBakery.getBakedTopLevelModels().put(key, new GemModel(oldModel, modelBakery));
        }
    }
}