package com.mjtg.neutron;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    Button card_manage,card_launch,card_settings,card_shop;
    private String[] permissionsGroup = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this,TransService.class));

        card_manage=findViewById(R.id.button_manage);
        card_launch=findViewById(R.id.button_init);
        card_settings=findViewById(R.id.button_set);
        card_shop=findViewById(R.id.button_shop);

        card_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SelectActivity.class);
                startActivity(intent);
            }
        });
        RxPermissions permissions=new RxPermissions(this);
        permissions.request(permissionsGroup);

    }

}
