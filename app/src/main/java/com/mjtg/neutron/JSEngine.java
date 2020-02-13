package com.mjtg.neutron;


import android.content.Context;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;

public class JSEngine {
    private Class clazz;
    private String allFunctions = "";//js方法语句
    private Context ctx;
    public JSEngine(Context ctx) {
        this.clazz = JSEngine.class;
        initJSStr("pre.js");//初始化js语句
    }

    private void initJSStr(String filename) {
        this.ctx=ctx;
        allFunctions = " var ScriptAPI = java.lang.Class.forName(\"" + JSEngine.class.getName() + "\", true, javaLoader);\n"+FileUntil.readAssets(ctx,filename);
    }

    //本地java方法
    public void setValue(Object keyStr, Object o) {
        System.out.println("JSEngine output - setValue : " + keyStr.toString() + " ------> " + o.toString());
    }

    //本地java方法
    public String getValue(String keyStr) {
        System.out.println("JSEngine output - getValue : " + keyStr.toString());
        return "获取到值了";
    }

    public void runScript(String js) {
        String runJSStr = allFunctions + "\n" + js;//运行js = allFunctions + js
        org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();
        rhino.setOptimizationLevel(-1) ;
        try {
            Scriptable scope = rhino.initStandardObjects();

            ScriptableObject.putProperty(scope, "javaContext", org.mozilla.javascript.Context.javaToJS(this, scope));//配置属性 javaContext:当前类JSEngine的上下文
            ScriptableObject.putProperty(scope, "javaLoader", org.mozilla.javascript.Context.javaToJS(clazz.getClassLoader(), scope));//配置属性 javaLoader:当前类的JSEngine的类加载器

            rhino.evaluateString(scope, runJSStr, clazz.getSimpleName(), 1, null);
        } finally {
            org.mozilla.javascript.Context.exit();
        }
    }
}