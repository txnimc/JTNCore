package toni.jtn.content.runes.gem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import toni.jtn.JTN;
import toni.jtn.foundation.registry.DynamicHolder;

import java.util.List;

public class GemModel implements BakedModel {
    private final BakedModel original;
    private final ItemOverrides overrides;

    @SuppressWarnings("deprecation")
    public GemModel(BakedModel original, ModelBakery loader) {
        this.original = original;
        this.overrides = new ItemOverrides(){
            @Override
            public BakedModel resolve(BakedModel original, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
                return GemModel.this.resolve(original, stack, world, entity, seed);
            }
        };
    }

    public BakedModel resolve(BakedModel original, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        DynamicHolder<Gem> gem = GemItem.getGem(stack);
        if (gem.isBound()) {
            return Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.inventory(JTN.location("item/gems/" + gem.getId().getPath())));
        }
        return original;
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.overrides;
    }

    @Override
    @Deprecated
    public List<BakedQuad> getQuads(BlockState pState, Direction pDirection, RandomSource pRandom) {
        return this.original.getQuads(pState, pDirection, pRandom);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.original.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.original.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.original.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.original.isCustomRenderer();
    }

    @Override
    @Deprecated
    public TextureAtlasSprite getParticleIcon() {
        return this.original.getParticleIcon();
    }

    @Override
    @Deprecated
    public ItemTransforms getTransforms() {
        return this.original.getTransforms();
    }
}