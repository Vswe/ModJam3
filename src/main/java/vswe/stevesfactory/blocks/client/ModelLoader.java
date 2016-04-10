package vswe.stevesfactory.blocks.client;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import vswe.stevesfactory.StevesFactoryManager;

public class ModelLoader implements ICustomModelLoader {

    private IResourceManager resourceManager;

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getResourceDomain().equals(StevesFactoryManager.MODID.toLowerCase()) && modelLocation.getResourcePath().startsWith("models/block/builtin/");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) {
        String model = modelLocation.getResourcePath().substring("models/block/builtin/".length());
        if (model.equals("cable_emitter")) {
            return new EmitterBlockModel(resourceManager);
        } else if(model.equals("cable_camouflage") || model.equals("cable_cluster")) {
            return new CamouflageBlockModel(resourceManager, model.equals("cable_camouflage"));
        } else {
            return ModelLoaderRegistry.getMissingModel();
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
}
