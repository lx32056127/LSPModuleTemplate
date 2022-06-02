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

package com.example.lspmt.hook.hooks.telephony;

import com.example.lspmt.hook.HookBase;
import com.example.lspmt.hook.hooks.telephony.hooks.TelephonyManager;

public class Telephony extends HookBase {

    static HookBase self = null;

    public static HookBase getInstance() {
        if (self == null)
            self = new Telephony();
        return self;
    }

    @Override
    public void hookLoadPackage() {
        try {
            TelephonyManager.init(utils);
        } catch (Throwable e) {
            utils.log("TelephonyManager HookError " + e.toString());
        }
    }


    @Override
    public String getPackPageName() {
        return "com.android.dialer";
    }

    @Override
    public String getAppName() {
        return "电话";
    }


    @Override
    public boolean needHelpFindApplication() {
        return true;
    }
}
