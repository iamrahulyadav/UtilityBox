package com.ansoft.utilitybox;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.ansoft.utilitybox.Hooks.Hook6;
import com.chrisplus.rootmanager.RootManager;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MobileIDSActivity extends AppCompatActivity {

    EditText tv1;
    EditText tv2;
    EditText tv4;
    EditText tv5;
    String f5416a = "Origin_Id";
    String ak = "version";
    String al = "version";

    Button btnRandomize;
    Button btnChange;

    Button btnSave;
    Button btnLoad;

    SharedPreferences pref;
    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_ids);
        initViews();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        pref = getSharedPreferences("IMEI_settings", 1);
        if (pref.getString("OriginalIMEI", "").toString().equalsIgnoreCase("")) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("OriginalIMEI", telephonyManager.getDeviceId().toString());
            editor.commit();
        }
        btnRandomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv1.setText(Hook6.getID("imei"));
                tv2.setText(Hook6.getID("android_id"));
                tv4.setText(Hook6.getID("mac_address"));
                tv5.setText(Hook6.getID("mac_address"));
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("CurrentIMEI", tv1.getText().toString());
                editor.putString("CurrentID", tv2.getText().toString());
                editor.putString("CurrentBMAC", tv4.getText().toString());
                editor.putString("CurrentWMAC", tv5.getText().toString());
                editor.commit();
                Toast.makeText(MobileIDSActivity.this, "IDs has been changed successfully!", Toast.LENGTH_SHORT).show();


            }
        });


        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadIDFromFile();
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveIDSToFile();
            }
        });
        putValues();
        new RetrieveIDs().execute(new Void[0]);
    }


    private static final <T> void runAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        asyncTask.execute(params);
    }


    private void initViews() {
        btnSave = (Button) findViewById(R.id.btnSave);
        btnLoad = (Button) findViewById(R.id.btnLoad);
        tv1 = (EditText) findViewById(R.id.editTextIMEI);
        tv2 = (EditText) findViewById(R.id.editTextADID);
        tv4 = (EditText) findViewById(R.id.editTextBMACID);
        tv5 = (EditText) findViewById(R.id.editTextWIFIMACID);
        btnChange = (Button) findViewById(R.id.btnChange);
        btnRandomize = (Button) findViewById(R.id.btnRandom);
    }

    public void toastMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    public void putValues() {
        tv1.setText(Hook6.getID("imei"));
        tv2.setText(Hook6.getID("android_id"));
        WifiInfo connectionInfo = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        tv5.setText(connectionInfo.getMacAddress());
    }

    class RetrieveIDs extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            String address = "";
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String string = Settings.Secure.getString(getContentResolver(), "android_id");
            String deviceId = telephonyManager.getDeviceId();
            String string2 = "";
            String string3 = "";
            try {
                if (Build.VERSION.SDK_INT >= 18) {
                    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                    address = (bluetoothManager == null || bluetoothManager.getAdapter() == null) ? string2 : bluetoothManager.getAdapter().getAddress();
                    string2 = address;
                } else {
                    string2 = BluetoothAdapter.getDefaultAdapter() != null ? BluetoothAdapter.getDefaultAdapter().getAddress() : BluetoothAdapter.getDefaultAdapter().getAddress();
                }
            } catch (NullPointerException e5) {
                e5.printStackTrace();
                address = string3;
            }
            SharedPreferences sharedPreferences = getSharedPreferences("xpref_config", 1);
            SharedPreferences.Editor edit;
            if (sharedPreferences.contains("fist_time")) {
                try {
                    int i = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                    if (getSharedPreferences(ak, 0).getInt(al, 0) < i) {
                        edit = sharedPreferences.edit();
                        edit.putString("googlead_id", address);
                        edit.apply();
                        edit = getSharedPreferences(f5416a, 0).edit();
                        edit.putString("googlead_id", address).apply();
                        edit.apply();
                        getSharedPreferences(ak, 0).edit().putInt(al, i).apply();
                    }
                } catch (PackageManager.NameNotFoundException e6) {
                    e6.printStackTrace();
                }
            } else {
                edit = sharedPreferences.edit();
                edit.putString("fist_time", "false");
                edit.putString("android_id", string);
                edit.putString("imei", deviceId);
                edit.putString("serial", Build.SERIAL);
                edit.putString("googlead_id", address);
                edit.apply();
                edit = getSharedPreferences(f5416a, 0).edit();
                edit.putString("android_id", string);
                edit.putString("imei", deviceId);
                edit.putString("serial", Build.SERIAL);
                edit.putString("googlead_id", address);
                edit.apply();
                try {
                    getSharedPreferences(ak, 0).edit().putInt(al, getPackageManager().getPackageInfo(getPackageName(), 0).versionCode).apply();
                } catch (PackageManager.NameNotFoundException e62) {
                    e62.printStackTrace();
                }
            }
            return new String[]{address, string, deviceId, string2};
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            tv1.setText(strings[2]);
            tv2.setText(strings[1]);
            tv4.setText(strings[3]);

            if (strings[1].isEmpty()||strings[2].isEmpty()||strings[3].isEmpty()){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("CurrentIMEI", Hook6.getID("imei"));
                                editor.putString("CurrentID", Hook6.getID("android_id"));
                                editor.putString("CurrentBMAC", Hook6.getID("mac_address"));
                                editor.putString("CurrentWMAC", Hook6.getID("mac_address"));
                                editor.commit();
                                String activityCommand = "reboot recovery";
                                RootManager.getInstance().runCommand(activityCommand);
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MobileIDSActivity.this);
                builder.setMessage("Attention!!\nSeems like you are running the application for the first time. You need to reboot the first time" +
                        " to make any changes through the app. \nThe app will boot you to the recovery mode. Then you can restart the device on your " +
                        "own. \nDo you want to continue?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", null).show();
            }
        }
    }


    public void SaveIDSToFile() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_AND_DIR_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{".json"};
        FilePickerDialog dialog = new FilePickerDialog(MobileIDSActivity.this, properties);
        dialog.setTitle("Select the Directory");
        dialog.show();
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("wmac", tv5.getText().toString());
                    object.put("bmac", tv4.getText().toString());
                    object.put("androidid", tv2.getText().toString());
                    object.put("imei", tv1.getText().toString());
                    final File path = new File(files[0]);
                    if (!path.exists()) {
                        path.mkdirs();
                    }

                    final File file = new File(path, "configUB.json");


                    Log.e("JSON", object.toString());
                    Log.e("SAVING DIRECTORY", file.getAbsolutePath());
                    try {
                        file.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(file);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(object.toString());

                        myOutWriter.close();

                        fOut.flush();
                        fOut.close();
                    } catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }

                    toastMessage("The IDS has been saved to a file");
                } catch (JSONException e) {
                    Log.e("JSON", e.getMessage());
                }
            }


        });
    }

    public void loadIDFromFile() {

        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.STORAGE_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{".json"};
        FilePickerDialog dialog = new FilePickerDialog(MobileIDSActivity.this, properties);
        dialog.setTitle("Load File");
        dialog.show();
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {

                String ret = "";

                try {
                    File file = new File(files[0]);
                    InputStream inputStream = new FileInputStream(file);

                    if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();

                        while ((receiveString = bufferedReader.readLine()) != null) {
                            stringBuilder.append(receiveString);
                        }

                        inputStream.close();
                        ret = stringBuilder.toString();
                    }
                } catch (FileNotFoundException e) {
                    Log.e("login activity", "File not found: " + e.toString());
                } catch (IOException e) {
                    Log.e("login activity", "Can not read file: " + e.toString());
                }


                try {
                    JSONObject obj = new JSONObject(ret);
                    tv1.setText(obj.getString("imei"));
                    tv2.setText(obj.getString("androidid"));
                    tv4.setText(obj.getString("bmac"));
                    tv5.setText(obj.getString("wmac"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                toastMessage("The IDS has been loaded from a file");
            }
        });
    }


}



