package vswe.stevesfactory.util;

import java.util.regex.Pattern;

/**
 * Created by gustaf on 05/09/14.
 */
public class Utils {

    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    public static String stripControlCodes(String s)
    {
        return patternControlCode.matcher(s).replaceAll("");
    }

}
