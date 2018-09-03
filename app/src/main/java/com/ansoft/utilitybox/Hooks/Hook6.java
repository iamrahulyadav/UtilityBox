package com.ansoft.utilitybox.Hooks;


import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import de.robv.android.xposed.XSharedPreferences;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Hook6 {
    private static XSharedPreferences f5411a;

    private static String PACKAGE_NAME="com.ansoft.utilitybox";
    public static XSharedPreferences m7269a() {
        if (f5411a != null) {
            return f5411a;
        }
        f5411a = new XSharedPreferences(PACKAGE_NAME, "xpref_config");
        return f5411a;
    }


    public static String getID(String str) {
        String r1="";
        int i = 0;
        Random random = new Random();
        if (str.equals("android_id")) {
            return Long.toHexString(random.nextLong());
        }
        String str2;
        if (str.equals("imei")) {
            String[] strArr = new String[]{"35", "01", "33", "44", "45", "49", "50", "51", "52", "53", "54", "86", "91", "98", "99"};
            str2 = strArr[random.nextInt(strArr.length)];
            while (str2.length() < 14) {
                str2 = str2 + Character.forDigit(random.nextInt(10), 10);
            }
            return str2 + Hook6.m7273c(str2);
        } else if (str.equals("serial")) {
            r1 = "0123456789abcdef";
            str2 = "";
            while (str2.length() < 12) {
                str2 = str2 + r1.charAt(random.nextInt(16));
            }
            return str2.toUpperCase();
        } else if (str.equals("mac_address")) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i2 = 0; i2 < 6; i2++) {
                if (i2 != 0) {
                    stringBuilder.append(':');
                }
                i = random.nextInt(256);
                if (i2 == 0) {
                    i &= 252;
                }
                stringBuilder.append(Integer.toHexString(i | 256).substring(1));
            }
            return stringBuilder.toString().toUpperCase();
        } else if (!str.equals("wifi_name")) {
            return str.equals("googlead_id") ? UUID.randomUUID().toString().toLowerCase() : "";
        } else {
            int nextInt = random.nextInt(12) + 5;
            r1 = "\"";
            String str3 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            int length = str3.length();
            while (i <= nextInt) {
                r1 = r1 + str3.charAt(random.nextInt(length));
                i++;
            }
            return r1 + "\"";
        }
    }

    private static String m7273c(String str) {
        int i = 0;
        int i2 = 0;
        while (i < str.length()) {
            int digit = Character.digit(str.charAt((str.length() - 1) - i), 10);
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            i2 += digit;
            i++;
        }
        return String.valueOf(Character.forDigit((i2 * 9) % 10, 10));
    }
}

