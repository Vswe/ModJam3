package vswe.stevesfactory.interfaces;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import vswe.stevesfactory.StevesFactoryManager;

import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class GuiBase extends GuiLarge {
    public GuiBase(Container container) {
        super(container);
    }



    private static final ResourceLocation TERRAIN = new ResourceLocation("textures/atlas/blocks.png");

    protected abstract ResourceLocation getComponentResource();


    public int offsetX;
    public int offsetY;
    public void drawTexture(int x, int y, int srcX, int srcY, int w, int h) {
        drawTexturedModalRect(x + offsetX, y + offsetY, srcX, srcY, w, h);
    }


    public static void bindTexture(ResourceLocation resource)  {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public static ResourceLocation registerTexture(String name) {
        return new ResourceLocation(StevesFactoryManager.RESOURCE_LOCATION, "textures/gui/" +  name + ".png");
    }

    public void drawString(String str, int x, int y, float mult, int color) {
        drawScaledString(str, x + offsetX, y + offsetY, mult, color);
        bindTexture(getComponentResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void drawSplitString(String str, int x, int y, int w, float mult, int color) {
        drawScaledSplitString(str, x + offsetX, y + offsetY, w, mult, color);
        bindTexture(getComponentResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void drawString(String str, int x, int y, int color) {
        drawString(str, x, y, 1F, color);
    }

    public void drawMouseOver(String str, int x, int y) {
        drawMouseOver(Arrays.asList(str.split("\n")), x, y);
    }

    public void drawMouseOver(List lst, int x, int y) {
        drawHoveringText(lst, x + guiLeft + offsetX, y + guiTop + offsetY, fontRenderer);
    }


    public void drawBlock(TileEntity te, int x, int y) {
        ItemStack item = getItemStackFromBlock(te);
        if (item != null) {
            drawItemStack(item, x, y);
        }
    }


    public String getBlockName(TileEntity te) {
        ItemStack item = getItemStackFromBlock(te);

        if (item != null) {
            try {
                List str = item.getTooltip(Minecraft.getMinecraft().thePlayer, false);
                if (str != null && str.size() > 0) {
                    return (String)str.get(0);
                }
            }catch (Throwable ignored) {}
        }

        return "Unknown";
    }

    private ItemStack getItemStackFromBlock(TileEntity te) {
        World world = te.getWorldObj();
        Block block = te.getBlockType();
        if (world != null && block != null) {
            int x = te.xCoord;
            int y = te.yCoord;
            int z = te.zCoord;




            try {
                //try to get it by picking the block
                ItemStack item = block.getPickBlock(new MovingObjectPosition(x, y, z, 1, Vec3.createVectorHelper(x, y, z)), world, x, y, z);
                if (item != null) {
                    return item;
                }
            }catch (Throwable ignored) {}


            try{
                //try to get it from dropped items
                List<ItemStack> items = block.getBlockDropped(world, x, y, z, te.getBlockMetadata(), 0);
                if (items != null && items.size() > 0 && items.get(0) != null) {
                    return items.get(0);
                }
            }catch (Throwable ignored) {}



            //get it from its id and meta
            return  new ItemStack(block, 1, te.getBlockMetadata());
        }else{
            return null;
        }

    }

    public void drawItemStack(ItemStack itemstack, int x, int y) {
        GL11.glPushMatrix();

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_LIGHTING);

        itemRenderer.zLevel = 1F;


        try {
            ItemRenderHelper.renderItemIntoGUI(itemRenderer, this.mc.getTextureManager(), itemstack, x + guiLeft + offsetX, y + guiTop + offsetY);
        }catch (Exception ex) {
            if (itemstack.getItemDamage() != 0) {
                ItemStack newStack = itemstack.copy();
                newStack.setItemDamage(0);
                drawItemStack(newStack, x, y);
            }
        }finally {
            itemRenderer.zLevel = 0F;

            bindTexture(getComponentResource());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);

            GL11.glPopMatrix();
        }

    }

    public int getStringWidth(String str) {
        return fontRenderer.getStringWidth(str);
    }


    public void drawCenteredString(String str, int x, int y, float mult, int width, int color) {
        drawString(str, x + (width - (int)(getStringWidth(str) * mult)) / 2, y, mult, color);
    }

    public void drawCursor(int x, int y, int z, int color) {
        drawCursor(x, y, z, 1F, color);
    }
    //TODO
    public void drawCursor(int x, int y, int z, float size, int color) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, z);
        x += guiLeft;
        y += guiTop;
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(size, size, 0);
        GL11.glTranslatef(-x, -y, 0);
        Gui.drawRect(x, y + 1, x + 1, y + 10, color);
        GL11.glPopMatrix();
    }

   //TODO
    public void drawLine(int x1, int y1, int x2, int y2) {
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0.4F, 0.4F, 0.4F, 1F);

        //GL11.glEnable(GL11.GL_BLEND);
        //GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(5);

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2i(guiLeft + x1, guiTop + y1);
        GL11.glVertex2i(guiLeft + x2, guiTop + y2);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }





    //TODO
    public void drawIcon(Icon icon, int x, int y) {
        drawTexturedModelRectFromIcon(guiLeft + x, guiTop + y, icon, 16, 16);
    }

    public void drawFluid(Fluid fluid, int x, int y) {


        Icon icon = fluid.getIcon();

        if (icon == null) {
            if (FluidRegistry.WATER.equals(fluid)) {
                icon = Block.waterStill.getIcon(0, 0);
            }else if(FluidRegistry.LAVA.equals(fluid)) {
                icon = Block.lavaStill.getIcon(0, 0);
            }
        }

        if (icon != null) {
            bindTexture(TERRAIN);
            setColor(fluid.getColor());

            drawIcon(icon, x, y);

            GL11.glColor4f(1F, 1F, 1F, 1F);
            bindTexture(getComponentResource());
        }
    }

    private void setColor(int color) {
        float[] colorComponents = new float[3];
        for (int i = 0; i < colorComponents.length; i++) {
            colorComponents[i] = ((color & (255 << (i * 8))) >> (i * 8)) / 255F;
        }
        GL11.glColor4f(colorComponents[2], colorComponents[1], colorComponents[0], 1F);
    }

}
