package vswe.stevesfactory.interfaces;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import org.lwjgl.opengl.GL11;


@SideOnly(Side.CLIENT)
public class GuiLarge extends net.minecraft.client.gui.inventory.GuiContainer  {

    private float cachedScale;

    public GuiLarge(Container container) {
        super(container);
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        generateScale();
        super.drawScreen(x, y, f);
    }

    protected float scaleCoordinateX(int x) {
        return scaleCoordinateX(x, getScale());
    }

    protected float scaleCoordinateY(int y) {
        return scaleCoordinateY(y, getScale());
    }

    private float scaleCoordinateX(int x, float scale) {
        return x * scale + (this.width - this.xSize * scale) / 2;
    }

    private float scaleCoordinateY(int y, float scale) {
        return y * scale + (this.height - this.ySize * scale) / 2;
    }


    protected float getScale() {
        return cachedScale;
    }

    protected void generateScale() {
        ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        float w = scaledresolution.getScaledWidth() * 0.9F;
        float h = scaledresolution.getScaledHeight() * 0.9F;
        float multX = w / xSize;
        float multY = h / ySize;
        cachedScale = Math.min(multX, multY);
        if (cachedScale > 1F) {
            cachedScale = 1F;
        }
    }


    public void drawScaledString(String str, int x, int y, float size, int color) {
        float total = size * getScale();
        GL11.glPushMatrix();
        GL11.glScalef(total, total, 1F);
        fontRenderer.drawString(str, (int)((scaleCoordinateX(x)) / total), (int)((scaleCoordinateY(y)) / total), color);
        GL11.glPopMatrix();
    }

    public void drawScaledSplitString(String str, int x, int y, int w, float size, int color) {
        int left = (int)scaleCoordinateX(x);
        int right = (int)scaleCoordinateX(x + w);

        float total = size * getScale();
        GL11.glPushMatrix();
        GL11.glScalef(total, total, 1F);
        fontRenderer.drawSplitString(str, (int)(left / total), (int)((scaleCoordinateY(y)) / total), (int)((right - left) / total), color);
        GL11.glPopMatrix();
    }

   @Override
    public void drawTexturedModalRect(int x, int y, int srcX, int srcY, int w, int h) {
        float targetLeft = scaleCoordinateX(x);
        float targetRight = scaleCoordinateX(x + w);
        float targetTop = scaleCoordinateY(y);
        float targetBot = scaleCoordinateY(y + h);

        float f = 0.00390625F;
        float f1 = 0.00390625F;

        float srcLeft = srcX * f;
        float srcRight = (srcX + w) * f;
        float srcTop = srcY * f1;
        float srcBot = (srcY + h) * f1;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(targetLeft,     targetBot,  this.zLevel,    srcLeft,    srcBot);
        tessellator.addVertexWithUV(targetRight,    targetBot,  this.zLevel,    srcRight,   srcBot);
        tessellator.addVertexWithUV(targetRight,    targetTop,  this.zLevel,    srcRight,   srcTop);
        tessellator.addVertexWithUV(targetLeft,     targetTop,  this.zLevel,    srcLeft,    srcTop);
        tessellator.draw();
    }

    protected int scaleMouseX(float x) {
        float scale = getScale();
        x /= scale;
        x += guiLeft;
        x -= (this.width - this.xSize * scale) / (2 * scale);
        return (int)x;
    }

    protected int scaleMouseY(float y) {
        float scale = getScale();
        y /= scale;
        y += guiTop;
        y -= (this.height - this.ySize * scale) / (2 * scale);
        return (int)y;
    }

    @Override
    protected final void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        x = scaleMouseX(x);
        y = scaleMouseY(y);

        x -= guiLeft;
        y -= guiTop;

        drawBackground(f, x, y);
    }

    @Override
    protected final void mouseClicked(int x, int y, int button) {
        x = scaleMouseX(x);
        y = scaleMouseY(y);

        super.mouseClicked(x, y, button);

        x -= guiLeft;
        y -= guiTop;

        onClick(x, y, button);
    }

    @Override
    protected final void mouseClickMove(int x, int y, int button, long ticks) {
        x = scaleMouseX(x);
        y = scaleMouseY(y);

        super.mouseClickMove(x, y, button, ticks);

        x -= guiLeft;
        y -= guiTop;

        onDrag(x, y, button, ticks);
    }

    @Override
    protected final void mouseMovedOrUp(int x, int y, int button) {
        x = scaleMouseX(x);
        y = scaleMouseY(y);

        super.mouseMovedOrUp(x, y, button);

        x -= guiLeft;
        y -= guiTop;

        onRelease(x, y, button);
    }


    protected void drawBackground(float f, int x, int y) {}
    protected void onClick(int x, int y, int button) {}
    protected void onDrag(int x, int y, int button, long ticks) {}
    protected void onRelease(int x, int y, int button) {}
}
