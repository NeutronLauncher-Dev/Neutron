package com.mjtg.neutron;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

public class FileActivity extends AppCompatActivity {
    File dir;
    ListView list;
    TextView pathView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        dir= Environment.getExternalStorageDirectory();
        list=findViewById(R.id.file_list);
        pathView=findViewById(R.id.path);

        toPath();
    }
    public void toPath(){
        FileViewAdapter fva=new FileViewAdapter(this,dir.getPath());
        list.setAdapter(fva);
        pathView.setText(dir.getPath());
    }
    public void upPath(){
        dir=dir.getParentFile();
        toPath();
    }
}
