package com.example.ericho.jecdemo;

import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ericho.jecdemo.camera.CameraActivity;
import com.example.ericho.jecdemo.permission.PermissionManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = BlankFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.page_content, fragment).commit();

    }

}
