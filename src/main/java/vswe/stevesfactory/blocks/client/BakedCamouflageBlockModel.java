package vswe.stevesfactory.blocks.client;

import com.google.common.base.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.util.vector.Vector3f;
import vswe.stevesfactory.blocks.BlockCableCamouflages;
import vswe.stevesfactory.blocks.BlockCableCluster;
import vswe.stevesfactory.blocks.TileEntityCamouflage;
import vswe.stevesfactory.blocks.TileEntityCluster;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static vswe.stevesfactory.blocks.TileEntityCamouflage.CamouflageType;

public class BakedCamouflageBlockModel implements IFlexibleBakedModel, ISmartBlockModel {

    private VertexFormat format;
    private TextureAtlasSprite normalSprite;
    private TextureAtlasSprite insideSprite;
    private TextureAtlasSprite transformSprite;
    private TextureAtlasSprite clusterFront;
    private TextureAtlasSprite clusterSide;
    private TextureAtlasSprite clusterFrontAdv;
    private TextureAtlasSprite clusterSideAdv;

    private FaceBakery bakery;
    private Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
    private IModelState modelState;

    public BakedCamouflageBlockModel(IModelState modelState, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, boolean isCamouflage) {
        this.format = format;

        this.bakedTextureGetter = bakedTextureGetter;
        this.modelState = modelState;
        normalSprite = bakedTextureGetter.apply(CamouflageBlockModel.NORMAL);
        insideSprite = bakedTextureGetter.apply(CamouflageBlockModel.INSIDE);
        transformSprite = bakedTextureGetter.apply(CamouflageBlockModel.TRANSFORM);

        if (!isCamouflage) {
            clusterFront = bakedTextureGetter.apply(CamouflageBlockModel.CL_FRONT);
            clusterSide = bakedTextureGetter.apply(CamouflageBlockModel.CL_SIDE);
            clusterFrontAdv = bakedTextureGetter.apply(CamouflageBlockModel.CL_ADV_FRONT);
            clusterSideAdv = bakedTextureGetter.apply(CamouflageBlockModel.CL_ADV_SIDE);
        }

        bakery = new FaceBakery();
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing side) {
        //This should never be called!  The handleBlockState returns an AssembledBakedModel
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        //This should never be called!  The handleBlockState returns an AssembledBakedModel
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return normalSprite;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public VertexFormat getFormat() {
        return format;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState blockState = (IExtendedBlockState) state;
            Object obj = blockState.getValue(BlockCableCamouflages.BLOCK_POS);

            if (obj != null) {

                BlockPos pos = (BlockPos) obj;
                BlockRendererDispatcher dispatcher = FMLClientHandler.instance().getClient().getBlockRendererDispatcher();
                BlockModelShapes modelShapes = dispatcher.getBlockModelShapes();

                TileEntity tileEntity = FMLClientHandler.instance().getWorldClient().getTileEntity(pos);
                TileEntityCamouflage camouflage = null;
                TileEntityCluster cluster = null;

                if (tileEntity instanceof TileEntityCluster) {
                    cluster = (TileEntityCluster) tileEntity;
                    camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, FMLClientHandler.instance().getWorldClient(), pos);

                    BlockCableCluster blockCluster = (BlockCableCluster) cluster.getBlockType();

                    if (camouflage == null) {
                        IModel clusterModel = null;
                        try {
                            clusterModel = ModelLoaderRegistry.getModel(blockCluster.isAdvanced(cluster.getBlockMetadata()) ? CamouflageBlockModel.MODEL_CLUSTER_ADV : CamouflageBlockModel.MODEL_CLUSTER);
                        } catch (IOException ignored) {}

                        if (clusterModel != null) {
                            return clusterModel.bake(modelState, format, bakedTextureGetter);
                        }
                    }
                } else if (tileEntity instanceof TileEntityCamouflage) {
                    camouflage = (TileEntityCamouflage) tileEntity;
                }
                return new AssembledBakedModel(camouflage, pos, blockState, modelShapes, cluster);
            }
        }

