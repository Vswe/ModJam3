package vswe.stevesfactory.components;


import vswe.stevesfactory.Localization;

public abstract class CheckBox {
    private int x, y;
    private Localization name;
    private int textWidth;

    public CheckBox(Localization name, int x, int y) {
        this.x = x;
        this.y = y;
        this.name = name;
        textWidth = Integer.MAX_VALUE;
    }

    public abstract void setValue(boolean val);
    public abstract boolean getValue();
    public abstract void onUpdate();


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getName() {
        return name == null ? null : name.toString();
    }

    public boolean isVisible() {
        return true;
    }

    public int getTextWidth() {
        return textWidth;
    }

    public void setTextWidth(int textWidth) {
        this.textWidth = textWidth;
    }
}
