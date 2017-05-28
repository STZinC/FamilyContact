/**************************************************************************************************
 * Copyright (C) 2016 WickerLabs. inc - All Rights Reserved.                                      *
 *                                                                                                *
 * NOTICE:  All information contained herein is, and remains the property of WickerLabs,          *
 * The intellectual and technical concepts contained herein are proprietary to WickerLabs.        *
 * Dissemination of this information or reproduction of this material                             *
 * is strictly forbidden unless prior permission is obtained from WickerLabs. inc                 *
 *                                                                                                *
 **************************************************************************************************/
package com.example.calls;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.calls.Adapter.LogsAdapter;
import com.example.calls.Adapter.PeopleAdapter;
import com.wickerlabs.logmanager.LogObject;
import com.wickerlabs.logmanager.LogsManager;

import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class FragmentCalllogs extends Fragment {
    //设置读取log数量的上限检查权限等
    private static final int READ_LOGS = 100;
    private List<LogObject> callLogs;
    private RecyclerView logListView;
    private LogsAdapter adapter;
    private Runnable logsRunnable;
    private String[] requiredPermissions = {Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE};
    private View view;
    private static final String TAG = "FragmentCalllogs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_calllogs,container,false);

        logsRunnable = new Runnable() {
            @Override
            public void run() {
                loadLogs();
            }
        };

        // Checking for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionToExecute(requiredPermissions, READ_LOGS, logsRunnable);
        } else {
            logsRunnable.run();
        }
        return view;
    }


    // This is to be run only when READ_CONTACTS and READ_CALL_LOG permission are granted
    private void loadLogs() {

        LogsManager logsManager = new LogsManager(getActivity());
        callLogs = logsManager.getLogs(LogsManager.ALL_CALLS);
        logListView = (RecyclerView) view.findViewById(R.id.LogsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        logListView.setLayoutManager(layoutManager);
        adapter = new LogsAdapter(callLogs);
        logListView.setAdapter(adapter);

    }

    @Override
    public void onResume(){
        super.onResume();
        logListView = (RecyclerView) view.findViewById(R.id.LogsList);
        logsRunnable.run();

    }

    // A method to check if a permission is granted then execute tasks depending on that particular permission
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute(String permissions[], int requestCode, Runnable runnable) {

        boolean logs = ContextCompat.checkSelfPermission(getActivity(), permissions[0]) != PackageManager.PERMISSION_GRANTED;
        boolean contacts = ContextCompat.checkSelfPermission(getActivity(), permissions[1]) != PackageManager.PERMISSION_GRANTED;
        boolean call = ContextCompat.checkSelfPermission(getActivity(), permissions[2]) != PackageManager.PERMISSION_GRANTED;
        if (logs || contacts|| call) {
            requestPermissions(permissions, requestCode);
        } else {
            runnable.run();
        }
    }


    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_LOGS && permissions[0].equals(Manifest.permission.READ_CALL_LOG) && permissions[1].equals(Manifest.permission.READ_CONTACTS)&&
                permissions[2].equals(Manifest.permission.CALL_PHONE)) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED && grantResults[1] == PermissionChecker.PERMISSION_GRANTED && grantResults[2] == PermissionChecker.PERMISSION_GRANTED) {
                logsRunnable.run();
            } else {
                new AlertDialog.Builder(getActivity())
                        .setMessage("The app needs these permissions to work, Exit?")
                        .setTitle("Permission Denied")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                checkPermissionToExecute(requiredPermissions, READ_LOGS, logsRunnable);
                            }
                        })
                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    }

}
