package com.medicoom.utils;


import java.text.SimpleDateFormat;
import java.util.Locale;

public class myUtils {
    public static final  SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy", Locale.getDefault());
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm", Locale.getDefault());


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
