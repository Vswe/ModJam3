package vswe.stevesfactory.components;


public class ConditionSettingChecker {
    private ItemSetting setting;
    private int itemCount;

    public ConditionSettingChecker(Setting setting) {
        this.setting = (ItemSetting)setting;
        itemCount = 0;
    }

    public void addCount(int n) {
        itemCount += n;
    }

    public boolean isTrue() {
        return !setting.isLimitedByAmount() || itemCount >= setting.getItem().stackSize;
    }
}
