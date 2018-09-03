package com.ansoft.utilitybox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ApplistActivity extends AppCompatActivity {

    static ListView listView;
    ArrayList<Appdata> appLists;
    AppListAdapter appListAdapter;
    private Method mGetPackageSizeInfoMethod;
    EditText txtSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mGetPackageSizeInfoMethod = getPackageManager().getClass().getMethod(
                    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_applist);
        listView=(ListView)findViewById(R.id.listView);
        txtSearch=(EditText)findViewById(R.id.inputSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {


                if (cs.toString().isEmpty()){
                    Log.e("NJN", "ss");
                    appListAdapter.getFilter().filter("SHOWALLLLLL");
                }else {
                    appListAdapter.getFilter().filter(cs.toString() + "");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence cs, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub


            }

            @Override
            public void afterTextChanged(Editable ed) {
            }
        });
        new TaskSync().execute(new Void[0]);
    }

    public boolean cacheExists(ArrayList<File> files, String pn){
        boolean exists=false;
        for (File file:files){
            if (file.toString().contains(pn)){
                exists=true;
            }
        }
        return exists;

    }

    public File getCacheDir(ArrayList<File> files, String pn){
        File cacheD=null;
        for (File file:files){
            if (file.toString().contains(pn)){
                cacheD=file;
            }
        }
        return cacheD;

    }

    class TaskSync extends AsyncTask<Void, Void, Void>{

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd=ProgressDialog.show(ApplistActivity.this, "", "Please wait");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getAllApps();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            new Global().setList(appLists);
            appListAdapter=new AppListAdapter(appLists, ApplistActivity.this);
            listView.setAdapter(appListAdapter);
            super.onPostExecute(aVoid);
        }
    }
    public void getAllApps(){

        appLists=new ArrayList<>();
        final PackageManager pm = getPackageManager();


        ArrayList<File> cacheDir=new ArrayList<>();
        final File externalDataDirectory = new File(Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/Android/data");

        final String externalCachePath = externalDataDirectory.getAbsolutePath() +
                "/%s/cache";

        if (externalDataDirectory.isDirectory()) {
            final File[] files = externalDataDirectory.listFiles();

            for (File file : files) {

                File cache=new File(String.format(externalCachePath,file.getName()));
                cacheDir.add(cache);
            }
        }

//get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            final Appdata app=new Appdata();
            app.setPckgInfo(packageInfo);
            app.setAppName(packageInfo.loadLabel(getPackageManager()).toString());
            app.setIcon(packageInfo.loadIcon(getPackageManager()));

            if (cacheExists(cacheDir, packageInfo.packageName)){
                app.setCacheDir(getCacheDir(cacheDir, packageInfo.packageName));

                Log.e("EXISTS CACHE", getCacheDir(cacheDir, packageInfo.packageName).toString());
            }


            try {
                mGetPackageSizeInfoMethod.invoke(getPackageManager(), packageInfo.packageName,
                        new IPackageStatsObserver.Stub() {

                            @Override
                            public void onGetStatsCompleted(PackageStats pStats,
                                                            boolean succeeded)
                                    throws RemoteException {

                                long mCacheSize=getSize(pStats, succeeded);
                                app.setCacheSize(mCacheSize);
                            }
                        }
                );
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            appLists.add(app);
        }


        Collections.sort(appLists, new CustomComparator());


    }


    public class CustomComparator implements Comparator<Appdata> {



        @Override
        public int compare(Appdata o1, Appdata o2) {
            return (int)(o2.getCacheSize()-o1.getCacheSize());
        }
    }


    private long getSize(PackageStats pStats, boolean succeeded) {
        long cacheSize = 0;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            cacheSize += pStats.cacheSize;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            cacheSize += pStats.externalCacheSize;
        }

        if (!succeeded || cacheSize <= 0) {
            return 0;
        }

        return cacheSize;
    }





}
