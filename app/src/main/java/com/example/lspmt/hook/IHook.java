package com.example.lspmt.hook;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public interface IHook {
    String getPackPageName();

    String getAppName();


    void onLoadPackage(XC_LoadPackage.LoadPackageParam lpparam);

    boolean needHelpFindApplication();
}
