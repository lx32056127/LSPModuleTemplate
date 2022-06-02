package com.example.lspmt.hook.hooks.sms;

import android.os.Build;

import com.example.lspmt.hook.HookBase;
import com.example.lspmt.hook.hooks.sms.hooks.PackageManagerServiceHook;
import com.example.lspmt.hook.hooks.sms.hooks.PermissionManagerServiceHook;
import com.example.lspmt.hook.hooks.sms.hooks.PermissionManagerServiceHook30;


public class Permission extends HookBase {
    static HookBase self = null;

    public static HookBase getInstance() {
        if (self == null)
            self = new Permission();
        return self;
    }

    @Override
    public void hookLoadPackage() {


        if (Build.VERSION.SDK_INT >= 30) { // Android 11+
            PermissionManagerServiceHook30.init(utils);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // Android 9.0~10
            PermissionManagerServiceHook.init(utils);
        } else { // Android 5.0 ~ 8.1
            PackageManagerServiceHook.init(utils);
        }
    }


    @Override
    public String getPackPageName() {
        return "android";
    }

    @Override
    public String getAppName() {
        return "安卓权限";
    }

    @Override
    public boolean needHelpFindApplication() {
        return true;
    }
}
