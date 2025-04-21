package com.medicoom.utils;

import android.app.Activity;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.medicoom.R;

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
