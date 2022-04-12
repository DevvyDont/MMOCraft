package me.devvy.mmocraft.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.util.StringUtil;

public class StringFormatting {

    // Give this an Enum that has underscores and all caps, return string with capped first letters, and spaces
    public static String cleanEnumName(String in) {
        String spaces = in.replace('_', ' ');
        String title = WordUtils.capitalizeFully(spaces);
        return title;
    }

}
