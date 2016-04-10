package vswe.stevesfactory.blocks.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;

public class EmitterBlockModel implements IModel{

    public EmitterBlockModel(IResourceManager resourceManager) {

    }

    public static final ResourceLocation EMITTER_MODEL = new ResourceLocation("stevesfactorymanager:block/cable_emitter");

    public static final ResourceLocation STRONG = new ResourceLocation("stevesfactorymanager:blocks/cable_output_strong");
    public static final ResourceLocation WEAK = new ResourceLocation("stevesfactorymanager:blocks/cable_output_weak");
    public static final ResourceLocation IDLE = new ResourceLocation("stevesfactorymanager:blocks/cable_idle");

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.copyOf(new ResourceLocation[]{EMITTER_MODEL});
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableList.copyOf(new ResourceLocation[]{STRONG, WEAK, IDLE});
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new BakedEmitterBlockModel(bakedTextureGetter);
    }

    @Override
    public IModelState getDefaultState() {
        return null;
    }
}
