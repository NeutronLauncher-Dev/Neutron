package com.mjtg.neutron;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class GData {
    public static ArrayList<ModDemo> modDemoList=new ArrayList<>();
    public static WeakReference<SelectActivity> selectActivity;
    public static WeakReference<FileActivity> fileActivity;

    public static void showAlert(Context ctx,View parent,String content, View.OnClickListener ifyes){
        PopupWindow pw=new PopupWindow(ctx);
        View v= LayoutInflater.from(ctx).inflate(R.layout.alert,null);
        TextView contentText=v.findViewById(R.id.content),
                yesText=v.findViewById(R.id.yes);
        contentText.setText(content);
        yesText.setOnClickListener(ifyes);

        pw.setWidth(-2);
        pw.setHeight(-2);
        pw.setFocusable(false);
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pw.showAtLocation(parent, Gravity.CENTER,0,0);
        pw.setContentView(v);
    }
    public static ModDemo decodeModFromPath(String path) {
        ModDemo md=new ModDemo();
        String introduction="F2L";
        String name="F2L";
        String s=File.separator;
        File file=new File(path);
        md.path=path;
        try {
            FileUntil.zipUncompress(path,file.getParent()+s+"temp"+s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        md.icon= BitmapFactory.decodeFile(file.getParent()+s+"temp"+s+"icon.png");
        try {
            JSONObject json=new JSONObject(FileUntil.readFile(file.getParent()+s+"temp"+s+"info.json"));
            introduction=json.getString("introduction");
            name=json.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        md.introduction=introduction;
        md.name=name;
        FileUntil.deletefile(file.getParent()+s+"temp");
        return md;
    }
}
