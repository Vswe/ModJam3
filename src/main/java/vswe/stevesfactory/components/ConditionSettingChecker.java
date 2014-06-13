package vswe.stevesfactory.components;


public class ConditionSettingChecker {
    private Setting setting;
    private int amount;

    public ConditionSettingChecker(Setting setting) {
        this.setting = setting;
        amount = 0;
    }

    public void addCount(int n) {
        amount += n;
    }

    public boolean isTrue() {
        return !setting.isLimitedByAmount() || amount >= setting.getAmount();
    }
}
