package vswe.stevesfactory.components;


import java.util.ArrayList;
import java.util.List;

public class SlotSideTarget {

    private int slot;
    private List<Integer> sides;

    public SlotSideTarget(int slot, int side) {
        this.slot = slot;
        sides = new ArrayList<Integer>();
        sides.add(side);
    }

    public void addSide(int side) {
        sides.add(side);
    }

    public int getSlot() {
        return slot;
    }

    public List<Integer> getSides() {
        return sides;
    }
}
