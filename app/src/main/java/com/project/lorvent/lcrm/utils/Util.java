package com.project.lorvent.lcrm.utils;

/**
 * Created by User on 4/19/2017.
 */

public class Util {
    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
