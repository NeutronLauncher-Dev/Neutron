package com.mjtg.neutron;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class FileViewAdapter extends BaseAdapter {
    File dir;
    Context ctx;
    public FileViewAdapter(Context ctx,String path){
        this.ctx=ctx;
        dir=new File(path);
    }
    @Override
    public int getCount() {
        return dir.list().length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    @SuppressLint("ViewHolder")
    public View getView(int i, View view, ViewGroup viewGroup) {
        File[] files=dir.listFiles();
        View v= LayoutInflater.from(ctx).inflate(R.layout.file_row,null);
        TextView tv=v.findViewById(R.id.name);
        ImageView icon=v.findViewById(R.id.icon);
        tv.setText(files[i].getName());

        if(files[i].isDirectory())
            icon.setImageResource(R.drawable.folder);
        else{
            String[] ss=files[i].getName().split("\\.");
            String s=ss[ss.length-1];
            if(s.equals("jar"))
                icon.setImageResource(R.drawable.pack);
            else
                icon.setImageResource(R.drawable.file);
        }
        return v;
    }
}