        return new AssembledBakedModel();
    }

    //Apparently it needs to be separate because it could be overridden my another thread as rendering is multithreaded
    //https://github.com/TheGreyGhost/MinecraftByExample/blob/master/src/main/java/minecraftbyexample/mbe05_block_smartblockmodel2/CompositeModel.java
    public class AssembledBakedModel implements IBakedModel
    {

        private List<BakedQuad> quads = new LinkedList<BakedQuad>();

        public AssembledBakedModel(TileEntityCamouflage camouflage, BlockPos pos, IExtendedBlockState blockState, BlockModelShapes modelShapes, TileEntityCluster cluster) {
            if (camouflage != null && quads.isEmpty()) {

                for (EnumFacing facing: EnumFacing.values()) {
                    Block block = Block.getBlockById(camouflage.getId(facing.getIndex()));
                    Block insideBlock = Block.getBlockById(camouflage.getId(facing.getIndex() + EnumFacing.values().length));

                    generateQuads(block, pos, blockState, modelShapes, camouflage, facing, false, cluster);
                    if (camouflage.getCamouflageType().useDoubleRendering()) {
                        generateQuads(insideBlock, pos, blockState, modelShapes, camouflage, facing, true, cluster);
                    }
                }
            }
        }

        public AssembledBakedModel() {

        }

        public void generateQuads(Block block, BlockPos pos, IExtendedBlockState blockState, BlockModelShapes modelShapes, TileEntityCamouflage camouflage, EnumFacing facing, boolean inside, TileEntityCluster cluster) {
            if (block instanceof BlockAir || block instanceof BlockCableCamouflages || block instanceof BlockCableCluster) {
                if (cluster == null) {
                    CamouflageType camoType = (CamouflageType) blockState.getValue(BlockCableCamouflages.CAMO_TYPE);
                    quads.add(getTransformedQuad(pos, blockState.getBlock(), facing, camoType.getIcon(), camoType == CamouflageType.NORMAL && !inside ? normalSprite : camoType == CamouflageType.INSIDE ? insideSprite: transformSprite, inside));
                } else {
                    BlockCableCluster blockCluster = (BlockCableCluster) cluster.getBlockType();
                    int clusterMeta = cluster.getBlockMetadata();
                    EnumFacing clusterFacing = blockCluster.getSide(clusterMeta);
                    boolean isFacingFront = ((camouflage != null && camouflage.getCamouflageType().useSpecialShape() && inside) || (camouflage == null && inside)) ? (facing == clusterFacing.getOpposite()): (facing == clusterFacing);
                    boolean isAdvanced = blockCluster.isAdvanced(clusterMeta);

                    String resource = (isAdvanced ? (isFacingFront ? CamouflageBlockModel.CL_ADV_FRONT: CamouflageBlockModel.CL_ADV_SIDE): isFacingFront ? CamouflageBlockModel.CL_FRONT: CamouflageBlockModel.CL_SIDE).toString();
                    TextureAtlasSprite texture = isAdvanced ? (isFacingFront ? clusterFrontAdv: clusterSideAdv): isFacingFront ? clusterFront: clusterSide;

                    quads.add(getTransformedQuad(pos, blockState.getBlock(), facing, resource, texture, inside));
                }
            } else {

                IBlockState camoState = block.getStateFromMeta(camouflage.getMeta(facing.getIndex() + (inside ? EnumFacing.values().length: 0)));
                IBakedModel model = modelShapes.getModelForState(camoState);

                List<BakedQuad> bakedQuads = model.getFaceQuads(facing);
                List<BakedQuad> reBakedQuads = new LinkedList<BakedQuad>();
                TextureAtlasSprite sprite = modelShapes.getTexture(camoState);


                if (bakedQuads.isEmpty()) {
                    bakedQuads = model.getGeneralQuads();
                }

                for (BakedQuad quad :bakedQuads) {
                    if (quad.getFace() == facing) {
                        if (camouflage.getCamouflageType().useDoubleRendering()) {
                            quad = reBakeQuadForBlock(quad, pos, blockState.getBlock(), facing, sprite, inside);
                        }
                        reBakedQuads.add(quad);
                    }
                }

                quads.addAll(reBakedQuads);
            }
        }

        private BakedQuad getTransformedQuad(BlockPos pos, Block block, EnumFacing facing, String resource, TextureAtlasSprite sprite, boolean inside) {

            block.setBlockBoundsBasedOnState(FMLClientHandler.instance().getWorldClient(), pos);

            float maxX = (((float)block.getBlockBoundsMaxX()) * 16f);
            float maxY = (((float)block.getBlockBoundsMaxY()) * 16f);
            float maxZ = (((float)block.getBlockBoundsMaxZ()) * 16f);
            float minX = (((float)block.getBlockBoundsMinX()) * 16f);
            float minY = (((float)block.getBlockBoundsMinY()) * 16f);
            float minZ = (((float)block.getBlockBoundsMinZ()) * 16f);

            BlockFaceUV faceUV = null;
            float f = 0.002F;

            if (inside) {
                float temp = maxY;
                maxY = 16 - minY;
                minY = 16 - temp;

                temp = maxZ;
                maxZ = 16 - minZ;
                minZ = 16 - temp;
            }

            switch (facing) {

                case DOWN:
                    if (!inside) {
                        faceUV = new BlockFaceUV(new float[]{16 - minX, 16 - maxZ, 16 - maxX, 16 - minZ}, 0);
                    } else {
                        faceUV = new BlockFaceUV(new float[]{16 - minX, 16 - minZ, 16 - maxX, 16 - maxZ}, 0);
                        maxY = minY + f;
                    }
                    break;
                case UP:
                    if (!inside) {
                        faceUV = new BlockFaceUV(new float[]{minX, minZ, maxX, maxZ}, 0);
                    } else {
                        faceUV = new BlockFaceUV(new float[]{minX, maxZ, maxX, minZ}, 0);
                        minY = maxY - f;
                    }
                    break;
                case NORTH:
                    if (!inside) {
                        faceUV = new BlockFaceUV(new float[]{16 - maxX, 16 - maxY, 16 - minX, 16 - minY}, 0);
                    } else {
                        faceUV = new BlockFaceUV(new float[]{16 - minX, maxY, 16 - maxX, minY}, 0);
                        maxZ = minZ + f;
                    }
                    break;
                case SOUTH:
                    if (!inside) {
                        faceUV = new BlockFaceUV(new float[]{minX, 16 - maxY, maxX, 16 - minY}, 0);
                    } else {
                        faceUV = new BlockFaceUV(new float[]{maxX, maxY, minX, minY}, 0);
                        minZ = maxZ - f;
                    }
                    break;
                case WEST:
                    if (!inside) {
                        faceUV = new BlockFaceUV(new float[]{minZ, 16 - maxY, maxZ, 16 - minY}, 0);
                    } else {
                        faceUV = new BlockFaceUV(new float[]{maxZ, maxY, minZ, minY}, 0);
                        maxX = minX + f;
                    }
                    break;
                case EAST:
                    if (!inside) {
                        faceUV = new BlockFaceUV(new float[]{16 - maxZ, 16 - maxY, 16 - minZ, 16 - minY}, 0);
                    } else {
                        faceUV = new BlockFaceUV(new float[]{16 - minZ, maxY, 16 - maxZ, minY}, 0);
                        minX = maxX - f;
                    }
                    break;
            }

            return bakery.makeBakedQuad(new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ), new BlockPartFace(facing, -1, resource == null ? "": resource, faceUV), sprite, inside ? facing.getOpposite(): facing, inside ? ModelRotation.X180_Y0: ModelRotation.X0_Y0, null, false, true);
        }

        private BakedQuad reBakeQuadForBlock(BakedQuad original, BlockPos pos, Block block, EnumFacing facing, TextureAtlasSprite sprite, boolean inside) {
            BakedQuad transformedQuad = getTransformedQuad(pos, block, facing, null, sprite, inside);
            int[] transformedFaceData = transformedQuad.getVertexData();

            int tintIndex = original.getTintIndex();
            EnumFacing face = original.getFace();
            int[] faceData = original.getVertexData().clone();

            for (int i = 0; i < 4; i++) {
                int storeIndex = i * 7;

                float[] d = new float[3];

                for (int j = 0; j < 3; j++) {
                    d[j] = (Float.intBitsToFloat(faceData[storeIndex + j]) * 16) - (Float.intBitsToFloat(transformedFaceData[storeIndex + j]) * 16);
                }

                faceData[storeIndex] = transformedFaceData[storeIndex];
                faceData[storeIndex + 1] = transformedFaceData[storeIndex + 1];
                faceData[storeIndex + 2] = transformedFaceData[storeIndex + 2];
                faceData[storeIndex + 4] = transformedFaceData[storeIndex + 4];
                faceData[storeIndex + 5] = transformedFaceData[storeIndex + 5];
            /*
            faceData[storeIndex + 4] = getDeltaU(faceData[storeIndex + 4], d, facing, storeIndex / 7);
            faceData[storeIndex + 5] = getDeltaV(faceData[storeIndex + 5], d, facing, storeIndex / 7);*/
            }

            return new BakedQuad(faceData, tintIndex, face);
        }

        //Attempt to get the correct side the block, left for future reference
    /*private int getSign(float i) {
        return i < 0 ? -1: 1;
    }

    private int getDeltaU(int origU, float[] delta, EnumFacing facing, int corner) {
        int d = 0;
        float d1 = delta[0];
        float d2 = delta[2];
        switch (facing) {

            case DOWN:
            case UP:
                d = Math.abs(d1) > 0 ? getSign(d1) * 261817 + 261816 * ((int)d1 - 1): 0;
                break;
            case NORTH:
            case SOUTH:
                d = Math.abs(d1) > 0 ? getSign(d1) * 261817 + 261816 * ((int)d1 - 1): 0;
                break;
            case WEST:
            case EAST:
                d = Math.abs(d2) > 0 ? getSign(d2) * 261817 + 261816 * ((int)d2 - 1): 0;
                break;
        }

        return origU - d;
    }

    private int getDeltaV(int origV, float[] delta, EnumFacing facing, int corner) {
        int d = 0;
        float d1 = delta[1];
        float d2 = delta[2];
        int sign = getSign(d1);
        int sign2 = getSign(d2);
        switch (facing) {
            case DOWN:
            case UP:
                d = Math.abs(d2) > 1 ? ((sign2 == -1 && corner != 0 && corner != 1) ? sign2: 0) * 261817 + 261816 * ((int)d2 - 1): 0;
                break;
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                d = Math.abs(d1) >= 0 ? ((sign != -1 && (corner == 0 || corner == 3)) ? 261816 * 2: (sign == -1 && (corner == 1 || corner == 2)) ? 261816 / ((int)Math.abs(d1)): 0): 0;
                break;
        }
        if (facing == EnumFacing.EAST) System.out.println("D:" + d + "; " + d1 + "; s:" + sign + "; c:" + corner + "; m:" + ((int)d1 - sign));

        return origV - d;
    }*/

        @Override
        public List getFaceQuads(EnumFacing side) {
            List<BakedQuad> allFaceQuads = new LinkedList<BakedQuad>();

            for (BakedQuad quad: quads) {
                if (FaceBakery.getFacingFromVertexData(quad.getVertexData()) == side) {
                    allFaceQuads.add(quad);
                }
            }

            return allFaceQuads;
        }

        @Override
        public List getGeneralQuads() {
            return new LinkedList<BakedQuad>(quads);
        }

        @Override
        public boolean isAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return normalSprite;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return ItemCameraTransforms.DEFAULT;
        }
    }
}
