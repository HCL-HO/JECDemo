package com.example.ericho.jecdemo;

import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ericho.jecdemo.camera.CameraActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {
/**
 *                              GRANT PERMISSIONS FIRST
 **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button imgBtn = findViewById(R.id.btn_img);
        final Button vidBtn = findViewById(R.id.btn_vid);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photo = new File(Environment.getExternalStorageDirectory() + "/" + "pic.jpg");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriFromFile(photo, MainActivity.this));
                startActivityForResult(cameraIntent, 2);
                //To support android 7+
                //https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
            }
        });

        vidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vidPath = Environment.getExternalStorageDirectory() + "/" + "vid.mp4";
                Intent vidIntent = new Intent(MainActivity.this, CameraActivity.class);
                vidIntent.putExtra(CameraActivity.TAG_FILEPATH, vidPath);
                startActivityForResult(
                        vidIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Proccess result...
    }
}
