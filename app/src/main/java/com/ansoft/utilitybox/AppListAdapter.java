package com.ansoft.utilitybox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abinash on 11/6/2016.
 */
public class AppListAdapter extends BaseAdapter {
    ArrayList<Appdata> appList;
    Activity activity;

    public AppListAdapter(ArrayList<Appdata> appList, Activity activity) {
        this.appList = appList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View cv, ViewGroup parent) {
        cv = activity.getLayoutInflater().inflate(R.layout.item_app_list, parent, false);
        ImageView imgIcon = (ImageView) cv.findViewById(R.id.imgAppIcon);
        TextView appName = (TextView) cv.findViewById(R.id.tvAppName);
        TextView cacheSize = (TextView) cv.findViewById(R.id.tvCacheSize);
        final Appdata data = appList.get(position);
        imgIcon.setImageDrawable(data.getIcon());
        appName.setText(data.getAppName());


        String size = "";

        if (data.getCacheSize() < 1024) {
            size = data.getCacheSize() + "bytes";
        } else if (data.getCacheSize() < (1024 * 1024) && data.getCacheSize() > 1024) {
            size = (int) data.getCacheSize() / 1024 + "Kb";
        } else {
            size = (int) data.getCacheSize() / (1024 * 1024) + "Mb";
        }

        cacheSize.setText(size);

        LinearLayout cleanBtn = (LinearLayout) cv.findViewById(R.id.btnClean);
        cleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final ProgressDialog pd = ProgressDialog.show(activity, "Please wait", "Reverting back to its original state");
                RootManager.getInstance().killProcessByName(data.getPckgInfo().packageName);


                final String command = "pm clear " + data.getPckgInfo().packageName;
                runAsyncTask(new AsyncTask<Void, Void, Result>() {

                    @Override
                    protected Result doInBackground(Void... params) {
                        return RootManager.getInstance().runCommand(command);
                    }

                    @Override
                    protected void onPostExecute(Result result) {
                        super.onPostExecute(result);

                        final String command = "pm path " + data.getPckgInfo().packageName;
                        runAsyncTask(new AsyncTask<Void, Void, Result>() {

                            @Override
                            protected Result doInBackground(Void... params) {
                                return RootManager.getInstance().runCommand(command);
                            }

                            @Override
                            protected void onPostExecute(Result result) {
                                super.onPostExecute(result);

                                final String path = result.getMessage().replace("package:", "");
                                Log.e("PATH", path);


                                extractapk(data.pckgInfo);


                                final String command = "pm uninstall " + data.getPckgInfo().packageName;
                                runAsyncTask(new AsyncTask<Void, Void, Result>() {

                                    @Override
                                    protected Result doInBackground(Void... params) {
                                        return RootManager.getInstance().runCommand(command);
                                    }

                                    @Override
                                    protected void onPostExecute(Result result) {
                                        super.onPostExecute(result);


                                        String filename = data.pckgInfo.packageName.toString();
                                        final String apkPath = Environment.getExternalStorageDirectory().toString() + "/UtilityBox/Apps/Data/Apks" + "/" + filename + ".apk";
                                        final String command = "pm install " + apkPath;
                                        runAsyncTask(new AsyncTask<Void, Void, Result>() {

                                            @Override
                                            protected Result doInBackground(Void... params) {
                                                return RootManager.getInstance().runCommand(command);
                                            }

                                            @Override
                                            protected void onPostExecute(Result result) {
                                                super.onPostExecute(result);

                                                pd.dismiss();
                                                Toast.makeText(activity, "Data for " + data.getAppName() + " has been cleaned", Toast.LENGTH_SHORT).show();
                                                activity.finish();
                                                activity.overridePendingTransition(0, 0);
                                                Intent in = new Intent(activity, ApplistActivity.class);
                                                in.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                activity.startActivity(in);
                                                Log.e("CLEAR", "Command " + command + " execute " + result.getResult()
                                                        + " with message " + result.getMessage());
                                            }

                                        });
                                    }

                                });
                            }

                        });
                    }

                });


            }
        });
        return cv;
    }


    public String extractapk(ApplicationInfo i) {
        File f = new File(i.publicSourceDir);

        String filename = i.packageName.toString();
        Log.d("file_name--", "" + filename);
        File f2 = null;
        String path1 = Environment.getExternalStorageDirectory().toString()
                + "/AppAndApks";
        try {
            String info = Environment.getExternalStorageState();
            if (info.equals(Environment.MEDIA_MOUNTED)) {
                f2 = new File(Environment.getExternalStorageDirectory()
                        .toString() + "/UtilityBox/Apps/Data/Apks");

            }
            if (!f2.exists())
                f2.mkdirs();
            f2 = new File(f2.getPath() + "/" + filename + ".apk");
            if (!f2.exists()) {
                f2.createNewFile();
                InputStream in = new FileInputStream(f);
                OutputStream out = new FileOutputStream(f2);
                byte[] bf = new byte[1024];
                int len;
                while ((len = in.read(bf)) > 0) {
                    out.write(bf, 0, len);
                }
                in.close();
                out.close();
                System.out.println("File Copied");

            }
        } catch (FileNotFoundException ex) {
            System.out
                    .println(ex.getMessage() + " in the specified directory.");
            return null;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return path1;
    }

    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (!constraint.equals("SHOWALLLLLL")) {
                    appList = (ArrayList<Appdata>) results.values;
                } else {
                    appList = new Global().getList();
                }
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new Filter.FilterResults();
                ArrayList<Appdata> FilteredArrayNames = new ArrayList<>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < appList.size(); i++) {
                    String dataNames = appList.get(i).getAppName();

                    if (dataNames.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        FilteredArrayNames.add(appList.get(i));
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }
        };

        return filter;
    }

    private static final <T> void runAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        asyncTask.execute(params);
    }
}
