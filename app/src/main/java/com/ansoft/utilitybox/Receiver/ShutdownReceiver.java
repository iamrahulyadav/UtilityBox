package com.ansoft.utilitybox.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Abinash on 11/22/2016.
 */
public class ShutdownReceiver extends BroadcastReceiver {


    public static String PREF_OFF="PreferenceOFf";
    public static String IS_OFF="PreferenceON";
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences=context.getSharedPreferences(PREF_OFF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(IS_OFF, true);
        editor.commit();
    }

}
