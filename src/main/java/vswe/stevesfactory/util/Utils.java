package vswe.stevesfactory.util;

import java.util.regex.Pattern;

public class Utils {

    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    public static String stripControlCodes(String s)
    {
        return patternControlCode.matcher(s).replaceAll("");
    }

}
