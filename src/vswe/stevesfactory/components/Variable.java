package vswe.stevesfactory.components;



import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.interfaces.GuiManager;

import java.util.ArrayList;
import java.util.List;

public class Variable implements IContainerSelection {
    private int id;
    private boolean declared;
    private List<Integer> containers;
    private boolean hasGlobalBeenSetUp;

    public Variable(int id) {
        this.id = id;
        containers = new ArrayList<Integer>();
    }

    public boolean isValid() {
        return declared;
    }

    @Override
    public int getId() {
        return id;
    }

    private static final int VARIABLE_SRC_X = 32;
    private static final int VARIABLE_SRC_Y = 130;
    private static final int VARIABLE_SIZE = 14;

    @Override
    public void draw(GuiManager gui, int x, int y) {
        VariableColor.values()[id].applyColor();
        gui.drawTexture(x + 1, y + 1, VARIABLE_SRC_X, VARIABLE_SRC_Y, VARIABLE_SIZE, VARIABLE_SIZE);
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    @Override
    public String getDescription(GuiManager gui) {
        VariableColor color = VariableColor.values()[id];
        return color.getTextColor().toString() + color.toString() + " Variable";
    }

    public void setDeclaration(boolean val) {
        declared = val;
    }

    public List<Integer> getContainers() {
        return containers;
    }

    public void add(int id) {
        if (!containers.contains((Integer)id)) {
            containers.add(id);
        }
    }
}
