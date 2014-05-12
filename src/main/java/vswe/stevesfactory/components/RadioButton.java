package vswe.stevesfactory.components;


import vswe.stevesfactory.Localization;

public class RadioButton {
    private int x;
    private int y;
    private Localization text;

    public RadioButton(int x, int y, Localization text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getText() {
        return text.toString();
    }

    public boolean isVisible() {
        return true;
    }
}
