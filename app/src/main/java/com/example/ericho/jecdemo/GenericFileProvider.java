package com.example.ericho.jecdemo;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by ericho on 22/3/2018.
 */

public class GenericFileProvider extends FileProvider {

    /*If your targetSdkVersion >= 24, then we have to use FileProvider class to give access to the particular file or folder
      to make them accessible for other apps. We create our own class inheriting FileProvider
      in order to make sure our FileProvider doesn't conflict with FileProviders declared in imported dependencies
    * https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
    * */
    public static Uri getUriFromFile(File f, Context context) {
        return FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider",
                f);
    }
}
