package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.interfaces.Color;

public enum VariableColor {
    WHITE(Color.WHITE, 1.0F, 1.0F, 1.0F),
    ORANGE(Color.ORANGE, 0.85F, 0.5F, 0.2F),
    MAGENTA(Color.MAGENTA, 0.7F, 0.3F, 0.85F),
    LIGHT_BLUE(Color.LIGHT_BLUE, 0.4F, 0.6F, 0.85F),
    YELLOW(Color.YELLOW, 0.9F, 0.9F, 0.2F),
    LIME(Color.LIME, 0.5F, 0.8F, 0.1F),
    PINK(Color.PINK, 0.95F, 0.5F, 0.65F),
    GRAY(Color.GRAY, 0.3F, 0.3F, 0.3F),
    LIGHT_GRAY(Color.LIGHT_GRAY, 0.6F, 0.6F, 0.6F),
    CYAN(Color.CYAN, 0.3F, 0.5F, 0.6F),
    PURPLE(Color.PURPLE, 0.5F, 0.25F, 0.7F),
    BLUE(Color.BLUE, 0.2F, 0.3F, 0.7F),
    BROWN(Color.WHITE, 0.4F, 0.3F, 0.2F),
    GREEN(Color.GREEN, 0.4F, 0.5F, 0.2F),
    RED(Color.RED, 0.6F, 0.2F, 0.2F),
    BLACK(Color.BLACK, 0.1F, 0.1F, 0.1F);

    private Color textColor;
    private float red;
    private float green;
    private float blue;

    private VariableColor(Color textColor, float red, float green, float blue) {
        this.textColor = textColor;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @SideOnly(Side.CLIENT)
    public void applyColor() {
        GL11.glColor4f(red, green, blue, 1F);
    }


    @Override
    public String toString() {
        return super.toString().charAt(0) + super.toString().substring(1).toLowerCase().replace("_", " ");
    }

    public Color getTextColor() {
        return textColor;
    }
}
