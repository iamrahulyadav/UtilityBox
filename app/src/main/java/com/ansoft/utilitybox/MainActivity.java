package com.ansoft.utilitybox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.chrisplus.rootmanager.RootManager;
import com.scottyab.rootbeer.RootBeer;

public class MainActivity extends AppCompatActivity{



    Button getRootAccBtn;
    Button getIDSBtn;
    Button appListBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getRootAccBtn=(Button)findViewById(R.id.button1);
        getRootAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RootBeer rootBeer = new RootBeer(getApplicationContext());
                if(rootBeer.isRooted()){
                   if ( RootManager.getInstance().obtainPermission()){
                       toastMessage("Root access has been granted!");
                   }else{
                       toastMessage("Root access was not granted!");
                   }
                }else{
                    toastMessage("Your phone is not rooted!");
                }
            }
        });

        getIDSBtn=(Button)findViewById(R.id.button3);
        getIDSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MobileIDSActivity.class));
            }
        });

        appListBtn=(Button)findViewById(R.id.button2);
        appListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ApplistActivity.class));
            }
        });
    }

    public void toastMessage(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }


}
