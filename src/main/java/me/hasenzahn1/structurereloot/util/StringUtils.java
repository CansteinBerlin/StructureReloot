package me.hasenzahn1.structurereloot.util;

import java.util.List;

public class StringUtils {

    public static String listToCommandArgs(List<String> values) {
        if (values.size() == 0) return "<>";
        StringBuilder sb = new StringBuilder("<" + values.get(0));
        for (int i = 1; i < values.size(); i++) {
            sb.append("/").append(values.get(i));
        }
        sb.append(">");
        return sb.toString();
    }

}
