/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.SetupWizardBrowser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;



public class SetupWizardBrowser extends BroadcastReceiver {

    private static final String TAG = "SetupWizardBrowser";
    private static final String ACTION_DEVICE_INITIALIZATION_WIZARD = "com.google.android.setupwizard.SETUP_WIZARD_FINISHED";
    private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private ConnectivityManager manager = null;
    private SharedPreferences sp = null;
    private SharedPreferences.Editor editor = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"lfk ---ACTION--->" + intent.getAction());
        sp = context.getSharedPreferences("setupwizard", 0);
        editor = sp.edit();
        if (ACTION_BOOT_COMPLETED.equals(intent.getAction()) ){
                Log.d(TAG, "ACTION_BOOT_COMPLETED--->");
                boolean isFinish = sp.getBoolean("isFinished", true);
                Log.d("lfk","isFinish =="+isFinish);
                if (!isFinish) {
                    Log.d("lfk","isFinish =="+isFinish);
                    if (checkNetworkState(context)) {
                        Log.d("lfk","checkNetworkState(context) =="+checkNetworkState(context));
                        Intent webIntent = new Intent(context,MainActivity.class);
                        webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(webIntent);
                        editor.putBoolean("isFinished", true);
                        editor.commit();
                    }
                }
            }

        if(ACTION_DEVICE_INITIALIZATION_WIZARD.equals(intent.getAction())){
                Log.d(TAG,"ACTION_DEVICE_INITIALIZATION_WIZARD--->");
               if(checkNetworkState(context)){
                    Intent webIntent = new Intent(context,MainActivity.class);
                    webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(webIntent);
                }else{
                    editor.putBoolean("isFinished", false);
                    editor.commit();
                }
            }
    }

    private boolean checkNetworkState(Context context) {
        boolean flag = false;
        manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }
}
