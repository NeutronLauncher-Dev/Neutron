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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final File[] files=dir.listFiles();

        final FileActivity fa= GData.fileActivity.get();
        final SelectActivity sa=GData.selectActivity.get();
        View v= LayoutInflater.from(ctx).inflate(R.layout.file_row,null);
        final TextView tv=v.findViewById(R.id.name);
        ImageView icon=v.findViewById(R.id.icon);
        tv.setText(files[i].getName());

        if(files[i].isDirectory()) {
            icon.setImageResource(R.drawable.folder);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fa.dir=files[i];
                    fa.toPath();
                }
            });
        }
        else{
            String[] ss=files[i].getName().split("\\.");
            String s=ss[ss.length-1];
            if(s.equals("neu")) {
                icon.setImageResource(R.drawable.pack);
                GData.showAlert(ctx, fa.getWindow().getDecorView(), "你确定添加此模组吗", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sa.addMod(GData.decodeModFromPath(files[i].getPath()));
                    }
                });
            }
            else
                icon.setImageResource(R.drawable.file);
        }
        return v;
    }
}
