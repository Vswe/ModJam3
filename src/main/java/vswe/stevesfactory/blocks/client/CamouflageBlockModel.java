package vswe.stevesfactory.blocks.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;

import java.util.Collection;

public class CamouflageBlockModel implements IModel {

    public static final ResourceLocation MODEL = new ResourceLocation("stevesfactorymanager:block/cable_camouflage");
    public static final ResourceLocation MODEL_CLUSTER = new ResourceLocation("stevesfactorymanager:block/cable_cluster");
    public static final ResourceLocation MODEL_CLUSTER_ADV = new ResourceLocation("stevesfactorymanager:block/cable_cluster_advanced");

    public static final ResourceLocation NORMAL = new ResourceLocation("stevesfactorymanager:blocks/cable_camo");
    public static final ResourceLocation INSIDE = new ResourceLocation("stevesfactorymanager:blocks/cable_camo_inside");
    public static final ResourceLocation TRANSFORM = new ResourceLocation("stevesfactorymanager:blocks/cable_camo_shape");

    public static final ResourceLocation CL_SIDE = new ResourceLocation("stevesfactorymanager:blocks/cable_cluster_front");
    public static final ResourceLocation CL_ADV_SIDE = new ResourceLocation("stevesfactorymanager:blocks/cable_cluster_adv");
    public static final ResourceLocation CL_FRONT = new ResourceLocation("stevesfactorymanager:blocks/cable_cluster_front");
    public static final ResourceLocation CL_ADV_FRONT = new ResourceLocation("stevesfactorymanager:blocks/cable_cluster_adv_front");

    private boolean isCamouflage;

    public CamouflageBlockModel(IResourceManager resourceManager, boolean isCamouflage) {
        this.isCamouflage = isCamouflage;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return isCamouflage ? ImmutableList.copyOf(new ResourceLocation[]{MODEL}): ImmutableList.copyOf(new ResourceLocation[]{MODEL, MODEL_CLUSTER, MODEL_CLUSTER_ADV});
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return isCamouflage ? ImmutableList.copyOf(new ResourceLocation[]{NORMAL, INSIDE, TRANSFORM}): ImmutableList.copyOf(new ResourceLocation[]{NORMAL, INSIDE, TRANSFORM, CL_SIDE, CL_FRONT, CL_ADV_SIDE, CL_ADV_FRONT});
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new BakedCamouflageBlockModel(state, format, bakedTextureGetter, isCamouflage);
    }

    @Override
    public IModelState getDefaultState() {
        return null;
    }
}
