package com.example.lspmt.hook.hooks.sms;

import com.example.lspmt.hook.HookBase;
import com.example.lspmt.hook.hooks.sms.hooks.SmsIntent;


public class Sms extends HookBase {
    static HookBase self = null;

    public static HookBase getInstance() {
        if (self == null)
            self = new Sms();
        return self;
    }

    @Override
    public void hookLoadPackage() {
        try {
            SmsIntent.init(utils);
        } catch (Throwable e) {
            e.printStackTrace();
            utils.log(e.toString());
        }
    }


    @Override
    public String getPackPageName() {
        return "com.android.phone";
    }

    @Override
    public String getAppName() {
        return "短信";
    }


    @Override
    public boolean needHelpFindApplication() {
        return true;
    }


}
