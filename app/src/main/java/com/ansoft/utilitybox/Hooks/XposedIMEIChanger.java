package com.ansoft.utilitybox.Hooks;

import android.content.ContentResolver;
import android.os.Build.VERSION;
import android.provider.Settings;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedIMEIChanger implements IXposedHookLoadPackage {


    public static String PREF_OFF="PreferenceOFf";
    public static String IS_OFF="PreferenceON";
    static String PAKCAGE_NAME = "com.ansoft.utilitybox";

    class Hook extends XC_MethodHook {
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            XSharedPreferences pref = new XSharedPreferences(PAKCAGE_NAME, "IMEI_settings");
            param.setResult(pref.getString("CurrentIMEI", pref.getString("OriginalIMEI", "")));
        }
    }

    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        XSharedPreferences preference = new XSharedPreferences(PAKCAGE_NAME, PREF_OFF);
        if (!preference.getBoolean(IS_OFF, false)) {

            try {
                final XSharedPreferences pref = new XSharedPreferences(PAKCAGE_NAME, "IMEI_settings");
                XposedHelpers
                        .findAndHookMethod("android.net.wifi.WifiInfo",
                                lpparam.classLoader, "getMacAddress",
                                XC_MethodReplacement.returnConstant(pref
                                        .getString("CurrentWMAC", "")));
                XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo",
                        lpparam.classLoader, "getSSID", XC_MethodReplacement
                                .returnConstant(pref.getString("CurrentSSID", "")));
			/*
			XposedHelpers.findAndHookMethod(
					"android.bluetooth.BluetoothAdapter", loadPP.classLoader,
					"getAddress", XC_MethodReplacement.returnConstant(xPref
							.getString("bluetooth_mac", "")));
			XposedHelpers.findAndHookMethod(
					"android.bluetooth.BluetoothDevice", loadPP.classLoader,
					"getAddress", XC_MethodReplacement.returnConstant(xPref
							.getString("bluetooth_mac", "")));
			*/

            } catch (Exception e) {
                XposedBridge.log("Couldn't change Wifi");
            }


            try {
                final XSharedPreferences pref = new XSharedPreferences(PAKCAGE_NAME, "IMEI_settings");
                XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "getAddress", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        param.setResult(pref.getString("CurrentBMAC", ""));


                    }

                });
                XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothDevice", lpparam.classLoader, "getAddress", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        param.setResult(pref.getString("CurrentBMAC", ""));
                    }
                });
            } catch (Exception e) {
                XposedBridge.log("Couldn't change Bluetooh");
            }
            // /////


        /*

        // Change Serial Number
        try {
            final XSharedPreferences pref = new XSharedPreferences(PAKCAGE_NAME, "IMEI_settings");
            Class<?> classBuild = XposedHelpers.findClass("android.os.Build",
                    lpparam.classLoader);
            XposedHelpers.setStaticObjectField(classBuild, "SERIAL",
                    pref.getString("CurrentSERIAL", ""));
            Class<?> classSysProp = Class
                    .forName("android.os.SystemProperties");
            XposedHelpers.findAndHookMethod(classSysProp, "get", String.class,
                    new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {
                            // TODO Auto-generated method stub
                            super.afterHookedMethod(param);

                            String serialno = (String) param.args[1];
                            if (serialno.equalsIgnoreCase(Build.SERIAL)) {
                                XSharedPreferences pref2 = new XSharedPreferences(PAKCAGE_NAME, "IMEI_settings");
                                param.setResult(pref2.getString("CurrentSERIAL", ""));
                            }
                        }

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            String serialno = (String) param.args[0];
                            if (serialno.equals("ro.serialno")
                                    || serialno.equals("ro.boot.serialno")
                                    || serialno.equals("ril.serialnumber")
                                    || serialno.equals("sys.serialnumber")) {
                                XSharedPreferences pref3 = new XSharedPreferences(PAKCAGE_NAME, "IMEI_settings");
                                param.args[1] = pref3.getString("CurrentSERIAL", "");

                        }
                        }
                    });
            return;

        } catch (IllegalArgumentException ill) {
            ill.printStackTrace();
            return;
        } catch (ClassNotFoundException classNF) {
            classNF.printStackTrace();
        }
        //////

*/

            try {
                XposedHelpers.findAndHookMethod("android.provider.Settings.Secure",
                        lpparam.classLoader, "getString", ContentResolver.class,
                        String.class, new XC_MethodHook() {

                            @Override
                            protected void afterHookedMethod(MethodHookParam param)
                                    throws Throwable {
                                // TODO Auto-generated method stub
                                // super.afterHookedMethod(param);
                                if (param.args[1]
                                        .equals(Settings.Secure.ANDROID_ID)) {


                                    XSharedPreferences pref = new XSharedPreferences(PAKCAGE_NAME, "IMEI_settings");
                                    param.setResult(pref.getString("CurrentID",
                                            ""));
                                }
                            /*
                             * if(param.args[1].equals("mock_location")) {
							 * param.setResult(0); }
							 */
                            }


                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);

                                if (String.valueOf(param.args[0]).equalsIgnoreCase(Settings.Secure.ANDROID_ID)) {
                                    XSharedPreferences pref = new XSharedPreferences(PAKCAGE_NAME, "IMEI_settings");
                                    param.args[1] = pref.getString("CurrentID", "");
                                }
                            }
                        });
            } catch (Exception e) {
                XposedBridge.log("Couldn't change android_id");
            }

            if (VERSION.SDK_INT < 22) {
                XposedHelpers.findAndHookMethod("com.android.internal.telephony.PhoneSubInfo", lpparam.classLoader, "getDeviceId", new Object[]{new Hook()});
                XposedHelpers.findAndHookMethod("com.android.internal.telephony.gsm.GSMPhone", lpparam.classLoader, "getDeviceId", new Object[]{new Hook()});
                XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getDeviceId", new Object[]{new Hook()});
            }
            if (VERSION.SDK_INT == 22) {
                try {
                    XposedHelpers.findAndHookMethod("com.android.internal.telephony.PhoneSubInfo", lpparam.classLoader, "getDeviceId", new Object[]{new Hook()});
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    XposedHelpers.findAndHookMethod("com.android.internal.telephony.PhoneProxy", lpparam.classLoader, "getDeviceId", new Object[]{new Hook()});
                } catch (Exception ex2) {
                    ex2.printStackTrace();
                }
                try {
                    XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getDeviceId", new Object[]{new Hook()});
                } catch (Exception ex22) {
                    ex22.printStackTrace();
                }
            }
            if (VERSION.SDK_INT >= 23) {
                try {
                    XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getDeviceId", new Object[]{new Hook()});
                } catch (Exception ex222) {
                    ex222.printStackTrace();
                }
                try {
                    XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getImei", new Object[]{new Hook()});
                } catch (Exception ex2222) {
                    ex2222.printStackTrace();
                }
                try {
                    XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getDeviceId", new Object[]{Integer.TYPE, new Hook()});
                } catch (Exception ex22222) {
                    ex22222.printStackTrace();
                }
                try {
                    XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getImei", new Object[]{Integer.TYPE, new XC_MethodHook() {
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XSharedPreferences pref = new XSharedPreferences(PAKCAGE_NAME, "IMEI_settings");
                            param.setResult(pref.getString("CurrentIMEI", ""));
                        }
                    }});
                } catch (Exception ex222222) {
                    ex222222.printStackTrace();
                }
                try {
                    if (lpparam.packageName.equalsIgnoreCase("com.android.settings")) {
                        XposedHelpers.findAndHookMethod("com.android.settings.deviceinfo.ImeiInformation", lpparam.classLoader, "setSummaryText", new Object[]{String.class, String.class, new XC_MethodHook() {
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                if (String.valueOf(param.args[0]).equalsIgnoreCase("imei")) {
                                    XSharedPreferences pref = new XSharedPreferences(PAKCAGE_NAME, "IMEI_settings");
                                    param.args[1] = pref.getString("CurrentIMEI", "");
                                }
                            }
                        }});
                    }
                } catch (Exception ex2222222) {
                    ex2222222.printStackTrace();
                }
            }
        }
    }
}
