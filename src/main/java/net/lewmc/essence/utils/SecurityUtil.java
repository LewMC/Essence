package net.lewmc.essence.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Security Utility
 */
public class SecurityUtil {
    /**
     * Checks for special characters.
     * @param string String - The string to check
     * @return boolean - If it has special characters
     */
    public boolean hasSpecialCharacters(String string) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);

        return m.find();
    }
}
