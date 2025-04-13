package com.medicoom.utils;

public class myUtils {
    public static boolean isSpace(String str) {
        String[] myStr = str.split("");
        boolean res = true;
        for (String a : myStr) {
            if (!(a.equals(" ") || a.equals("\n"))) {
                res = false;
                break;
            }
        }
        return res;
    }
}
