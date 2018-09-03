package com.ansoft.utilitybox;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.scottyab.rootbeer.RootBeer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HomeActivity extends AppCompatActivity{

    static String FILENAME_LOLIPOP_ABOVE = "Xposed_lolipop_Above.apk";
    static String FILENAME_LOLIPOP_BELOW = "Xposed_lolipop_Below.apk";
    static String PACKAGE_NAME = "de.robv.android.xposed.installer";

    private static final int INTERNET=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT>=23){
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{   Manifest.permission.INTERNET,
                                    Manifest.permission.ACCESS_WIFI_STATE,
                                    Manifest.permission.BLUETOOTH,
                                    Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_SETTINGS,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.ACCESS_NETWORK_STATE,
                                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    },
                    INTERNET);
        }else {
            InitInstallation();
        }
    }

    public void exit(){
        toastMessage("This permission is needed");
        finish();
    }


    public void toastMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case INTERNET: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    InitInstallation();
                } else {
                    exit();
                }
                return;
            }

        }
    }

    private void InitInstallation() {
        if (isPackageExisted(PACKAGE_NAME)) {
            finish();
            Intent intent = new Intent(HomeActivity.this, MobileIDSActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else {

            RootManager manager = RootManager.getInstance();
            if (manager.hasRooted()) {
                if (manager.obtainPermission()) {
                    runAsyncTask(new AsyncTask<Void, Void, Void>() {

                        final ProgressDialog pd = ProgressDialog.show(HomeActivity.this, "", "Setting up");

                        @Override
                        protected Void doInBackground(Void... params) {
                            CopyAssets();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);


                            runAsyncTask(new AsyncTask<Void, Void, Result>() {

                                @Override
                                protected Result doInBackground(Void... params) {
                                    String installCommand = "";

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        installCommand = "pm install " + Environment.getExternalStorageDirectory().toString() + "/" + FILENAME_LOLIPOP_ABOVE;
                                    } else {
                                        installCommand = "pm install " + Environment.getExternalStorageDirectory().toString() + "/" + FILENAME_LOLIPOP_BELOW;
                                    }
                                    return RootManager.getInstance().runCommand(installCommand);
                                }

                                @Override
                                protected void onPostExecute(Result result) {
                                    super.onPostExecute(result);


                                    runAsyncTask(new AsyncTask<Void, Void, Result>() {

                                        @Override
                                        protected Result doInBackground(Void... params) {
                                            String activityCommand = "am start " + PACKAGE_NAME + "/.WelcomeActivity";
                                            return RootManager.getInstance().runCommand(activityCommand);
                                        }

                                        @Override
                                        protected void onPostExecute(Result result) {
                                            super.onPostExecute(result);


                                            pd.dismiss();

                                            ToastMessage("Steps to follow\n\n1. Go to Module\n2. Check on IMEI Changer\n3. Go to Framework\n4. Click install/update and then reboot\n5. Now you're done, you can change the IMEI", true);

                                            AssetManager assetManager = getAssets();
                                            String[] files = null;
                                            try {
                                                files = assetManager.list("");
                                            } catch (IOException e) {
                                                Log.e("tag", e.getMessage());
                                            }

                                            String ROOT_DIR = Environment.getExternalStorageDirectory().toString();
                                            for (String filename : files) {
                                                File file = new File(ROOT_DIR + "/" + filename);
                                                file.delete();
                                            }

                                            Log.e("CLEAR", "Command has been executed " + result.getResult()
                                                    + " with message " + result.getMessage());
                                        }

                                    });
                                    Log.e("CLEAR", "Command " + " execute " + result.getResult()
                                            + " with message " + result.getMessage());
                                }

                            });

                        }
                    });
                } else {
                    ToastMessage("This app needs root access to continue", false);

                }
            } else {
                ToastMessage("Your phone is not rooted!\n Can't Continue", false);
                finish();
            }

        }
    }


    public boolean isPackageExisted(String targetPackage) {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public void ToastMessage(String message, boolean longs) {
        if (longs) {
            Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void CopyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        String ROOT_DIR = Environment.getExternalStorageDirectory().toString();
        for (String filename : files) {
            System.out.println("File name => " + filename);
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);   // if files resides inside the "Files" directory itself
                out = new FileOutputStream(ROOT_DIR + "/" + filename);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    private static final <T> void runAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        asyncTask.execute(params);
    }


}
