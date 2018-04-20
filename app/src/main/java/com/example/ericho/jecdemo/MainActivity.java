package com.example.ericho.jecdemo;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ericho.jecdemo.camera.CameraActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button imgBtn = findViewById(R.id.btn_img);
        final Button vidBtn = findViewById(R.id.btn_vid);

        // GRANT PERMISSIONS FIRST

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

}
