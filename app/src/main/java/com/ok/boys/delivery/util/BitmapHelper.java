package com.ok.boys.delivery.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.ok.boys.delivery.chunking.ImageManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class BitmapHelper {

    /**
     * Bitmap to base64 string
     */
    public static String bitmapToBaseString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }


    /**
     * Get real path from uri
     */


    /**
     * Convert Uri to Bitmap
     */
    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }

    /**
     * Convert file path to Bitmap
     */
    public static Bitmap decodePathToBitmap(Context mContext, String sendUri) {
        Bitmap getBitmap = null;
        try {
            getBitmap = BitmapFactory.decodeFile(sendUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }





    /**
     * Save Bitmap
     */
    public static String saveCompressImage(Bitmap image) {
        String returnPath = "";
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.e("compress", "Error creating media file, check storage permissions: ");
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            returnPath = pictureFile.getPath();
        } catch (FileNotFoundException e) {
            Log.e("compress", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e("compress", "Error accessing file: " + e.getMessage());
        }
        return returnPath;
    }


    public static File getOutputMediaFile() {
        String newFileName = "Invoice_" + System.currentTimeMillis() + ".png";
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "OkBoys");
        if (!mediaStorageDir.isDirectory()) {
            mediaStorageDir.mkdir();
        }
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + newFileName);
        return mediaFile;
    }

    /*public static void mediaScanner(Context mContext,String path){
        MediaScannerConnection.scanFile(mContext,
                new String[] { path }, null, (path1, uri) -> {
                    Log.i("ExternalStorage", "Scanned " + path1 + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                });
    }*/

    /**
     * Resize Bitmap
     */
    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static String getRealPath(Context context, String path) {
        Bitmap decodeUriToBitmap = decodePathToBitmap(context, path);
        Bitmap resizedBitmap = getResizedBitmap(decodeUriToBitmap, 420);
        return saveCompressImage(resizedBitmap);
    }

    public static Bitmap getRealPathBitmap(Context context, String path) {
        Bitmap decodeUriToBitmap = decodePathToBitmap(context, path);
        return getResizedBitmap(decodeUriToBitmap, 420);
    }

    public static Bitmap getChunkPathBitmap(Context context, String path) {
        Bitmap decodeUriToBitmap = decodePathToBitmap(context, path);
        return getResizedBitmap(decodeUriToBitmap, ImageManager.IMAGE_QUALITY_MEDIUM);
    }
}