package com.example.ericho.jecdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

/**
 * Created by ebsl on 19/1/2018.
 */

public class PermissionManager {

    public static final int REQUEST_CAMERA = 0;
    public static final int REQUEST_STORAGE = 2;
    public static final int REQUEST_ALL = 9;
    public static final int REQUEST_VIDEO = 12;
    public static final int REQUEST_PHONE = 121;

    public interface PermissionListener {
        void onPermissionsGranted();
    }

    private Fragment frag;

    public PermissionManager(Fragment frag) {
        this.frag = frag;
    }

    public boolean requestPermission() {
        List<String> permissions = new ArrayList<>();
        if (!permissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionGranted(Manifest.permission.RECORD_AUDIO)) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!permissionGranted(Manifest.permission.CAMERA)) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (!permissionGranted(Manifest.permission.CALL_PHONE)) {
            permissions.add(Manifest.permission.CALL_PHONE);
        }
        if (permissions.size() > 0) {
            frag.requestPermissions(permissions.toArray(new String[permissions.size()]),
                    REQUEST_ALL);
            return false;
        } else {
            return true;
        }
    }

    public boolean requestCameraPermission() {
        List<String> permissions = new ArrayList<>();
        if (!permissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionGranted(Manifest.permission.CAMERA)) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (permissions.size() > 0) {
            frag.requestPermissions(permissions.toArray(new String[permissions.size()]),
                    REQUEST_CAMERA);
            return false;
        } else {
            return true;
        }
    }

    public boolean requestCameraPermission(PermissionListener listener) {
        registerCallback(listener, REQUEST_CAMERA);
        return requestCameraPermission();
    }

    public boolean requestPhonePermission() {
        List<String> permissions = new ArrayList<>();
        if (!permissionGranted(Manifest.permission.CALL_PHONE)) {
            permissions.add(Manifest.permission.CALL_PHONE);
        }
        if (permissions.size() > 0) {
            frag.requestPermissions(permissions.toArray(new String[permissions.size()]),
                    REQUEST_PHONE);
            return false;
        } else {
            return true;
        }
    }

    public boolean requestPhonePermission(PermissionListener listener) {
        registerCallback(listener, REQUEST_CAMERA);
        return requestPhonePermission();
    }

    public boolean requestVideoPermission() {
        List<String> permissions = new ArrayList<>();
        if (!permissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionGranted(Manifest.permission.RECORD_AUDIO)) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!permissionGranted(Manifest.permission.CAMERA)) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (permissions.size() > 0) {
            frag.requestPermissions(permissions.toArray(new String[permissions.size()]),
                    REQUEST_VIDEO);
            return false;
        } else {
            return true;
        }
    }

    public boolean requestVideoPermission(PermissionListener listener) {
        registerCallback(listener, REQUEST_CAMERA);
        return requestVideoPermission();
    }

    public boolean requestStoragePermission() {
        if (!permissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            frag.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
            return false;
        } else {
            return true;
        }
    }

    public boolean requestStoragePermission(PermissionListener listener) {
        registerCallback(listener, REQUEST_CAMERA);
        return requestStoragePermission();
    }

    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(frag.getActivity(),
                permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean arePermissionGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result == PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    //Register when request permission
    //Unregister / call onPermissionsGranted on request result
    private SparseArray<PermissionListener> listenerSparseArray = new SparseArray<>();

    public void registerCallback(PermissionListener listener, int requestCamera) {
        listenerSparseArray.put(requestCamera, listener);
    }

    public void unregisterListener(int requestCode) {
        listenerSparseArray.remove(requestCode);
    }

    public void onPermissionsGranted(int requestCode) {
        PermissionListener listenerInstance = listenerSparseArray.get(requestCode);
        if (listenerInstance != null) {
            listenerInstance.onPermissionsGranted();
            unregisterListener(requestCode);
        }
    }
}
