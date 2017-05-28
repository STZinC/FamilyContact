/**************************************************************************************************
 * Copyright (C) 2016 WickerLabs. inc - All Rights Reserved.                                      *
 *                                                                                                *
 * NOTICE:  All information contained herein is, and remains the property of WickerLabs,          *
 * The intellectual and technical concepts contained herein are proprietary to WickerLabs.        *
 * Dissemination of this information or reproduction of this material                             *
 * is strictly forbidden unless prior permission is obtained from WickerLabs. inc                 *
 *                                                                                                *
 **************************************************************************************************/
package com.example.calls.Adapter;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by littl on 2017/5/26.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
//usage
//HttpUtil.sendOkHttpRequest("URLADDR", new okhttp3.Callback(){
//        @Override
//            public void onResponse(Call call, Response response)throws IOException{
//            String responseData = response.Body().String();
//        }
//        @Override
//        public void onFailure(Call call, IOException e)
//        })