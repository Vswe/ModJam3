package vswe.stevesfactory.interfaces;


import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

public class ItemRenderHelper {

    private static RenderBlocks renderBlocks = new RenderBlocks();

    public static void renderItemIntoGUI(RenderItem renderItem, TextureManager textureManager, ItemStack item, int x, int y) {
        if (item == null || ForgeHooksClient.renderInventoryItem(renderBlocks, textureManager, item, renderItem.renderWithColor, renderItem.zLevel, (float)x, (float)y)) {
            return;
        }


        Item k = item.getItem();
        int l = item.getItemDamage();
        Object object = item.getIconIndex();
        float f;
        int i1;
        float f1;
        float f2;

        Block block = (k instanceof ItemBlock ? Block.getBlockFromItem(k) : null);
        if (item.getItemSpriteNumber() == 0 && block != null && RenderBlocks.renderItemIn3d(block.getRenderType())) {
            textureManager.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(x - 2), (float)(y + 3), -3.0F + renderItem.zLevel);
            GL11.glScalef(10.0F, 10.0F, 10.0F);
            GL11.glTranslatef(1.0F, 0.5F, 1.0F);
            GL11.glScalef(1.0F, 1.0F, -1.0F);
            GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            i1 = k.getColorFromItemStack(item, 0);
            f = (float)(i1 >> 16 & 255) / 255.0F;
            f1 = (float)(i1 >> 8 & 255) / 255.0F;
            f2 = (float)(i1 & 255) / 255.0F;

            if (renderItem.renderWithColor){
                GL11.glColor4f(f, f1, f2, 1.0F);
            }

            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            renderBlocks.useInventoryTint = renderItem.renderWithColor;
            renderBlocks.renderBlockAsItem(block, l, 1.0F);
            renderBlocks.useInventoryTint = true;
            GL11.glPopMatrix();
        }else if (k.requiresMultipleRenderPasses()){
            GL11.glDisable(GL11.GL_LIGHTING);

            for (int j1 = 0; j1 < k.getRenderPasses(l); ++j1)
            {
                textureManager.bindTexture(item.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
                IIcon icon = k.getIcon(item, j1);
                int k1 = k.getColorFromItemStack(item, j1);
                f1 = (float)(k1 >> 16 & 255) / 255.0F;
                f2 = (float)(k1 >> 8 & 255) / 255.0F;
                float f3 = (float)(k1 & 255) / 255.0F;

                if (renderItem.renderWithColor)
                {
                    GL11.glColor4f(f1, f2, f3, 1.0F);
                }

                renderItem.renderIcon(x, y, icon, 16, 16);

                if (item.hasEffect(j1))
                {
                    renderEffect(renderItem, textureManager, x, y);
                }
            }

            GL11.glEnable(GL11.GL_LIGHTING);
        }else{
            GL11.glDisable(GL11.GL_LIGHTING);
            ResourceLocation resourcelocation = textureManager.getResourceLocation(item.getItemSpriteNumber());
            textureManager.bindTexture(resourcelocation);

            if (object == null)
            {
                object = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
            }

            i1 = k.getColorFromItemStack(item, 0);
            f = (float)(i1 >> 16 & 255) / 255.0F;
            f1 = (float)(i1 >> 8 & 255) / 255.0F;
            f2 = (float)(i1 & 255) / 255.0F;

            if (renderItem.renderWithColor) {
                GL11.glColor4f(f, f1, f2, 1.0F);
            }

            renderItem.renderIcon(x, y, (IIcon)object, 16, 16);
            GL11.glEnable(GL11.GL_LIGHTING);

            if (item.hasEffect(0)) {
                renderEffect(renderItem, textureManager, x, y);
            }
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
    }


    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static void renderEffect(RenderItem renderItem, TextureManager manager, int x, int y) {
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        manager.bindTexture(RES_ITEM_GLINT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
        GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
        renderGlint(renderItem, x - 2, y - 2, 20, 20);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }

    private static void renderGlint(RenderItem renderItem, int par2, int par3, int par4, int par5) {
        for (int j1 = 0; j1 < 2; ++j1) {
            if (j1 == 0) {
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            }

            if (j1 == 1) {
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            }

            float f = 0.00390625F;
            float f1 = 0.00390625F;
            float f2 = (float)(Minecraft.getSystemTime() % (long)(3000 + j1 * 1873)) / (3000.0F + (float)(j1 * 1873)) * 256.0F;
            float f3 = 0.0F;
            Tessellator tessellator = Tessellator.instance;
            float f4 = 4.0F;

            if (j1 == 1){
                f4 = -1.0F;
            }

            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + par5), (double)renderItem.zLevel, (double)((f2 + (float)par5 * f4) * f), (double)((f3 + (float)par5) * f1));
            tessellator.addVertexWithUV((double)(par2 + par4), (double)(par3 + par5), (double)renderItem.zLevel, (double)((f2 + (float)par4 + (float)par5 * f4) * f), (double)((f3 + (float)par5) * f1));
            tessellator.addVertexWithUV((double)(par2 + par4), (double)(par3 + 0), (double)renderItem.zLevel, (double)((f2 + (float)par4) * f), (double)((f3 + 0.0F) * f1));
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)renderItem.zLevel, (double)((f2 + 0.0F) * f), (double)((f3 + 0.0F) * f1));
            tessellator.draw();
        }
    }
}
