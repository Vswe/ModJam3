package vswe.stevesfactory.components;



public class CraftingSetting extends ItemSetting {
    public CraftingSetting(int id) {
        super(id);
    }

    @Override
    public boolean isAmountSpecific() {
        return false;
    }
}
