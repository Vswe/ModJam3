package vswe.stevesfactory.interfaces;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.blocks.TileEntityClusterElement;
import vswe.stevesfactory.settings.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class GuiBase extends GuiAntiNEI {
    public GuiBase(Container container) {
        super(container);
    }

    private static final ResourceLocation TERRAIN = new ResourceLocation("textures/atlas/blocks.png");

    public abstract ResourceLocation getComponentResource();

    public void drawTexture(int x, int y, int srcX, int srcY, int w, int h) {
        float scale = getScale();

        drawScaleFriendlyTexture(
                fixScaledCoordinate(guiLeft + x, scale, this.mc.displayWidth),
                fixScaledCoordinate(guiTop + y, scale, this.mc.displayHeight),
                fixScaledCoordinate(srcX, scale, 256),
                fixScaledCoordinate(srcY, scale, 256),
                fixScaledCoordinate(w, scale, this.mc.displayWidth),
                fixScaledCoordinate(h, scale, this.mc.displayHeight)
        );
    }

    private double fixScaledCoordinate(int val, float scale, int size) {
        double d = val / scale;
        d *= size;
        d = Math.floor(d);
        d /= size;
        d *= scale;

        return d;
    }

    public void drawScaleFriendlyTexture(double x, double y, double srcX, double srcY, double w, double h) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + h, (double)this.zLevel, (srcX + 0) * f, (srcY + h) * f1);
        tessellator.addVertexWithUV(x + w, y + h, (double)this.zLevel, (srcX + w) * f, (srcY + h) * f1);
        tessellator.addVertexWithUV(x + w, y + 0, (double)this.zLevel, (srcX + w) * f, (srcY + 0) * f1);
        tessellator.addVertexWithUV(x + 0, y + 0, (double)this.zLevel, (srcX + 0) * f, (srcY + 0) * f1);
        tessellator.draw();
    }

    public static void bindTexture(ResourceLocation resource)  {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public static ResourceLocation registerTexture(String name) {
        return new ResourceLocation(StevesFactoryManager.RESOURCE_LOCATION, "textures/gui/" +  name + ".png");
    }

    public void drawString(String str, int x, int y, float mult, int color) {
        GL11.glPushMatrix();
        GL11.glScalef(mult, mult, 1F);
        fontRendererObj.drawString(str, (int) ((x + guiLeft) / mult), (int) ((y + guiTop) / mult), color);
        bindTexture(getComponentResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();
    }

    public void drawSplitString(String str, int x, int y, int w, float mult, int color) {
        GL11.glPushMatrix();
        GL11.glScalef(mult, mult, 1F);
        fontRendererObj.drawSplitString(str, (int)((x + guiLeft) / mult), (int)((y + guiTop) / mult), (int)(w / mult), color);
        bindTexture(getComponentResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();
    }

    public void drawString(String str, int x, int y, int color) {
        drawString(str, x, y, 1F, color);
    }

    public void drawMouseOver(String str, int x, int y, int width) {


        drawMouseOver(getLinesFromText(str, width), x, y);
    }

    public List<String> getLinesFromText(String str, int width) {
        List<String> lst = new ArrayList<String>();
        String[] words = str.split(" ");
        String line = "";
        for (String word : words) {
            String newLine;
            if (line.equals("")) {
                newLine = word;
            }else{
                newLine = line + " " + word;
            }
            if (getStringWidth(newLine) < width) {
                line = newLine;
            }else{
                lst.add(line);
                line = word;
            }
        }
        lst.add(line);

        return lst;
    }

    private int getStringMaxWidth(List<String> lines) {
        int width = 0;

        if (lines != null) {
            for (String line : lines) {
                int w = fontRendererObj.getStringWidth(line);

                if (w > width) {
                    width = w;
                }
            }
        }

        return width;
    }

    private int renderLines(List<String> lines, int mX, int mY, boolean first) {
        return renderLines(lines, mX, mY, first, false);
    }
    private int renderLines(List<String> lines, int mX, int mY, boolean first, boolean fake) {
        if (lines != null) {
            for (String line : lines) {
                if (!fake) {
                    fontRendererObj.drawStringWithShadow(line, mX, mY, -1);
                }

                if (first){
                    mY += 2;
                    first = false;
                }

                mY += 10;
            }
        }

        return mY;
    }

    public void drawMouseOver(IAdvancedTooltip tooltip, int mX, int mY) {
        drawMouseOver(tooltip, mX, mY, mX, mY);
    }
    public void drawMouseOver(IAdvancedTooltip tooltip, int x, int y, int mX, int mY) {
        if (tooltip != null) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);


            List<String> prefix = tooltip.getPrefix(this);
            List<String> suffix = tooltip.getSuffix(this);
            int prefixLength = prefix != null ? prefix.size() : 0;
            int suffixLength = suffix != null ? suffix.size() : 0;
            int extraHeight = tooltip.getExtraHeight(this);

            int width = Math.max(tooltip.getMinWidth(this), Math.max(getStringMaxWidth(prefix), getStringMaxWidth(suffix)));
            int height = extraHeight + (prefixLength + suffixLength) * 10 - 2;


            x += guiLeft + 12;
            y += guiTop - 12;
            if (x + width > this.width){
                x -= 28 + width;
            }
            if (y + height + 6 > this.height){
                y = this.height - height - 6;
            }


            this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
            int j1 = -267386864;
            this.drawGradientRect(x - 3, y - 4, x + width + 3, y - 3, j1, j1);
            this.drawGradientRect(x - 3, y + height + 3, x + width + 3, y + height + 4, j1, j1);
            this.drawGradientRect(x - 3, y - 3, x + width + 3, y + height + 3, j1, j1);
            this.drawGradientRect(x - 4, y - 3, x - 3, y + height + 3, j1, j1);
            this.drawGradientRect(x + width + 3, y - 3, x + width + 4, y + height + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            this.drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, k1, l1);
            this.drawGradientRect(x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, k1, l1);
            this.drawGradientRect(x - 3, y - 3, x + width + 3, y - 3 + 1, k1, k1);
            this.drawGradientRect(x - 3, y + height + 2, x + width + 3, y + height + 3, l1, l1);

            y = renderLines(prefix, x, y, true);
            tooltip.drawContent(this, x - guiLeft, y - guiTop, mX, mY);
            y += extraHeight;
            renderLines(suffix, x, y, false);

            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }
    public int getAdvancedToolTipContentStartX(IAdvancedTooltip tooltip) {
        return 12;
    }
    public int getAdvancedToolTipContentStartY(IAdvancedTooltip tooltip) {
        if (tooltip != null) {
            return renderLines(tooltip.getPrefix(this), 0, 0, true, true) - 12;
        }

        return 0;
    }



    public void drawMouseOver(String str, int x, int y) {
        drawMouseOver(Arrays.asList(str.split("\n")), x, y);
    }

    public void drawMouseOver(List lst, int x, int y) {
        if (lst != null) {
            drawHoveringText(lst, x + guiLeft, y + guiTop, fontRendererObj);
        }
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
            return getItemName(item);
        }

        return "Unknown";
    }

    public String getItemName(ItemStack item) {
        try {
            List str = item.getTooltip(Minecraft.getMinecraft().thePlayer, false);
            if (str != null && str.size() > 0) {
                return (String)str.get(0);
            }
        }catch (Throwable ignored) {}

        return "Unknown";
    }

    private ItemStack getItemStackFromBlock(TileEntity te) {
        if (te != null) {
            if (te instanceof TileEntityClusterElement) {
                return ((TileEntityClusterElement)te).getItemStackFromBlock();
            }

            World world = te.getWorldObj();
            Block block = te.getBlockType();
            if (world != null && block != null) {
                int x = te.xCoord;
                int y = te.yCoord;
                int z = te.zCoord;

                return getItemStackFromBlock(world, x, y, z, block, world.getBlockMetadata(x, y, z));
            }
        }

        return null;
    }

    public ItemStack getItemStackFromBlock(World world, int x, int y, int z) {
        if (world != null) {
            Block block = world.getBlock(x, y, z);
            if (block != null) {
                return getItemStackFromBlock(world, x, y, z, block, world.getBlockMetadata(x, y, z));
            }
        }

        return null;
    }

    private ItemStack getItemStackFromBlock(World world, int x, int y, int z, Block block, int meta) {
        try {
            //try to get it by picking the block
            ItemStack item = block.getPickBlock(new MovingObjectPosition(x, y, z, 1, Vec3.createVectorHelper(x, y, z)), world, x, y, z);
            if (item != null) {
                return item;
            }
        }catch (Throwable ignored) {}


        try{
            //try to get it from dropped items
            List<ItemStack> items = block.getDrops(world, x, y, z, meta, 0);
            if (items != null && items.size() > 0 && items.get(0) != null) {
                return items.get(0);
            }
        }catch (Throwable ignored) {}



        //get it from its id and meta
        return  new ItemStack(block, 1, meta);
    }

    public void drawItemAmount(ItemStack itemstack, int x, int y) {
        itemRender.renderItemOverlayIntoGUI(fontRendererObj, this.mc.getTextureManager(), itemstack, x + guiLeft, y + guiTop);
        bindTexture(getComponentResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public void drawItemStack(ItemStack itemstack, int x, int y) {
        GL11.glPushMatrix();

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_LIGHTING);

        itemRender.zLevel = 1F;


        try {
            itemRender.renderItemIntoGUI(fontRendererObj, this.mc.getTextureManager(), itemstack, x + guiLeft, y + guiTop);
        }catch (Exception ex) {
            if (itemstack.getItemDamage() != 0) {
                ItemStack newStack = itemstack.copy();
                newStack.setItemDamage(0);
                drawItemStack(newStack, x, y);
            }
        }finally {
            itemRender.zLevel = 0F;

            bindTexture(getComponentResource());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glPopMatrix();
        }

    }

    public int getStringWidth(String str) {
        return fontRendererObj.getStringWidth(str);
    }


    public void drawCenteredString(String str, int x, int y, float mult, int width, int color) {
        drawString(str, x + (width - (int)(getStringWidth(str) * mult)) / 2, y, mult, color);
    }

    public void drawCursor(int x, int y, int z, int color) {
        drawCursor(x, y, z, 1F, color);
    }
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


    public void drawLine(int x1, int y1, int x2, int y2) {
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0.4F, 0.4F, 0.4F, 1F);

        //GL11.glEnable(GL11.GL_BLEND);
        //GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
        //GL11.glShadeModel(GL11.GL_SMOOTH);
        //GL11.glEnable(GL11.GL_LINE_SMOOTH);
        //GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        //GL11.glLineWidth(5);
        GL11.glLineWidth(1 + 5 * this.width / 500F);

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3f(guiLeft + x1, guiTop + y1, 0);
        GL11.glVertex3f(guiLeft + x2, guiTop + y2, 0);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }





    @Override
    public final void drawDefaultBackground() {
        super.drawDefaultBackground();

        startScaling();
    }



    @Override
    public void drawScreen(int x, int y, float f) {

        super.drawScreen(scaleX(x), scaleY(y), f);

        stopScaling();
    }

    private void startScaling() {
        //start scale
        GL11.glPushMatrix();

        float scale = getScale();

        GL11.glScalef(scale, scale, 1);
        GL11.glTranslatef(-guiLeft, -guiTop, 0.0F);
        GL11.glTranslatef((this.width - this.xSize * scale) / (2 * scale), (this.height - this.ySize * scale) / (2 * scale), 0.0F);
    }

    private void stopScaling() {
        //stop scale
        GL11.glPopMatrix();
    }

    protected float getScale() {

        net.minecraft.client.gui.ScaledResolution scaledresolution = new net.minecraft.client.gui.ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        float w = scaledresolution.getScaledWidth() * 0.9F;
        float h = scaledresolution.getScaledHeight() * 0.9F;
        float multX = w / xSize;
        float multY = h / ySize;
        float mult = Math.min(multX, multY);
        if (mult > 1F && !Settings.isEnlargeInterfaces()) {
            mult = 1F;
        }

        mult = (float)(Math.floor(mult * 1000)) / 1000F;

        return mult;
    }

    protected int scaleX(float x) {
        float scale = getScale();
        x /= scale;
        x += guiLeft;
        x -= (this.width - this.xSize * scale) / (2 * scale);
        return (int)x;
    }
    protected int scaleY(float y) {
        float scale = getScale();
        y /= scale;
        y += guiTop;
        y -= (this.height - this.ySize * scale) / (2 * scale);
        return (int)y;
    }

    public void drawIcon(IIcon icon, int x, int y) {
        drawTexturedModelRectFromIcon(guiLeft + x, guiTop + y, icon, 16, 16);
    }

    public void drawFluid(Fluid fluid, int x, int y) {


        IIcon icon = fluid.getIcon();

        if (icon == null) {
            if (FluidRegistry.WATER.equals(fluid)) {
                icon = Blocks.water.getIcon(0, 0);
            }else if(FluidRegistry.LAVA.equals(fluid)) {
                icon = Blocks.lava.getIcon(0, 0);
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


    public int getFontHeight() {
        return fontRendererObj.FONT_HEIGHT;
    }

    public int getLeft() {
        return guiLeft;
    }

    public int getTop() {
        return guiTop;
    }
}
