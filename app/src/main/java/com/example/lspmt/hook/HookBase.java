package com.example.lspmt.hook;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;

import com.example.lspmt.utils.MultiprocessSharedPreferences;
import com.example.lspmt.utils.Utils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class HookBase implements IHook {
    protected ClassLoader mAppClassLoader;
    protected WeakReference<Context> mContext = new WeakReference<>(null);
    protected String TAG = "Auto-AppHook";
    protected Utils utils;

    @Override
    protected void finalize() throws Throwable {
        mContext = new WeakReference<>(null);
        mAppClassLoader = null;
        System.setProperty("AUTO_FULL_TAG_" + getClass().getSimpleName(), "false");
    }

    private void hookMainInOtherAppContext(Runnable runnable) {
        Runnable findContext1 = new Runnable() {
            @Override
            public void run() {
                XposedHelpers.findAndHookMethod(ContextWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        mContext = new WeakReference<>((Context) param.args[0]);
                        mAppClassLoader = mContext.get().getClassLoader();
                        runnable.run();
                    }
                });
            }
        };
        Runnable findContext2 = new Runnable() {
            @Override
            public void run() {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        mContext = new WeakReference<>((Context) param.args[0]);
                        mAppClassLoader = mContext.get().getClassLoader();
                        runnable.run();
                    }
                });
            }
        };
        try {
            findContext1.run();
        } catch (Throwable e) {
            findContext2.run();
        }


    }


    public void initLoadPackage(String pkg) {
        MultiprocessSharedPreferences.setAuthority("com.example.lspmt.provider");
        if ("true".equals(System.getProperty("AUTO_FULL_TAG_" + getClass().getSimpleName()))) {
            // XposedBridge.log("?????????????????????hook??????");
            //I don't know... What happened?
            return;
        }

        System.setProperty("AUTO_FULL_TAG_" + getClass().getSimpleName(), "true");
        utils = new Utils(mContext.get(), mAppClassLoader, getAppName(), getPackPageName());
        XposedBridge.log(" LSPMT???????????????????????????:" + utils.getAppName() + "  ???????????????:" + utils.getVerCode() + "  ??????????????????" + utils.getVerName());
        try {
            hookLoadPackage();
        } catch (Error | Exception e) {
            utils.log("hook ?????????????????????" + e.toString(), true);
        }

    }

    public abstract void hookLoadPackage();

    @Override
    public void onLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        String pkg = lpparam.packageName;
        String processName = lpparam.processName;
        if (!lpparam.isFirstApplication) return;
        if (getPackPageName() != null) {
            if (!pkg.equals(getPackPageName()) || !processName.equals(getPackPageName())) return;
        }
        // if (hookLoadPackage > getHookIndex()) return;

        mAppClassLoader = lpparam.classLoader;
        mContext = new WeakReference<>(AndroidAppHelper.currentApplication());
        if (!needHelpFindApplication()) {
            initLoadPackage(pkg);
            return;
        }
        hookMainInOtherAppContext(() -> initLoadPackage(pkg));
    }


    /**
     * ??????????????????Hook??????
     *
     * @param flag ????????????,????????????Hook????????????????????????
     * @return ??????????????????Hook??????
     */
    private boolean isInject(String flag) {
        try {
            if (TextUtils.isEmpty(flag)) return false;
            Field methodCacheField = XposedHelpers.class.getDeclaredField("methodCache");
            methodCacheField.setAccessible(true);
            HashMap<String, Method> methodCache = (HashMap<String, Method>) methodCacheField.get(null);
            Method method = XposedHelpers.findMethodBestMatch(Application.class, "onCreate");
            String key = String.format("%s#%s", flag, method.getName());
            if (methodCache.containsKey(key)) return true;
            methodCache.put(key, method);
            return false;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean canUseCache() {
        try {
            XposedHelpers.class.getDeclaredField("methodCache");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
