package vswe.stevesfactory.components;


public enum FuzzyMode {
    PRECISE("Precise detection"),
    NBT_FUZZY("NBT independent detection"),
    FUZZY("Fuzzy detection"),
    ORE_DICTIONARY("Ore dictionary support");

    private String text;

    private FuzzyMode(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }


}
