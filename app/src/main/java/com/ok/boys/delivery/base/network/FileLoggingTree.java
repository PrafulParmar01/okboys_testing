package com.ok.boys.delivery.base.network;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

public class FileLoggingTree extends Timber.DebugTree {

    private static final String LOG_TAG = FileLoggingTree.class.getSimpleName();
    private Context context;
    public FileLoggingTree(Application context) {
        super();
        this.context = context;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        try {
            String path = "Log";
            String logTimeStamp = new SimpleDateFormat("E MMM dd yyyy 'at' hh:mm:ss:SSS aaa", Locale.getDefault()).format(new Date());
            String fileName = "OkBoysErrorLogs" + ".html";

            // Create file
            File file  = generateFile(path, fileName, context);

            // If file created or exists save logs
            if (file != null) {
                FileWriter writer = new FileWriter(file, true);
                writer.append("<p style=\"background:lightgray;\"><strong "
                        + "style=\"background:lightblue;\">&nbsp&nbsp")
                        .append(logTimeStamp)
                        .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
                        .append(tag)
                        .append("</strong> - ")
                        .append(message)
                        .append("</p>");
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG,"Error while logging into file : " + e);
        }
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return super.createStackElementTag(element) + " - " + element.getLineNumber();
    }

    /*  Helper method to create file*/
    @Nullable
    private static File generateFile(@NonNull String path, @NonNull String fileName, Context context) {
        File file = null;
        if (isExternalStorageAvailable()) {
            File root = new File(Objects.requireNonNull(context.getExternalCacheDir()).getAbsolutePath(), path);

            boolean dirExists = true;

            if (!root.exists()) {
                dirExists = root.mkdirs();
            }

            if (dirExists) {
                file = new File(root, fileName);
            }
        }
        return file;
    }

    private static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}