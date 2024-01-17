package net.lewmc.essence.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityUtil {
    public boolean hasSpecialCharacters(String string) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);

        return m.find();
    }
}
