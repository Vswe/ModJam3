package vswe.stevesfactory.components;


public class TextBoxNumber {
    private static final int TEXT_BOX_SIZE_W = 21;
    private static final int TEXT_BOX_SIZE_W_WIDE = 33;

    private int x;
    private int y;
    private int number;
    private int length;
    private boolean wide;

    public TextBoxNumber(int x, int y, int length, boolean wide) {
        this.x = x;
        this.y = y;
        number = 0;
        this.length = length;
        this.wide = wide;
    }

    public int getLength() {
        return length;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isVisible() {
        return true;
    }

    public boolean isWide() {
        return wide;
    }

    public void onNumberChanged() {};

    public int getWidth() {
        return wide ? TEXT_BOX_SIZE_W_WIDE : TEXT_BOX_SIZE_W;
    }
}
