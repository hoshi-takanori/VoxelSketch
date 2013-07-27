package com.example.surface;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import android.os.Environment;

public class Util {
    public static final void saveFile(final String filename, final String str) {
        // String filePath = Environment.getExternalStorageDirectory() + "/" +
        // filename;
        File path = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);
        file.getParentFile().mkdir();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(str);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkExternalStorageAvaiable() {
        boolean available = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            available  = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            available = true;
        } else {
            // Can't read or write
            available = false;
        }
        return available;
    }

    public static boolean checkExternalStorageWritable() {
        boolean writable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            writable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            writable = false;
        } else {
            // Can't read or write
            writable = false;
        }
        return writable;
    }
}
