package toni.jtn.content.yggdrvald;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.resources.ResourceLocation;
import toni.lib.utils.VersionUtils;

public class YggdrvaldModel extends GeoModel<Yggdrvald> {
    private static final ResourceLocation model = VersionUtils.resource("jtn", "geo/yggdrvald/yggdrvald.geo.json");
    private static final ResourceLocation texture = VersionUtils.resource("jtn", "textures/entity/yggdrvald/yggdrvald.png");
    private static final ResourceLocation animation = VersionUtils.resource("jtn", "animations/yggdrvald/yggdrvald.animation.json");

    @Override
    public ResourceLocation getModelResource(Yggdrvald object) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(Yggdrvald object) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(Yggdrvald object) {
        return this.animation;
    }
}