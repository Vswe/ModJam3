package vswe.stevesfactory.components;


import vswe.stevesfactory.Localization;

public enum FuzzyMode {
    PRECISE(Localization.DETECTION_PRECISE),
    NBT_FUZZY(Localization.DETECTION_NBT_FUZZY),
    FUZZY(Localization.DETECTION_FUZZY),
    ORE_DICTIONARY(Localization.DETECTION_ORE_DICTIONARY);

    private Localization text;

    private FuzzyMode(Localization text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text.toString();
    }


}
