package com.mjtg.neutron;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class SelectActivity extends AppCompatActivity {

    ListView list;
    TextView addModText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        GData.selectActivity=new WeakReference<>(this);

        list=findViewById(R.id.mod_list);
        addModText=findViewById(R.id.add_mod);

        addModText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SelectActivity.this,FileActivity.class);
                startActivity(intent);
            }
        });

        updateList();

    }
    public void addMod(ModDemo md){
        GData.modDemoList.add(0,md);
        updateList();
    }
    public void removeMod(int index){
        GData.modDemoList.remove(index);
        updateList();

    }
    public void updateList(){
        list.setAdapter(new ModViewAdapter(this,GData.modDemoList));
    }
}
