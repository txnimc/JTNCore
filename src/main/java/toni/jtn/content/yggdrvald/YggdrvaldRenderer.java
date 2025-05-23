package toni.jtn.content.yggdrvald;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class YggdrvaldRenderer extends GeoEntityRenderer<Yggdrvald> {
    public YggdrvaldRenderer(EntityRendererProvider.Context context) {
        super(context, new YggdrvaldModel());
    }
}