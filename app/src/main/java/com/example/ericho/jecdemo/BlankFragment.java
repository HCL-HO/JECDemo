package com.example.ericho.jecdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ericho.jecdemo.camera.CameraActivity;

import java.io.File;


public class BlankFragment extends Fragment {


    private PermissionManager permissionManager;

    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance() {
        BlankFragment fragment = new BlankFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_blank1, container, false);
        Button imgBtn = view.findViewById(R.id.btn_img);
        Button vidBtn = view.findViewById(R.id.btn_vid);

        permissionManager = new PermissionManager(this);

        imgBtn.setOnClickListener(new View.OnClickListener() {

            PermissionManager.PermissionListener photoCallback = new PermissionManager.PermissionListener() {
                @Override
                public void onPermissionsGranted() {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photo = new File(Environment.getExternalStorageDirectory() + "/" + "pic.jpg");
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriFromFile(photo, getActivity()));
                    startActivityForResult(cameraIntent, 2);
                    //To support android 7+
                    //https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
                }
            };

            @Override
            public void onClick(View view) {
                if (permissionManager.requestCameraPermission(photoCallback)) {
                    photoCallback.onPermissionsGranted();
                }
            }
        });

        vidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vidPath = Environment.getExternalStorageDirectory() + "/" + "vid.mp4";
                Intent vidIntent = new Intent(getActivity(), CameraActivity.class);
                vidIntent.putExtra(CameraActivity.TAG_FILEPATH, vidPath);
                startActivityForResult(
                        vidIntent, 1);
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionManager.arePermissionGranted(grantResults)) {
            permissionManager.onPermissionsGranted(requestCode);
        }
    }
}
