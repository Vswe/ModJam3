package vswe.stevesfactory.components;


public abstract class CheckBox {
    private int x, y;
    private String name;

    public CheckBox( String name, int x, int y) {
        this.x = x;
        this.y = y;
        this.name = name;
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
        return name;
    }
}
