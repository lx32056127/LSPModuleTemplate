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

package com.example.lspmt.hook.hooks.telephony.hooks;

import android.content.Context;
import android.widget.Toast;

import com.example.lspmt.utils.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class TelephonyManager {
    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Context context = utils.getContext();
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", mAppClassLoader, "getDeviceId", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String imei = (String) param.getResult();
                utils.log("getDeviceId.result: " + imei);
                Toast.makeText(context, "Real IMEI: " + imei, Toast.LENGTH_SHORT).show();
                param.setResult("888888888888888");
            }
        });
    }
}
