/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.lspmt.utils;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.lspmt.BuildConfig;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

public class Utils {
    public static final String SEND_ACTION = "com.example.lspmt.XPOSED";
    public static final String SEND_LOG_ACTION = "com.example.lspmt.XPOSED_LOG";
    public static final String SEND_ACTION_APP = "com.example.lspmt.APP";
    private Context mContext;
    private final ClassLoader mAppClassLoader;
    private final String appName;
    private final String packageName;

    public Utils(Context context, ClassLoader classLoader, String name, String packageName) {
        mContext = context;
        mAppClassLoader = classLoader;
        appName = name;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public ClassLoader getClassLoader() {
        return mAppClassLoader;
    }

    public Context getContext() {
        if (mContext == null) {
            mContext = AndroidAppHelper.currentApplication();
        }
        return mContext;
    }

    public void writeData(String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("LSPMT_xp", Context.MODE_PRIVATE); //????????????

        SharedPreferences.Editor editor = sharedPreferences.edit();//???????????????

        editor.putString(key, value);

        editor.apply();//????????????
    }


    public String readData(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("LSPMT_xp", Context.MODE_PRIVATE); //????????????
        return sharedPreferences.getString(key, "");
    }


    //JSON?????????URL
    public static String convert(JSONObject object) {
        String buff = object.toString();
        buff = buff.replace("{", "")
                .replace("}", "")
                .replace(":\"", ":")
                .replace("\"", "")
                .replace("\\", "");
        return buff.replaceAll("\\r\\n|\\r|\\n", "");
    }


    public void send(JSONObject jsonObject) {
        if (jsonObject == null) return;
        XposedBridge.log("???????????????" + jsonObject.toString());
        Bundle bundle = new Bundle();
        bundle.putString("data", convert(jsonObject));
        send(bundle, "app");
    }

    public void sendString(String str) {
        sendString(str, "app", getPackageName());
    }

    public void sendString(String str, String identify, String packageName) {
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        bundle.putString("app_identify", identify);
        log("?????????LSPMT???" + str, true);
        if (packageName == null)
            sendBroadcast(SEND_ACTION, bundle);
        else sendBroadcast(SEND_ACTION, bundle, packageName);
    }

    public void send2auto(String str) {
        log("APP???????????????LSPMT???" + str, true);
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        sendBroadcast(SEND_ACTION_APP, bundle);
    }

    private void sendBroadcast(String Action, Bundle bundle, String packageName) {
        bundle.putString("app_package", packageName);
        Intent intent = new Intent(Action);
        intent.setPackage(BuildConfig.APPLICATION_ID);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        try {
            if (mContext != null) {
                getContext().sendBroadcast(intent, null);
            } else {
                XposedBridge.log("content???????????????????????????:" + bundle.toString());
            }
        } catch (Throwable e) {
            XposedBridge.log("?????????" + e.toString());
            XposedBridge.log("???????????????" + bundle.toString());
        }

    }

    private void sendBroadcast(String Action, Bundle bundle) {
        sendBroadcast(Action, bundle, getPackageName());
    }

    /**
     * ???????????????
     */
    public String getVerName() {
        String verName = "";
        try {
            verName = getContext().getPackageManager().
                    getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (Exception ignored) {

        }
        return verName;
    }

    /**
     * ??????app???????????????
     *
     * @return
     */
    public int getVerCode() {
        int verName = 0;
        try {
            verName = getContext().getPackageManager().
                    getPackageInfo(getContext().getPackageName(), 0).versionCode;
        } catch (Exception ignored) {

        }
        return verName;
    }

    /**
     * ???????????????????????????
     */

    public boolean isDebug() {
        return readDataByBoolean("apps", "debug");
    }

    /**
     * ????????????
     *
     * @param msg ????????????
     */
    public void log(String... msg) {
        StringBuilder m = new StringBuilder();
        for (String mm : msg) {
            m.append(mm).append(",");
        }
        if (!m.toString().equals("")) {
            m = new StringBuilder(m.substring(0, m.length() - 1));
        }
        log(m.toString(), true);
    }

    /**
     * ????????????
     *
     * @param msg ????????????
     * @param xp  ???????????????xposed
     */
    public void log(String msg, boolean xp) {
        XposedBridge.log("LSPMT-" + appName + " -> " + msg);
        //?????????LSPMT??????
        //  Log.i("LSPMT-" + appName, msg);
        // ?????????????????? ?????????????????????
        if (msg.length() > 4000) {
            for (int i = 0; i < msg.length(); i += 4000) {
                if (i + 4000 < msg.length()) {
                    Log.i("LSPMT-" + appName + " ???" + i + "??????", msg.substring(i, i + 4000));
                } else {
                    Log.i("LSPMT-" + appName + " ???" + i + "??????", msg.substring(i));
                }
            }
        } else {
            Log.i("LSPMT-" + appName + " ????????????", "************************  response = " + msg);
        }
        Bundle bundle = new Bundle();
        bundle.putString("tag", "LSPMT-" + appName);
        bundle.putString("msg", msg);
        sendBroadcast(SEND_LOG_ACTION, bundle);
    }

    public Boolean readDataByBoolean(String app, String name) {
        MultiprocessSharedPreferences.setAuthority("com.example.lspmt.provider");
        SharedPreferences data = MultiprocessSharedPreferences.getSharedPreferences(getContext(), app, Context.MODE_PRIVATE);
        return data.getBoolean(name, false);
    }

    public void writeDataByData(String app, String name, String data) {
        MultiprocessSharedPreferences.setAuthority("com.example.lspmt.provider");
        SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(getContext(), app, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(name, data).apply();
    }

    public String readDataByApp(String app, String name) {
        MultiprocessSharedPreferences.setAuthority("com.example.lspmt.provider");
        SharedPreferences data = MultiprocessSharedPreferences.getSharedPreferences(getContext(), app, Context.MODE_PRIVATE);

        return data.getString(name, "");
    }

    public void dumpFields(Object classObj, Class<?> dumpClass) {
        log("????????????????????????????????????" + dumpClass.getSimpleName());
        try {
            Field[] fields = dumpClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                log(field.getName(), String.valueOf(field.get(classObj)));
            }
            log("dump?????????");
        } catch (Exception e) {
            log("dump?????????", e.toString());
        }
    }

    public void dumpFields(Object classObj) {
        dumpFields(classObj, classObj.getClass());
    }

    // ?????????????????????????????????????????????????????????????????????????????????
    public void dumpClass(Class<?> actions) {
        XposedBridge.log("[LSPMT]" + "Dump class " + actions.getName());
        XposedBridge.log("[LSPMT]" + "Methods");
        // ??????????????????????????????????????????????????????
        Method[] m = actions.getDeclaredMethods();
        // ?????????????????????????????????????????????
        for (Method method : m) {
            XposedBridge.log(method.toString());
        }
        XposedBridge.log("[LSPMT]" + "Fields");
        // ??????????????????????????????????????????????????????
        Field[] f = actions.getDeclaredFields();
        // ???????????????????????????????????????
        for (Field field : f) {
            XposedBridge.log("[LSPMT]" + field.toString());
        }
        XposedBridge.log("[LSPMT]Classes");
        // ????????????????????????????????????????????????????????????
        Class<?>[] c = actions.getDeclaredClasses();
        // ??????????????????????????????????????????
        for (Class<?> aClass : c) {
            XposedBridge.log("[LSPMT]" + aClass.toString());
        }

    }


    public void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    public void send(Bundle bundle, String identify) {
        bundle.putString("app_identify", identify);
        log("?????????LSPMT???" + bundle.toString(), true);
        sendBroadcast(SEND_ACTION, bundle);
    }

    public void setContext(Context context) {
        mContext = context;
    }
}

