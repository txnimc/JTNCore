package toni.jtn.mixin.gems;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toni.jtn.JTN;

import java.util.Map;
import java.util.Set;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow
    @Final
    private Map<ModelResourceLocation, UnbakedModel> unbakedCache;

    @Shadow
    @Final
    private Map<ModelResourceLocation, UnbakedModel> topLevelModels;

    @Shadow abstract UnbakedModel getModel(ResourceLocation modelLocation);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadSpecialItemModelAndDependencies(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V", shift = At.Shift.AFTER))
    private void jtn$initCustomModels(BlockColors blockColors, ProfilerFiller profilerFiller, Map map, Map map2, CallbackInfo ci){
        Set<ModelResourceLocation> extraModels = Sets.newHashSet();

        Set<ResourceLocation> locs = Minecraft.getInstance()
            .getResourceManager()
            .listResources("models", loc -> JTN.ID.equals(loc.getNamespace()) && loc.getPath().contains("/gems/") && loc.getPath().endsWith(".json"))
            .keySet();

        for (ResourceLocation s : locs) {
            String path = s.getPath().substring("models/".length(), s.getPath().length() - ".json".length());
            extraModels.add(ModelResourceLocation.inventory(JTN.location(path)));
        }
//
//        extraModels.add(ReforgingTableTileRenderer.HAMMER);
//        extraModels.add(AugmentingTableTileRenderer.STAR_CUBE);

        for (ModelResourceLocation resourceLocation : extraModels) {
            UnbakedModel unbakedmodel = this.getModel(resourceLocation.id());
            unbakedCache.put(resourceLocation, unbakedmodel);
            topLevelModels.put(resourceLocation, unbakedmodel);
        }
    }
}