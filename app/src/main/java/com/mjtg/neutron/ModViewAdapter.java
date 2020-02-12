package com.mjtg.neutron;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ModViewAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<ModDemo> list;
    public ModViewAdapter(Context ctx,ArrayList<ModDemo> list){
        this.ctx=ctx;

    }
    @Override
    public int getCount() {
        return list.size();
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
        View v= LayoutInflater.from(ctx).inflate(R.layout.mod_row,null);
        ImageView icon=v.findViewById(R.id.icon);
        TextView name=v.findViewById(R.id.name);
        TextView introduction=v.findViewById(R.id.introduction);
        icon.setImageBitmap(list.get(i).icon);
        name.setText(list.get(i).name);
        introduction.setText(list.get(i).introduction);
        return v;
    }
}
