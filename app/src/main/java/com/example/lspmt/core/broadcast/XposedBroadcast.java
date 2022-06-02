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

package com.example.lspmt.core.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class XposedBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        if (action.equals("com.example.lspmt.XPOSED_LOG")) {
            Bundle extData = intent.getExtras();
            if (extData == null) return;
            String tag = extData.getString("tag");
            String msg = extData.getString("msg");
            Log.i("Xposed Log-" + tag, msg);
        } else if (action.equals("com.example.lspmt.XPOSED")) {
            Bundle extData = intent.getExtras();
            if (extData == null) return;
            String data = extData.getString("data").replace("\t", "").replace("\n", "n");
            String identify = extData.getString("app_identify");
            String app = extData.getString("app_package");
            String appName = extData.getString("app_name");
            Log.i("Xposed - LSPMT", "LSPMT Recivier Data：AppName: " + appName + ", form: " + app + ", data: " + data);
        }
    }
}
