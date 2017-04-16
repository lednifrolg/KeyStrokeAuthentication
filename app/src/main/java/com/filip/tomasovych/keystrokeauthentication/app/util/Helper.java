package com.filip.tomasovych.keystrokeauthentication.app.util;

/**
 * Created by nolofinwe on 9.10.2016.
 */

public final class Helper {

    public final static String USER_NAME = "ActiveUserName";
    public final static String USER_ID = "ActiveUserID";
    public final static String USER_PASSWORD = "ActiveUserPassword";
    public final static String NUM_PASSWORD = "NumberPassword";
    public static final String IS_IDENTIFY = "IsIdentify";
    public static String STATIC_NUM_PASSWORD = "67804910";
    public static String STATIC_PASSWORD = ".tie5Roanl";
    public static String IS_EXPERIMENT = "IsExperiment";
    public final static int NUM_PASSWORD_CODE = 0x0000;
    public final static int ALNUM_PASSWORD_CODE = 0x0001;

    public static boolean isNumeric(String s) {
        return s.matches("\\d*");
    }
}
