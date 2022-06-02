package com.example.lspmt.hook;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.example.lspmt.hook.hooks.sms.Sms;
import com.example.lspmt.hook.hooks.telephony.Telephony;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {
    private final List<HookBase> mHookList;

    {
        mHookList = new ArrayList<>();

        mHookList.add(Sms.getInstance());
        mHookList.add(Telephony.getInstance());
    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        for (HookBase hook : mHookList) {
            hook.onLoadPackage(lpparam);
        }
    }
}
